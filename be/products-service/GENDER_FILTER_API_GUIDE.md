# Hướng dẫn sử dụng API Products với Gender Filter

## Tổng quan
Đã thêm tham số `gender` vào API `GET /api/products` để lọc sản phẩm theo giới tính.

## API Endpoints được cập nhật

### 1. Lấy danh sách sản phẩm với gender filter
```http
GET /api/products?page=0&size=10&status=ACCEPTED&gender=MALE
```

**Tham số:**
- `page` (optional, default: 0): Trang hiện tại
- `size` (optional, default: 10): Số sản phẩm mỗi trang
- `status` (optional): Lọc theo trạng thái sản phẩm
- `gender` (optional): Lọc theo giới tính

**Giá trị gender:**
- `MALE`: Sản phẩm dành cho nam
- `FEMALE`: Sản phẩm dành cho nữ
- `UNISEX`: Sản phẩm unisex (dành cho cả nam và nữ)

### 2. Lấy sản phẩm của vendor với gender filter
```http
GET /api/products/me?page=0&size=10&status=ACCEPTED&gender=FEMALE
```

## Ví dụ sử dụng

### Lấy tất cả sản phẩm nam
```bash
curl -X GET "http://localhost:8080/api/products?gender=MALE" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

### Lấy sản phẩm nữ đã được chấp nhận
```bash
curl -X GET "http://localhost:8080/api/products?status=ACCEPTED&gender=FEMALE" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

### Lấy sản phẩm unisex với phân trang
```bash
curl -X GET "http://localhost:8080/api/products?page=1&size=20&gender=UNISEX" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

### Lấy sản phẩm nam của vendor
```bash
curl -X GET "http://localhost:8080/api/products/me?gender=MALE" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

## Logic hoạt động

### Kết hợp filter:
1. **Chỉ có gender**: Lọc theo gender, không lọc theo status
2. **Chỉ có status**: Lọc theo status, không lọc theo gender  
3. **Có cả gender và status**: Lọc theo cả hai điều kiện
4. **Không có filter nào**: Lấy tất cả sản phẩm (trừ DELETED)

### Database Queries:

**Chỉ filter theo gender:**
```sql
SELECT * FROM products 
WHERE gender = 'MALE' 
ORDER BY created_at DESC 
LIMIT 10 OFFSET 0;
```

**Filter theo cả status và gender:**
```sql
SELECT * FROM products 
WHERE status = 'ACCEPTED' AND gender = 'FEMALE' 
ORDER BY created_at DESC 
LIMIT 10 OFFSET 0;
```

**Filter theo vendor và gender:**
```sql
SELECT * FROM products 
WHERE created_by = 'vendor_id' AND gender = 'UNISEX' 
ORDER BY created_at DESC 
LIMIT 10 OFFSET 0;
```

## Response Format

Response vẫn giữ nguyên format cũ:

```json
{
  "products": [
    {
      "id": 1,
      "name": "Áo thun nam",
      "description": "Áo thun dành cho nam",
      "price": 150000,
      "rating": 4.5,
      "status": "ACCEPTED",
      "gender": "MALE",
      "viewCount": 100,
      "images": ["url1", "url2"],
      "variants": [...],
      "vendorInfo": {...},
      "isFavorite": false
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 25,
  "totalPages": 3
}
```

## Các trường hợp sử dụng

### 1. E-commerce Website
- Hiển thị sản phẩm theo giới tính người dùng
- Filter sản phẩm trong trang danh mục
- Tìm kiếm sản phẩm phù hợp

### 2. Vendor Dashboard
- Quản lý sản phẩm theo giới tính
- Thống kê sản phẩm theo gender
- Filter sản phẩm trong inventory

### 3. Recommendation System
- Gợi ý sản phẩm phù hợp với giới tính
- Personalization dựa trên gender

## Lưu ý quan trọng

1. **Backward Compatibility**: API vẫn hoạt động bình thường nếu không truyền tham số `gender`
2. **Case Sensitivity**: Giá trị gender phải đúng chính xác (MALE, FEMALE, UNISEX)
3. **Performance**: Query được tối ưu với index trên các trường `gender`, `status`, `created_by`
4. **Validation**: Cần validate giá trị gender ở frontend trước khi gọi API

## Error Handling

- **400 Bad Request**: Nếu giá trị gender không hợp lệ
- **200 OK**: Trả về danh sách sản phẩm (có thể rỗng nếu không có sản phẩm phù hợp)
- **401 Unauthorized**: Nếu không có token hoặc role không đúng

## Migration Notes

Nếu có dữ liệu cũ không có giá trị gender:
- Sản phẩm cũ sẽ không được trả về khi filter theo gender
- Cần cập nhật dữ liệu cũ để có giá trị gender phù hợp
- Có thể sử dụng API không có gender filter để lấy tất cả sản phẩm
