# Products Service - Kafka Integration

## Tổng Quan

Products Service sử dụng Kafka để gửi thông báo khi product được accept. Khi admin accept product, service sẽ gửi event qua Kafka topic `product.approval`.

## Cấu Hình Kafka

### 1. Dependencies (build.gradle)
```gradle
implementation 'org.springframework.kafka:spring-kafka'
```

### 2. Application Configuration (application.yaml)
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false
        spring.json.trusted.packages: "*"

kafka:
  topics:
    product-approval: product.approval
```

### 3. Kafka Configuration (KafkaConfig.java)
```java
@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Bean
    public ProducerFactory<String, ProductApprovalEventDto> producerFactory() {
        // Cấu hình producer
    }
    
    @Bean
    public KafkaTemplate<String, ProductApprovalEventDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

## Cách Sử Dụng

### 1. Inject KafkaTemplate
```java
@Service
public class NotificationEventService {
    
    private final KafkaTemplate<String, ProductApprovalEventDto> kafkaTemplate;
    
    public void sendProductApprovalNotification(List<String> productIds) {
        // Tạo event
        ProductApprovalEventDto event = new ProductApprovalEventDto(...);
        
        // Gửi qua Kafka
        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event sent successfully");
                } else {
                    log.error("Failed to send event: {}", ex.getMessage());
                }
            });
    }
}
```

### 2. Gọi từ ProductService
```java
@Service
public class ProductService {
    
    private final NotificationEventService notificationEventService;
    
    public void acceptProduct(List<String> productIds) {
        // Logic accept product
        
        // Gửi notification
        try {
            notificationEventService.sendProductApprovalNotification(productIds);
            log.info("Product approval notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}
```

## Test Endpoints

### 1. Health Check
```bash
curl -X GET http://localhost:8080/api/kafka-test/health
```

### 2. Test Product Approval Notification
```bash
curl -X POST http://localhost:8080/api/kafka-test/send-product-approval \
  -H "Content-Type: application/json" \
  -d '["1", "2", "3"]'
```

### 3. Test Direct Kafka Message
```bash
curl -X POST http://localhost:8080/api/kafka-test/send-direct-kafka \
  -H "Content-Type: application/json" \
  -d '{
    "topic": "product.approval",
    "key": "test-key"
  }'
```

## Cấu Trúc Event

### ProductApprovalEventDto
```json
{
  "eventType": "PRODUCT_APPROVED",
  "productIds": ["1", "2", "3"],
  "approvedBy": "admin",
  "approvedAt": "2024-01-01T10:00:00",
  "message": "Sản phẩm đã được chấp nhận bởi admin",
  "vendorId": "vendor123",
  "vendorName": "Vendor Name"
}
```

## Monitoring

### 1. Kiểm tra Kafka Topics
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --list
```

### 2. Xem messages trong topic
```bash
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic product.approval --from-beginning
```

### 3. Kiểm tra logs
```bash
# Products Service logs
tail -f logs/products-service.log | grep "Kafka\|product approval"

# Notification Service logs  
tail -f logs/notification-service.log | grep "product approval"
```

## Troubleshooting

### 1. Kafka không kết nối được
- Kiểm tra Kafka đã start chưa
- Kiểm tra bootstrap-servers trong application.yaml
- Kiểm tra network connectivity

### 2. Serialization Error
- Kiểm tra ProductApprovalEventDto có getter/setter không
- Kiểm tra cấu hình JsonSerializer
- Thêm `spring.json.trusted.packages: "*"`

### 3. Topic không tồn tại
- Tạo topic manually:
```bash
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic product.approval --partitions 3 --replication-factor 1
```

### 4. Permission Error
- Kiểm tra quyền write vào topic
- Kiểm tra ACL nếu có

## Best Practices

1. **Error Handling**: Luôn wrap Kafka send trong try-catch
2. **Async Processing**: Sử dụng whenComplete() để xử lý async
3. **Logging**: Log đầy đủ thông tin success/error
4. **Monitoring**: Monitor Kafka metrics và logs
5. **Retry Logic**: Implement retry mechanism cho failed messages

## Development Tips

1. **Local Development**: Sử dụng Docker Compose để start Kafka
2. **Testing**: Sử dụng test endpoints để verify Kafka integration
3. **Debugging**: Enable DEBUG logging cho Kafka
4. **Schema Evolution**: Cẩn thận khi thay đổi DTO structure 