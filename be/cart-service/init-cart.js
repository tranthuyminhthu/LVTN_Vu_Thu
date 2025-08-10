// MongoDB initialization script for cart-service
db = db.getSiblingDB('cart');

// Create collections
db.createCollection('carts');
db.createCollection('cart_items');

// Create indexes for better performance
db.carts.createIndex({ "userId": 1 });
db.carts.createIndex({ "createdAt": 1 });

db.cart_items.createIndex({ "cartId": 1 });
db.cart_items.createIndex({ "productId": 1 });
db.cart_items.createIndex({ "createdAt": 1 });

// Create a user for the application
db.createUser({
  user: "cart_user",
  pwd: "cart_password",
  roles: [
    { role: "readWrite", db: "cart" }
  ]
});

print("Cart database initialization completed!"); 