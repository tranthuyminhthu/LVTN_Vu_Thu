from flask import Flask, request, jsonify
from PIL import Image
import torch
import torch.nn as nn
from torchvision import models, transforms
import numpy as np
import io
import os
from pymongo import MongoClient
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from datetime import datetime
import requests

app = Flask(__name__)

mongo_client = MongoClient('mongodb://admin:password@localhost:27017/')
db = mongo_client.recommendation_db
products_collection = db.products

INDEX_TO_CATEGORY_ID = {0: 1, 1: 2, 2: 7, 3: 8, 4: 9}
CATEGORY_ID_TO_INDEX = {v: k for k, v in INDEX_TO_CATEGORY_ID.items()}

CATEGORY_MAPPING = {
    1: "short sleeve top",
    2: "long sleeve top",
    7: "short",
    8: "trousers",
    9: "skirt"
}
CATEGORY_VIETNAMESE = {
    1: "áo thun tay ngắn",
    2: "áo thun tay dài",
    7: "quần short",
    8: "quần dài",
    9: "váy ngắn"
}
CATEGORY_FULL_MAPPING = {
    0: {"category_id": 1, "en": "short sleeve top", "vi": "áo thun tay ngắn"},
    1: {"category_id": 2, "en": "long sleeve top", "vi": "áo thun tay dài"},
    2: {"category_id": 7, "en": "short", "vi": "quần short"},
    3: {"category_id": 8, "en": "trousers", "vi": "quần dài"},
    4: {"category_id": 9, "en": "skirt", "vi": "váy ngắn"}
}

def init_database():
    try:
        if 'products' not in db.list_collection_names():
            db.create_collection('products')
            print("Created products collection")
        
        products_collection.create_index([("name", 1)])
        products_collection.create_index([("created_at", 1)])
        print("Created indexes")
        
        try:
            db.command({
                "createUser": "recommend_user",
                "pwd": "recommend_password",
                "roles": [{"role": "readWrite", "db": "recommendation_db"}]
            })
            print("Created recommend_user")
        except Exception as e:
            if "already exists" not in str(e):
                print(f"Error creating user: {e}")
        
        print("Database initialization completed!")
    except Exception as e:
        print(f"Error initializing database: {e}")

init_database()

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Using device: {device}")

data_transforms = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
])

def remove_module_prefix(state_dict):
    new_state_dict = {}
    for key, value in state_dict.items():
        new_key = key.replace("module.", "") if key.startswith("module.") else key
        new_state_dict[new_key] = value
    return new_state_dict

import torch.nn as nn
from torchvision import models

class ResNet50Classifier(nn.Module):
    def __init__(self, num_classes, dropout_rate=0.5):
        super().__init__()
        self.backbone = models.resnet50(weights=None)
        in_features = self.backbone.fc.in_features
        self.backbone.fc = nn.Sequential(
            nn.Dropout(dropout_rate),
            nn.Linear(in_features, 512),
            nn.ReLU(),
            nn.BatchNorm1d(512),
            nn.Dropout(dropout_rate),
            nn.Linear(512, num_classes)
        )

    def forward(self, x):
        return self.backbone(x)

def load_feature_model():
    num_classes = 5
    model = ResNet50Classifier(num_classes=num_classes, dropout_rate=0.5)
    model_path = os.path.join(os.path.dirname(__file__), 'resnet50.pth')
    if not os.path.exists(model_path):
        raise FileNotFoundError(f"Model file {model_path} not found.")
    state_dict = torch.load(model_path, map_location=device)
    model.load_state_dict(state_dict)
    # Loại bỏ layer cuối để lấy feature
    feature_extractor = nn.Sequential(*list(model.backbone.children())[:-1])
    feature_extractor = feature_extractor.to(device)
    feature_extractor.eval()
    return feature_extractor

def load_classification_model():
    num_classes = 5
    model = ResNet50Classifier(num_classes=num_classes, dropout_rate=0.5)
    model_path = os.path.join(os.path.dirname(__file__), 'resnet50.pth')
    if not os.path.exists(model_path):
        raise FileNotFoundError(f"Model file {model_path} not found.")
    state_dict = torch.load(model_path, map_location=device)
    model.load_state_dict(state_dict)
    model = model.to(device)
    model.eval()
    return model

feature_model = load_feature_model()
classification_model = load_classification_model()

def extract_feature_from_image(image):
    image = data_transforms(image).unsqueeze(0).to(device)
    with torch.no_grad():
        feature = feature_model(image)
        feature = feature.view(feature.size(0), -1).cpu().numpy()
    # Chuyển sang float32 để lưu trữ trong MongoDB
    feature = feature.astype(np.float32)
    return feature

def predict_category(image):
    image = data_transforms(image).unsqueeze(0).to(device)
    with torch.no_grad():
        outputs = classification_model(image)
        probabilities = torch.softmax(outputs, dim=1)
        predicted_index = torch.argmax(probabilities, dim=1).item()
        confidence = probabilities[0][predicted_index].item()
        category_id = INDEX_TO_CATEGORY_ID[predicted_index]
    return {
        "category_en": CATEGORY_MAPPING[category_id],
        "category_vi": CATEGORY_VIETNAMESE[category_id],
        "category_id": category_id,
        "confidence": confidence,
        "probabilities": probabilities[0].cpu().numpy().tolist()
    }

@app.route('/api/extract_features', methods=['POST'])
def extract_features():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    file = request.files['image']
    try:
        image = Image.open(file).convert('RGB')
    except:
        return jsonify({"error": "Invalid image format"}), 400
    feature = extract_feature_from_image(image)
    return jsonify({"features": feature[0].tolist()})

@app.route('/api/analyze_image', methods=['POST'])
def analyze_image():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    try:
        image = Image.open(file).convert('RGB')
    except:
        return jsonify({"error": "Invalid image format"}), 400
    
    try:
        # Trích xuất đặc trưng
        feature = extract_feature_from_image(image)
        
        # Dự đoán category
        category_prediction = predict_category(image)
        
        return jsonify({
            "feature_vector": feature[0].tolist(),
            "feature_vector_size": len(feature[0]),
            "category_prediction": category_prediction
        })
    except Exception as e:
        return jsonify({"error": f"Analysis failed: {str(e)}"}), 500

@app.route('/api/predict_category', methods=['POST'])
def predict_category_endpoint():
    if 'image' not in request.files:
        return jsonify({"error": "No image provided"}), 400
    
    file = request.files['image']
    try:
        image = Image.open(file).convert('RGB')
    except:
        return jsonify({"error": "Invalid image format"}), 400
    
    try:
        prediction = predict_category(image)
        return jsonify({
            "prediction": prediction,
            "categories": CATEGORY_FULL_MAPPING
        })
    except Exception as e:
        return jsonify({"error": f"Prediction failed: {str(e)}"}), 500

@app.route('/internal/product', methods=['POST'])
def save_product():
    try:
        # Kiểm tra dữ liệu đầu vào
        if 'image' not in request.files:
            return jsonify({"error": "No image provided"}), 400
        
        if 'product_id' not in request.form:
            return jsonify({"error": "Product ID is required"}), 400
        
        file = request.files['image']
        product_id = request.form['product_id']
        
        
        # Kiểm tra file ảnh
        if file.filename == '':
            return jsonify({"error": "No image file selected"}), 400
            
        # Kiểm tra định dạng ảnh
        allowed_extensions = {'png', 'jpg', 'jpeg', 'gif', 'bmp'}
        if not file.filename.lower().endswith(tuple(allowed_extensions)):
            return jsonify({"error": "Invalid image format. Allowed: png, jpg, jpeg, gif, bmp"}), 400
        
        # Xử lý ảnh
        try:
            image = Image.open(file).convert('RGB')
        except Exception as e:
            return jsonify({"error": f"Invalid image format: {str(e)}"}), 400
        
        # Kiểm tra sản phẩm đã tồn tại
        existing_product = products_collection.find_one({"product_id": product_id})
        if existing_product:
            return jsonify({
                "error": f"Product with ID {product_id} already exists",
                "existing_product": {
                    "product_id": existing_product.get("product_id"),
                }
            }), 409
        
        # Trích xuất đặc trưng
        feature = extract_feature_from_image(image)
        
        # Dự đoán category
        category_prediction = predict_category(image)
        
        # Lưu vào MongoDB
        product_data = {
            "product_id": product_id,
            "feature_vector": feature[0].tolist(),
            "category_en": category_prediction["category_en"],
            "category_vi": category_prediction["category_vi"],
            "category_id": category_prediction["category_id"],
            "confidence": category_prediction["confidence"],
            "probabilities": category_prediction["probabilities"],
            "created_at": datetime.utcnow()
        }
        
        result = products_collection.insert_one(product_data)
        
        return jsonify({
            "product_id": product_id,
            "feature_vector_size": len(feature[0]),
            "category_prediction": category_prediction,
        }), 201
        
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/internal/product/url', methods=['POST'])
def save_product_by_url():
    try:
        data = request.get_json()
        if not data:
            return jsonify({"error": "No JSON data provided"}), 400
        
        product_id = data.get('product_id')
        image_urls = data.get('image_urls', [])
        
        if not product_id:
            return jsonify({"error": "Product ID is required"}), 400
        
        if not image_urls:
            return jsonify({"error": "At least one image URL is required"}), 400
        
        # Kiểm tra sản phẩm đã tồn tại
        existing_product = products_collection.find_one({"product_id": product_id})
        if existing_product:
            return jsonify({
                "error": f"Product with ID {product_id} already exists",
                "existing_product": {
                    "product_id": existing_product.get("product_id"),
                }
            }), 409
        
        # Tải ảnh từ URL đầu tiên
        try:
            import requests
            response = requests.get(image_urls[0], timeout=10)
            response.raise_for_status()
            image = Image.open(io.BytesIO(response.content)).convert('RGB')
        except Exception as e:
            return jsonify({"error": f"Failed to download image from URL: {str(e)}"}), 400
        
        # Trích xuất đặc trưng
        feature = extract_feature_from_image(image)
        
        # Dự đoán category
        category_prediction = predict_category(image)
        
        # Lưu vào MongoDB
        product_data = {
            "product_id": product_id,
            "image_urls": image_urls,
            "feature_vector": feature[0].tolist(),
            "category_en": category_prediction["category_en"],
            "category_vi": category_prediction["category_vi"],
            "category_id": category_prediction["category_id"],
            "confidence": category_prediction["confidence"],
            "probabilities": category_prediction["probabilities"],
            "created_at": datetime.utcnow()
        }
        
        result = products_collection.insert_one(product_data)
        
        return jsonify({
            "product_id": product_id,
            "feature_vector_size": len(feature[0]),
            "category_prediction": category_prediction,
            "image_urls": image_urls
        }), 201
        
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/api/products', methods=['GET'])
def get_products():
    try:
        limit = int(request.args.get('limit', 50))
        skip = int(request.args.get('skip', 0))
        
        products = list(products_collection.find(
            {}, 
            {"feature_vector": 0}  # Không trả về vector đặc trưng để giảm kích thước response
        ).skip(skip).limit(limit))
        
        # Chuyển đổi ObjectId thành string
        for product in products:
            product['_id'] = str(product['_id'])
            if 'created_at' in product:
                product['created_at'] = product['created_at'].isoformat()
        
        total_count = products_collection.count_documents({})
        
        return jsonify({
            "products": products,
            "total_count": total_count,
            "limit": limit,
            "skip": skip
        })
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/api/products/<product_id>', methods=['GET'])
def get_product(product_id):
    try:
        product = products_collection.find_one({"product_id": product_id})
        
        if not product:
            return jsonify({"error": f"Product with ID {product_id} not found"}), 404
        
        # Chuyển đổi ObjectId thành string
        product['_id'] = str(product['_id'])
        if 'created_at' in product:
            product['created_at'] = product['created_at'].isoformat()
        
        return jsonify(product)
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/api/products/<product_id>', methods=['DELETE'])
def delete_product(product_id):
    try:
        result = products_collection.delete_one({"product_id": product_id})
        
        if result.deleted_count == 0:
            return jsonify({"error": f"Product with ID {product_id} not found"}), 404
        
        return jsonify({
            "message": f"Product {product_id} deleted successfully",
            "deleted_count": result.deleted_count
        })
    except Exception as e:
        return jsonify({"error": f"Internal server error: {str(e)}"}), 500

@app.route('/recommend_by_image', methods=['POST'])
def recommend_by_image():
    try:
        # Kiểm tra dữ liệu đầu vào
        if 'image' not in request.files:
            return jsonify({"error": "No image provided"}), 400
        
        file = request.files['image']
        
        # Kiểm tra file ảnh
        if file.filename == '':
            return jsonify({"error": "No image file selected"}), 400
        
        # Kiểm tra định dạng ảnh
        allowed_extensions = {'png', 'jpg', 'jpeg', 'gif', 'bmp'}
        if not file.filename.lower().endswith(tuple(allowed_extensions)):
            return jsonify({"error": "Invalid image format. Allowed: png, jpg, jpeg, gif, bmp"}), 400
        
        # Xử lý ảnh
        try:
            image = Image.open(file).convert('RGB')
        except Exception as e:
            return jsonify({"error": f"Invalid image format: {str(e)}"}), 400
        
        # Trích xuất đặc trưng từ ảnh query
        query_feature = extract_feature_from_image(image)
        
        # Dự đoán category
        category_prediction = predict_category(image)
        query_category_id = category_prediction["category_id"]
        
        # Lấy các sản phẩm cùng category từ database
        same_category_products = list(products_collection.find({"category_id": query_category_id}, {
            "feature_vector": 1, 
            "product_id": 1, 
            "product_name": 1,
            "product_description": 1,
            "product_price": 1,
            "category_en": 1,
            "category_vi": 1,
            "confidence": 1,
            "product_images": 1
        }))
        
        if not same_category_products:
            return jsonify({
                "error": "No products found in database for this category",
                "query_category": category_prediction
            }), 404
        
        # Tính toán similarity với các sản phẩm cùng category
        similar_products = []
        for product in same_category_products:
            if "feature_vector" in product:
                stored_vector = np.array(product["feature_vector"])
                # Tính cosine similarity
                similarity = cosine_similarity([query_feature[0]], [stored_vector])[0][0]
                # Chỉ lấy những sản phẩm có độ tương đồng > 90%
                if similarity > 0.9:
                    similar_products.append({
                        "product_id": product.get("product_id"),
                        "category_en": product.get("category_en", ""),
                        "category_vi": product.get("category_vi", ""),
                        "similarity": float(similarity),
                        "confidence": product.get("confidence", 0.0),
                    })
        
        # Sắp xếp theo similarity giảm dần
        similar_products.sort(key=lambda x: x["similarity"], reverse=True)
        
        # Lấy tối đa 5 sản phẩm
        top_k_products = similar_products[:5]
        
        # Tính toán thống kê
        avg_similarity = np.mean([p["similarity"] for p in top_k_products]) if top_k_products else 0.0
        category_distribution = {}
        for product in top_k_products:
            category = product["category_en"]
            category_distribution[category] = category_distribution.get(category, 0) + 1
        
        # Tạo list product IDs
        product_ids = [product["product_id"] for product in top_k_products]
        
        return jsonify({
            "query_category": category_prediction,
            "recommendations": top_k_products,
            "product_ids": product_ids,
            "statistics": {
                "total_products_in_category": len(same_category_products),
                "recommendations_count": len(top_k_products),
                "average_similarity": float(avg_similarity),
                "category_distribution": category_distribution
            }
        })
        
    except Exception as e:
        return jsonify({"error": f"Recommendation failed: {str(e)}"}), 500

@app.route('/api/recommend_by_product/<product_id>', methods=['GET'])
def recommend_by_product(product_id):
    try:
        # Lấy thông tin sản phẩm gốc
        source_product = products_collection.find_one({"product_id": product_id})
        
        if not source_product:
            return jsonify({"error": f"Product with ID {product_id} not found"}), 404
        
        # Lấy feature vector của sản phẩm gốc
        source_feature = np.array(source_product["feature_vector"])
        
        # Lấy tất cả sản phẩm khác (loại trừ sản phẩm gốc)
        other_products = list(products_collection.find(
            {"product_id": {"$ne": product_id}}, 
            {
                "feature_vector": 1, 
                "product_id": 1, 
                "product_name": 1,
                "product_description": 1,
                "product_price": 1,
                "category_en": 1,
                "category_vi": 1,
                "confidence": 1
            }
        ))
        
        if not other_products:
            return jsonify({
                "error": "No other products found in database",
                "source_product": {
                    "product_id": source_product.get("product_id"),
                    "product_name": source_product.get("product_name"),
                    "category_en": source_product.get("category_en"),
                    "category_vi": source_product.get("category_vi")
                }
            }), 404
        
        # Tính toán similarity
        similar_products = []
        for product in other_products:
            if "feature_vector" in product:
                stored_vector = np.array(product["feature_vector"])
                similarity = cosine_similarity([source_feature], [stored_vector])[0][0]
                
                similar_products.append({
                    "product_id": product.get("product_id"),
                    "product_name": product.get("product_name", ""),
                    "product_description": product.get("product_description", ""),
                    "product_price": product.get("product_price", 0.0),
                    "category_en": product.get("category_en", ""),
                    "category_vi": product.get("category_vi", ""),
                    "similarity": float(similarity),
                    "confidence": product.get("confidence", 0.0)
                })
        
        # Sắp xếp theo similarity giảm dần
        similar_products.sort(key=lambda x: x["similarity"], reverse=True)
        
        # Lấy top K sản phẩm
        k = int(request.args.get('k', 10))
        top_k_products = similar_products[:k]
        
        # Tính toán thống kê
        avg_similarity = np.mean([p["similarity"] for p in top_k_products])
        category_distribution = {}
        for product in top_k_products:
            category = product["category_en"]
            category_distribution[category] = category_distribution.get(category, 0) + 1
        
        return jsonify({
            "source_product": {
                "product_id": source_product.get("product_id"),
                "product_name": source_product.get("product_name"),
                "category_en": source_product.get("category_en"),
                "category_vi": source_product.get("category_vi"),
                "confidence": source_product.get("confidence", 0.0)
            },
            "recommendations": top_k_products,
            "statistics": {
                "total_products_in_db": len(other_products) + 1,
                "recommendations_count": len(top_k_products),
                "average_similarity": float(avg_similarity),
                "category_distribution": category_distribution
            }
        })
        
    except Exception as e:
        return jsonify({"error": f"Recommendation failed: {str(e)}"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)