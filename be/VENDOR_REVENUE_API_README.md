# API Thống Kê Doanh Thu Cho Vendor

## Tổng quan

API thống kê doanh thu được triển khai trong `user-service` và gọi qua `order-service` để lấy dữ liệu đơn hàng. Điều này giúp tập trung logic nghiệp vụ vendor tại một nơi.

## Cấu trúc API

### 1. Thống kê doanh thu theo khoảng thời gian

**Endpoint:** `GET /api/vendors/me/revenue-stats`

**Parameters:**
- `startDate` (required): Ngày bắt đầu (LocalDateTime)
- `endDate` (required): Ngày kết thúc (LocalDateTime)

**Response:**
```json
{
  "vendorId": "vendor123",
  "vendorName": "Shop ABC",
  "totalRevenue": 1500000.0,
  "totalOrders": 25,
  "averageOrderValue": 60000.0,
  "startDate": "2024-01-01T00:00:00",
  "endDate": "2024-01-31T23:59:59",
  "dailyRevenue": [
    {
      "date": "2024-01-15T00:00:00",
      "revenue": 50000.0,
      "orderCount": 2
    }
  ],
  "topProducts": [
    {
      "productSku": "SKU001",
      "productName": "Áo thun nam",
      "quantitySold": 50,
      "totalRevenue": 250000.0
    }
  ]
}
```

### 2. Thống kê doanh thu theo tháng

**Endpoint:** `GET /api/vendors/me/revenue-stats/monthly`

**Parameters:**
- `year` (optional): Năm cần thống kê (default: năm hiện tại)

**Response:**
```json
{
  "vendorId": "vendor123",
  "vendorName": "Shop ABC",
  "totalRevenue": 5000000.0,
  "totalOrders": 100,
  "averageOrderValue": 50000.0,
  "monthlyRevenue": [
    {
      "year": 2024,
      "month": 1,
      "revenue": 1500000.0,
      "orderCount": 25
    },
    {
      "year": 2024,
      "month": 2,
      "revenue": 2000000.0,
      "orderCount": 35
    }
  ]
}
```

### 3. Thống kê doanh thu cho vendor cụ thể (Admin)

**Endpoint:** `GET /api/vendors/{vendorId}/revenue-stats`

**Endpoint:** `GET /api/vendors/{vendorId}/revenue-stats/monthly`

## Cách sử dụng

### 1. Lấy thống kê doanh thu tháng này
```bash
curl -X GET "http://localhost:8081/api/vendors/me/revenue-stats?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. Lấy thống kê doanh thu theo tháng
```bash
curl -X GET "http://localhost:8081/api/vendors/me/revenue-stats/monthly?year=2024" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Lưu ý

1. **Authentication**: Tất cả API đều yêu cầu JWT token hợp lệ
2. **Authorization**: Vendor chỉ có thể xem thống kê của chính mình
3. **Data Source**: Dữ liệu được lấy từ đơn hàng có status = 'RECEIVED'
4. **Performance**: Các query được tối ưu với index trên vendorId, status, và createdAt

## Cấu hình

### User Service (application.yaml)
```yaml
service:
  order-service: http://localhost:8084
```

### Order Service (application.yaml)
```yaml
server:
  port: 8084
```

## Dependencies

### User Service
- Spring Cloud OpenFeign
- Spring Boot Web
- Spring Data MongoDB

### Order Service
- Spring Cloud OpenFeign
- Spring Boot Web
- Spring Data JPA
- PostgreSQL

## Error Handling

API trả về các HTTP status codes:
- `200`: Thành công
- `400`: Bad Request (thiếu parameters)
- `401`: Unauthorized (thiếu hoặc token không hợp lệ)
- `500`: Internal Server Error (lỗi server)

Error response format:
```json
{
  "message": "Error description"
}
``` 