# PostgreSQL Setup cho Notification Service

## Tổng quan
Notification Service đã được cấu hình để sử dụng PostgreSQL thay vì MySQL. PostgreSQL cung cấp hiệu suất cao, hỗ trợ JSONB, và các tính năng nâng cao cho analytics.

## Cài đặt PostgreSQL

### 1. Sử dụng Docker Compose (Khuyến nghị)

```bash
# Chạy PostgreSQL và pgAdmin
docker-compose -f docker-compose-postgresql.yml up -d

# Kiểm tra trạng thái
docker-compose -f docker-compose-postgresql.yml ps
```

### 2. Cài đặt PostgreSQL locally

#### Windows:
1. Tải PostgreSQL từ: https://www.postgresql.org/download/windows/
2. Cài đặt với default settings
3. Ghi nhớ password cho user postgres

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib

# Start service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Switch to postgres user
sudo -u postgres psql

# Create database and user
CREATE DATABASE notification_db;
CREATE USER notification_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE notification_db TO notification_user;
\q
```

#### macOS:
```bash
# Using Homebrew
brew install postgresql
brew services start postgresql

# Create database
createdb notification_db
```

## Cấu hình Database

### 1. Kết nối database
```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d notification_db

# Hoặc nếu dùng Docker
docker exec -it notification-postgres psql -U postgres -d notification_db
```

### 2. Kiểm tra schema
```sql
-- Kiểm tra tables
\dt

-- Kiểm tra enums
\dT+ notification_type
\dT+ notification_category
\dT+ notification_status

-- Kiểm tra indexes
\di
```

### 3. Kiểm tra sample data
```sql
-- Xem templates
SELECT * FROM notification_templates;

-- Xem notifications (nếu có)
SELECT * FROM notifications LIMIT 10;
```

## pgAdmin Setup

### 1. Truy cập pgAdmin
- **URL**: http://localhost:5050
- **Email**: admin@admin.com
- **Password**: admin

### 2. Kết nối database
1. Click "Add New Server"
2. **General Tab**:
   - Name: Notification Service
3. **Connection Tab**:
   - Host: localhost (hoặc notification-postgres nếu dùng Docker)
   - Port: 5432
   - Database: notification_db
   - Username: postgres
   - Password: password

## Cấu hình Application

### 1. application.yaml
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
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

### 2. Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/notification_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=password
```

## PostgreSQL Features được sử dụng

### 1. JSONB Data Type
```sql
-- Lưu trữ template variables
UPDATE notifications 
SET data = '{"orderId": "123", "userName": "John", "totalAmount": "500000"}'::jsonb
WHERE id = 1;

-- Query JSONB data
SELECT * FROM notifications 
WHERE data->>'orderId' = '123';
```

### 2. Enum Types
```sql
-- Tạo enum types
CREATE TYPE notification_type AS ENUM ('EMAIL', 'SMS', 'PUSH', 'IN_APP');
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED', 'READ');

-- Sử dụng trong queries
SELECT * FROM notifications 
WHERE type = 'EMAIL' AND status = 'PENDING';
```

### 3. Advanced Indexing
```sql
-- GIN index cho JSONB
CREATE INDEX idx_notifications_data_gin ON notifications USING GIN (data);

-- Composite indexes
CREATE INDEX idx_notifications_user_status_created 
ON notifications(user_id, status, created_at DESC);
```

### 4. Stored Functions
```sql
-- Function để lấy statistics
SELECT * FROM get_notification_stats(CURRENT_TIMESTAMP - INTERVAL '7 days');

-- Function để lấy daily stats
SELECT * FROM get_daily_notification_stats(CURRENT_TIMESTAMP - INTERVAL '30 days');
```

## Performance Optimization

### 1. Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 2. Query Optimization
```sql
-- Analyze tables
ANALYZE notifications;
ANALYZE notification_templates;

-- Check query performance
EXPLAIN ANALYZE 
SELECT * FROM notifications 
WHERE user_id = 'user123' 
AND status = 'PENDING' 
ORDER BY created_at DESC;
```

### 3. Partitioning (cho production)
```sql
-- Partition theo thời gian
CREATE TABLE notifications_2024 PARTITION OF notifications
FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

CREATE TABLE notifications_2025 PARTITION OF notifications
FOR VALUES FROM ('2025-01-01') TO ('2026-01-01');
```

## Monitoring và Maintenance

### 1. Database Size
```sql
-- Kiểm tra kích thước database
SELECT 
    pg_size_pretty(pg_database_size('notification_db')) as db_size;

-- Kiểm tra kích thước tables
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

### 2. Index Usage
```sql
-- Kiểm tra index usage
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes 
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
```

### 3. Slow Queries
```sql
-- Enable query logging
ALTER SYSTEM SET log_statement = 'all';
ALTER SYSTEM SET log_min_duration_statement = 1000; -- 1 second
SELECT pg_reload_conf();
```

## Backup và Recovery

### 1. Backup
```bash
# Full backup
pg_dump -h localhost -U postgres -d notification_db > backup.sql

# Compressed backup
pg_dump -h localhost -U postgres -d notification_db | gzip > backup.sql.gz

# Docker backup
docker exec notification-postgres pg_dump -U postgres notification_db > backup.sql
```

### 2. Restore
```bash
# Restore from backup
psql -h localhost -U postgres -d notification_db < backup.sql

# Restore compressed backup
gunzip -c backup.sql.gz | psql -h localhost -U postgres -d notification_db
```

## Troubleshooting

### 1. Connection Issues
```bash
# Kiểm tra PostgreSQL service
sudo systemctl status postgresql

# Kiểm tra port
netstat -an | grep 5432

# Test connection
psql -h localhost -U postgres -d notification_db
```

### 2. Performance Issues
```sql
-- Kiểm tra active connections
SELECT * FROM pg_stat_activity WHERE state = 'active';

-- Kiểm tra locks
SELECT * FROM pg_locks WHERE NOT granted;

-- Kiểm tra slow queries
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;
```

### 3. Disk Space
```sql
-- Kiểm tra disk usage
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public';

-- Clean up old data
DELETE FROM notifications 
WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '1 year';
```

## Migration từ MySQL

### 1. Export từ MySQL
```bash
mysqldump -u root -p notification_db > mysql_backup.sql
```

### 2. Convert và Import
```bash
# Sử dụng tool chuyển đổi
mysql2postgresql mysql_backup.sql postgresql_backup.sql

# Import vào PostgreSQL
psql -h localhost -U postgres -d notification_db < postgresql_backup.sql
```

### 3. Verify Data
```sql
-- Kiểm tra số lượng records
SELECT COUNT(*) FROM notifications;
SELECT COUNT(*) FROM notification_templates;

-- Kiểm tra data integrity
SELECT type, COUNT(*) FROM notifications GROUP BY type;
```

## Production Considerations

### 1. Security
```sql
-- Tạo user riêng cho application
CREATE USER notification_app WITH PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE notification_db TO notification_app;
GRANT USAGE ON SCHEMA public TO notification_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO notification_app;
```

### 2. Replication
```sql
-- Setup streaming replication
-- Primary server
wal_level = replica
max_wal_senders = 3
max_replication_slots = 3

-- Standby server
primary_conninfo = 'host=primary_server port=5432 user=repl password=password'
```

### 3. Monitoring
- Setup pgAdmin hoặc Grafana dashboard
- Configure alerts cho disk space, connections
- Monitor query performance
- Setup automated backups 