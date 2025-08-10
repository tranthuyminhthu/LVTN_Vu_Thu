# API Xóa Mềm Sản Phẩm

## Tổng quan

API này cho phép xóa mềm sản phẩm bằng cách đổi status thành `DELETED` thay vì xóa hoàn toàn khỏi database. Sản phẩm đã xóa mềm sẽ không hiển thị trong danh sách sản phẩm thông thường.

## Endpoints

### 1. Xóa mềm một sản phẩm
```
DELETE /products/{productId}
```

### 2. Xóa mềm nhiều sản phẩm
```
DELETE /products/batch
```

## Quyền truy cập

- **SELLER** - Chỉ có thể xóa sản phẩm của mình
- **ADMIN** - Có thể xóa bất kỳ sản phẩm nào
- **MANAGER** - Có thể xóa bất kỳ sản phẩm nào

## Request

### Headers
```
Content-Type: application/json
Authorization: Bearer YOUR_TOKEN
```

### Parameters
- `productId` (path): ID của sản phẩm cần xóa (cho endpoint xóa một sản phẩm)

### Body (cho endpoint batch)
```json
[1, 2, 3, 4, 5]
```

## Response

### Success (200 OK)
```json
{
  "message": "Product deleted successfully"
}
```

### Error Responses

#### 400 Bad Request
```json
{
  "error": "Product not found with id: 123"
}
```

```json
{
  "error": "You can only delete your own products"
}
```

#### 401 Unauthorized
```json
{
  "error": "Access denied"
}
```

#### 404 Not Found
```json
{
  "error": "Product not found"
}
```

#### 500 Internal Server Error
```json
{
  "error": "An error occurred while deleting the product: [error message]"
}
```

## Cách hoạt động

### 1. Xóa mềm một sản phẩm
- Kiểm tra sản phẩm tồn tại
- Kiểm tra quyền xóa (chỉ người tạo hoặc admin)
- Đổi status thành `DELETED`
- Xóa khỏi Elasticsearch index
- Lưu thay đổi vào database

### 2. Xóa mềm nhiều sản phẩm
- Kiểm tra tất cả sản phẩm tồn tại
- Kiểm tra quyền xóa cho từng sản phẩm
- Đổi status tất cả sản phẩm thành `DELETED`
- Xóa khỏi Elasticsearch index
- Lưu thay đổi vào database

## Ví dụ sử dụng

### CURL - Xóa một sản phẩm
```bash
curl -X DELETE \
  http://localhost:8080/products/123 \
  -H 'Authorization: Bearer YOUR_TOKEN'
```

### CURL - Xóa nhiều sản phẩm
```bash
curl -X DELETE \
  http://localhost:8080/products/batch \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -d '[1, 2, 3, 4, 5]'
```

### JavaScript/Fetch - Xóa một sản phẩm
```javascript
const softDeleteProduct = async (productId) => {
  try {
    const response = await fetch(`/products/${productId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': 'Bearer YOUR_TOKEN'
      }
    });
    
    if (response.ok) {
      const result = await response.json();
      console.log('Product deleted:', result.message);
      return result;
    } else {
      const error = await response.json();
      console.error('Error:', error);
      throw new Error(error.error);
    }
  } catch (error) {
    console.error('Failed to delete product:', error);
    throw error;
  }
};

// Sử dụng
softDeleteProduct(123);
```

### JavaScript/Fetch - Xóa nhiều sản phẩm
```javascript
const softDeleteProducts = async (productIds) => {
  try {
    const response = await fetch('/products/batch', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer YOUR_TOKEN'
      },
      body: JSON.stringify(productIds)
    });
    
    if (response.ok) {
      const result = await response.json();
      console.log('Products deleted:', result.message);
      return result;
    } else {
      const error = await response.json();
      console.error('Error:', error);
      throw new Error(error.error);
    }
  } catch (error) {
    console.error('Failed to delete products:', error);
    throw error;
  }
};

// Sử dụng
softDeleteProducts([1, 2, 3, 4, 5]);
```

## Lưu ý quan trọng

1. **Xóa mềm**: Sản phẩm không bị xóa hoàn toàn khỏi database, chỉ đổi status thành `DELETED`
2. **Không hiển thị**: Sản phẩm đã xóa mềm sẽ không hiển thị trong danh sách sản phẩm thông thường
3. **Quyền xóa**: SELLER chỉ có thể xóa sản phẩm của mình, ADMIN/MANAGER có thể xóa bất kỳ sản phẩm nào
4. **Elasticsearch**: Sản phẩm cũng bị xóa khỏi Elasticsearch index để không xuất hiện trong tìm kiếm
5. **Validation**: API kiểm tra sản phẩm tồn tại và quyền xóa trước khi thực hiện

## Khôi phục sản phẩm

Để khôi phục sản phẩm đã xóa mềm, bạn có thể:
1. Truy cập trực tiếp database và đổi status về trạng thái cũ
2. Tạo API riêng để khôi phục sản phẩm (nếu cần)

## Database Query

### Kiểm tra sản phẩm đã xóa mềm
```sql
SELECT * FROM products WHERE status = '5';
```

### Khôi phục sản phẩm (nếu cần)
```sql
UPDATE products SET status = '1' WHERE id = 123;
``` 