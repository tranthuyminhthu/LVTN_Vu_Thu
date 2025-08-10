# Products Service - Notification Test API

## Tổng Quan

API này cho phép test việc gửi email notification từ products-service qua Kafka để trigger `listenEmailNotifications` trong notification-service.

## Test Endpoints

### 1. Health Check
```bash
curl -X GET http://localhost:8080/api/notification-test/health
```

### 2. Send Email Notification (Generic)
```bash
curl -X POST http://localhost:8080/api/notification-test/send-email-notification \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "category": "SECURITY",
    "title": "Password Reset Request",
    "content": "You requested a password reset for your account. Click the link below to reset your password.",
    "recipient": "user@example.com",
    "data": {
      "firstName": "User",
      "resetLink": "http://localhost:3000/reset-password?token=abc123def456",
      "expiryHours": "24"
    }
  }'
```

### 3. Send Password Reset Email (Specific)
```bash
curl -X POST http://localhost:8080/api/notification-test/send-password-reset-email \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "recipient": "john.doe@example.com",
    "data": {
      "firstName": "John",
      "resetLink": "https://myapp.com/reset-password?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NTBlODQwMC1lMjliLTQxZDQtYTcxNi00NDY2NTU0NDAwMDAiLCJleHAiOjE3MDQwOTYwMDB9.signature",
      "expiryHours": "24"
    }
  }'
```

### 4. Send Product Approval Email
```bash
curl -X POST http://localhost:8080/api/notification-test/send-product-approval-email \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "recipient": "customer@example.com",
    "vendorId": "vendor456",
    "vendorName": "Fashion Store",
    "productIds": ["1", "2", "3"],
    "productCount": 3,
    "approvedBy": "admin"
  }'
```

## Expected Response

### Success Response
```json
{
  "message": "Email notification sent successfully from products-service",
  "topic": "notification.email",
  "key": "email-notification",
  "userId": "user123",
  "recipient": "user@example.com",
  "timestamp": "2024-01-01T10:00:00"
}
```

### Error Response
```json
{
  "error": "Failed to send email notification from products-service: Kafka connection failed"
}
```

## Logs to Monitor

### Products Service Logs
```bash
# Start products service
cd products-service
./gradlew bootRun

# Monitor logs
tail -f logs/products-service.log | grep "email notification"
```

### Notification Service Logs
```bash
# Start notification service
cd notification_service
./gradlew bootRun

# Monitor logs
tail -f logs/notification-service.log | grep "email notification"
```

### Expected Log Output

**Products Service:**
```
2024-01-01 10:00:00 - Sending email notification from products-service to topic: notification.email, key: email-notification
2024-01-01 10:00:00 - Notification details: userId=user123, recipient=user@example.com, title=Password Reset Request
2024-01-01 10:00:00 - Email notification sent successfully from products-service to topic: notification.email, partition: 0, offset: 1
```

**Notification Service:**
```
2024-01-01 10:00:01 - Received email notification from topic: notification.email, partition: 0, offset: 1
2024-01-01 10:00:01 - Processing email notification: id=1, recipient=user@example.com
2024-01-01 10:00:01 - HTML email sent successfully to: user@example.com
```

## Kafka Commands

### Check Topics
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### Monitor Email Topic
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic notification.email --from-beginning
```

### Check Consumer Groups
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group notification-service-group
```

## Test Scenarios

### 1. Password Reset Email
```bash
curl -X POST http://localhost:8080/api/notification-test/send-password-reset-email \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test-user-123",
    "recipient": "test@example.com",
    "data": {
      "firstName": "Test",
      "resetLink": "http://localhost:3000/reset-password?token=test-token-123",
      "expiryHours": "24"
    }
  }'
```

### 2. Product Approval Email
```bash
curl -X POST http://localhost:8080/api/notification-test/send-product-approval-email \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "customer-789",
    "recipient": "customer@example.com",
    "vendorId": "fashion-store-123",
    "vendorName": "Fashion Store",
    "productIds": ["101", "102", "103"],
    "productCount": 3,
    "approvedBy": "admin"
  }'
```

### 3. Welcome Email
```bash
curl -X POST http://localhost:8080/api/notification-test/send-email-notification \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "new-user-456",
    "type": "EMAIL",
    "category": "WELCOME",
    "title": "Welcome to Our Platform!",
    "content": "Thank you for joining our platform. We are excited to have you on board!",
    "recipient": "welcome@example.com",
    "data": {
      "firstName": "New",
      "activationLink": "http://localhost:3000/activate?token=welcome-token-456"
    }
  }'
```

## Integration with Product Approval

Bạn có thể tích hợp việc gửi email notification vào logic accept product:

```java
@Service
public class ProductService {
    
    private final KafkaTemplate<String, Object> objectKafkaTemplate;
    
    public void acceptProduct(List<String> productIds) {
        // Logic accept product
        
        // Gửi email notification
        try {
            Map<String, Object> notification = Map.of(
                "userId", "customer123",
                "type", "EMAIL",
                "category", "VENDOR_PRODUCT_APPROVAL",
                "title", "Sản phẩm mới từ " + vendorName,
                "content", "Vendor " + vendorName + " vừa có " + productIds.size() + " sản phẩm mới được chấp nhận!",
                "recipient", "customer@example.com",
                "data", Map.of(
                    "vendorId", vendorId,
                    "vendorName", vendorName,
                    "productIds", productIds,
                    "productCount", productIds.size(),
                    "approvedBy", "admin",
                    "approvedAt", LocalDateTime.now().toString()
                )
            );
            
            objectKafkaTemplate.send("notification.email", "product-approval", notification);
            log.info("Product approval email notification sent successfully");
            
        } catch (Exception e) {
            log.error("Failed to send product approval email notification: {}", e.getMessage());
        }
    }
}
```

## Troubleshooting

### 1. Kafka Connection Issues
- Kiểm tra Kafka đã start chưa
- Kiểm tra bootstrap-servers trong application.yaml
- Kiểm tra network connectivity

### 2. Topic Not Found
```bash
# Tạo topic nếu chưa có
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic notification.email --partitions 3 --replication-factor 1
```

### 3. Serialization Errors
- Kiểm tra cấu hình JsonSerializer
- Kiểm tra data format
- Kiểm tra trusted packages

### 4. Email Not Sent
- Kiểm tra notification-service đã start chưa
- Kiểm tra SMTP configuration trong notification-service
- Kiểm tra logs của notification-service

## Development Tips

1. **Start Services**: Đảm bảo Kafka, products-service và notification-service đã start
2. **Monitor Logs**: Theo dõi logs của cả hai services
3. **Test Incrementally**: Test từng bước một
4. **Use Real Email**: Test với email thật để verify delivery 