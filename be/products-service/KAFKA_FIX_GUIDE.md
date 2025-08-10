# Hướng dẫn fix lỗi Kafka

## Vấn đề đã sửa:
- `KafkaTestController` yêu cầu `KafkaTemplate` bắt buộc
- `NotificationEventService` và `NotificationTestController` cũng yêu cầu `KafkaTemplate` bắt buộc
- Ứng dụng không thể start khi không có Kafka server

## Giải pháp đã áp dụng:

### 1. Thêm `@ConditionalOnProperty` vào KafkaConfig
```java
@Configuration
@EnableKafka
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaConfig {
    // Chỉ load khi có cấu hình Kafka
}
```

### 2. Làm KafkaTemplate optional trong tất cả services/controllers
```java
@Autowired(required = false)
private KafkaTemplate<String, Object> kafkaTemplate;
```

### 3. Thêm null check trong tất cả methods
```java
if (kafkaTemplate != null) {
    kafkaTemplate.send(topic, key, event);
} else {
    log.info("Kafka not available - Event would be sent");
}
```

## Cách chạy ứng dụng:

### Với Kafka:
```bash
./gradlew bootRun
```

### Không có Kafka (để test):
```bash
./gradlew bootRun --args='--spring.config.location=classpath:application-no-kafka.yaml'
```

## Test endpoints:

### Health check:
```bash
curl -X GET "http://localhost:8080/api/kafka-test/health"
curl -X GET "http://localhost:8080/api/notification-test/health"
```

### Test Kafka:
```bash
curl -X POST "http://localhost:8080/api/kafka-test/send-product-approval" \
  -H "Content-Type: application/json" \
  -d '["1", "2", "3"]'
```

### Test Notification:
```bash
curl -X POST "http://localhost:8080/api/notification-test/send-email-notification" \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "recipient": "test@example.com", "title": "Test"}'
```

## Log messages:

### Với Kafka:
```
INFO: Direct Kafka message sent successfully to topic: product.approval
```

### Không có Kafka:
```
INFO: Kafka not available - Direct Kafka message would be sent to topic: product.approval
```

## Lợi ích:
1. **Flexible deployment:** Chạy với hoặc không có Kafka
2. **Easy testing:** Test mà không cần Kafka server
3. **Graceful degradation:** Ứng dụng vẫn hoạt động khi không có Kafka
4. **Clear status:** Health check cho biết trạng thái Kafka 