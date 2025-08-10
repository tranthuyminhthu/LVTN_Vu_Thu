# Notification Service

## Tổng quan
Notification Service là một microservice được thiết kế để xử lý và gửi các loại thông báo khác nhau (Email, SMS, Push, In-App) trong hệ thống e-commerce. Service sử dụng Apache Kafka làm message queue và PostgreSQL làm database.

## Kiến trúc

### Components chính:
- **Kafka**: Message queue cho async processing
- **PostgreSQL**: Database chính với JSONB support
- **Spring Boot**: Framework chính
- **Spring Kafka**: Kafka integration
- **Spring Mail**: Email sending
- **pgAdmin**: Database management UI

### Notification Types:
- **EMAIL**: Gửi email thông qua SMTP
- **SMS**: Gửi SMS thông qua SMS provider
- **PUSH**: Push notification thông qua Firebase
- **IN_APP**: In-app notification

## Quick Start

### 1. Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven/Gradle

### 2. Setup Infrastructure

#### Start PostgreSQL và pgAdmin:
```bash
docker-compose -f docker-compose-postgresql.yml up -d
```

#### Start Kafka:
```bash
docker-compose -f docker-compose-kafka.yml up -d
```

### 3. Database Setup
Database sẽ được tự động tạo khi chạy Docker Compose. Schema bao gồm:
- `notifications`: Bảng chính lưu trữ notifications
- `notification_templates`: Templates cho notifications
- `user_notification_preferences`: User preferences

### 4. Run Application
```bash
# Build project
./gradlew build

# Run application
./gradlew bootRun
```

## API Endpoints

### Send Notification
```http
POST /api/notifications/send
Content-Type: application/json

{
  "userId": "user123",
  "type": "EMAIL",
  "category": "ORDER",
  "title": "Order Confirmation",
  "content": "Your order has been confirmed",
  "recipient": "user@example.com",
  "data": {
    "orderId": "123",
    "totalAmount": "500000"
  }
}
```

### Schedule Notification
```http
POST /api/notifications/schedule
Content-Type: application/json

{
  "userId": "user123",
  "type": "EMAIL",
  "category": "PROMOTION",
  "title": "Special Offer",
  "content": "Get 20% off on your next purchase",
  "recipient": "user@example.com",
  "scheduledAt": "2024-01-15T10:00:00"
}
```

### Get User Notifications
```http
GET /api/notifications/user?page=0&size=10
```

### Mark as Read
```http
PUT /api/notifications/{id}/read
```

### Get Statistics
```http
GET /api/notifications/stats?since=2024-01-01T00:00:00
```

## Configuration

### Database (PostgreSQL)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_db
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### Kafka
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Email (SMTP)
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

## Kafka Topics

### Producer Topics:
- `notification.email`: Email notifications
- `notification.sms`: SMS notifications
- `notification.push`: Push notifications
- `notification.inapp`: In-app notifications
- `notification.retry`: Failed notifications for retry

### Consumer Groups:
- `notification-service-group`: Main consumer group

## Database Schema

### Notifications Table
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type notification_type NOT NULL,
    category notification_category NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    template_id VARCHAR(100),
    status notification_status NOT NULL DEFAULT 'PENDING',
    recipient VARCHAR(255) NOT NULL,
    data JSONB,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Enum Types
```sql
CREATE TYPE notification_type AS ENUM ('EMAIL', 'SMS', 'PUSH', 'IN_APP');
CREATE TYPE notification_category AS ENUM ('ORDER', 'PROMOTION', 'SYSTEM', 'SECURITY', 'MARKETING');
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED', 'READ', 'SCHEDULED');
```

## PostgreSQL Features

### 1. JSONB Support
```sql
-- Store template variables
UPDATE notifications 
SET data = '{"orderId": "123", "userName": "John"}'::jsonb
WHERE id = 1;

-- Query JSONB data
SELECT * FROM notifications 
WHERE data->>'orderId' = '123';
```

### 2. Advanced Indexing
```sql
-- GIN index for JSONB
CREATE INDEX idx_notifications_data_gin ON notifications USING GIN (data);

-- Composite indexes
CREATE INDEX idx_notifications_user_status_created 
ON notifications(user_id, status, created_at DESC);
```

### 3. Stored Functions
```sql
-- Get notification statistics
SELECT * FROM get_notification_stats(CURRENT_TIMESTAMP - INTERVAL '7 days');

-- Get daily statistics
SELECT * FROM get_daily_notification_stats(CURRENT_TIMESTAMP - INTERVAL '30 days');
```

## Monitoring

### Health Check
```http
GET /actuator/health
```

### Metrics
```http
GET /actuator/metrics
```

### Database Monitoring
- **pgAdmin**: http://localhost:5050
  - Email: admin@admin.com
  - Password: admin

### Kafka Monitoring
- **Kafka UI**: http://localhost:8080

## Development

### Project Structure
```
src/main/java/org/example/notification_service/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── listener/        # Kafka listeners
├── repository/      # Data access layer
└── service/         # Business logic
```

### Adding New Notification Type
1. Add enum value to `NotificationEntity.NotificationType`
2. Create service implementation
3. Add Kafka topic configuration
4. Update listener to handle new type

### Testing
```bash
# Run tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## Production Deployment

### Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/notification_db
export SPRING_DATASOURCE_USERNAME=notification_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export SPRING_KAFKA_BOOTSTRAP_SERVERS=prod-kafka:9092
export SPRING_MAIL_USERNAME=prod-email@domain.com
export SPRING_MAIL_PASSWORD=prod-app-password
```

### Docker Deployment
```bash
# Build image
docker build -t notification-service .

# Run container
docker run -d \
  --name notification-service \
  -p 8085:8085 \
  --env-file .env \
  notification-service
```

### Database Migration
```bash
# Backup current data
pg_dump -h localhost -U postgres notification_db > backup.sql

# Apply schema changes
psql -h localhost -U postgres -d notification_db < init-schema.sql
```

## Troubleshooting

### Common Issues

#### 1. Database Connection
```bash
# Check PostgreSQL status
docker-compose -f docker-compose-postgresql.yml ps

# Test connection
psql -h localhost -U postgres -d notification_db
```

#### 2. Kafka Connection
```bash
# Check Kafka status
docker-compose -f docker-compose-kafka.yml ps

# List topics
docker exec notification-kafka kafka-topics --list --bootstrap-server localhost:9092
```

#### 3. Email Sending
- Verify SMTP credentials
- Check firewall settings
- Enable "Less secure app access" for Gmail

### Performance Tuning

#### Database
```sql
-- Analyze tables
ANALYZE notifications;

-- Check query performance
EXPLAIN ANALYZE SELECT * FROM notifications WHERE user_id = 'user123';
```

#### Kafka
```bash
# Increase partitions
docker exec notification-kafka kafka-topics --alter \
  --topic notification.email \
  --partitions 3 \
  --bootstrap-server localhost:9092
```

## Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Add tests
5. Submit pull request

## License

This project is licensed under the MIT License. 