# 🧪 Password Management API Testing Guide

## 📋 Test Environment Setup

### 1. **Start Services**
```bash
# Start Keycloak
docker-compose -f auth-service/docker-compose-keycloak.yml up -d

# Start Notification Service
cd notification_service
./gradlew bootRun

# Start Auth Service
cd ../auth-service
./gradlew bootRun
```

### 2. **Verify Services**
```bash
# Check Keycloak
curl http://localhost:8180/health

# Check Notification Service
curl http://localhost:8086/actuator/health

# Check Auth Service
curl http://localhost:8082/auth/actuator/health
```

## 🧪 API Tests

### 1. **Test Forgot Password API**

#### Request:
```bash
curl -X POST http://localhost:8082/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

#### Expected Response:
```json
{
  "message": "If an account with that email exists, a password reset link has been sent."
}
```

### 2. **Test Change Password API**

#### Request:
```bash
curl -X POST http://localhost:8082/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "oldPassword123!",
    "newPassword": "newPassword123!",
    "confirmPassword": "newPassword123!"
  }'
```

#### Expected Response:
```json
{
  "message": "Password changed successfully"
}
```

### 3. **Test Reset Password API**

#### Request:
```bash
curl -X POST http://localhost:8082/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset_token_here",
    "newPassword": "newPassword123!",
    "confirmPassword": "newPassword123!"
  }'
```

#### Expected Response:
```json
{
  "message": "Password reset successfully"
}
```

## 🔍 Validation Tests

### 1. **Password Strength Validation**

#### Test Weak Password:
```bash
curl -X POST http://localhost:8082/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "oldPassword123!",
    "newPassword": "weak",
    "confirmPassword": "weak"
  }'
```

#### Expected Response:
```json
{
  "message": "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"
}
```

### 2. **Password Confirmation Mismatch**

#### Test:
```bash
curl -X POST http://localhost:8082/auth/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "oldPassword123!",
    "newPassword": "newPassword123!",
    "confirmPassword": "differentPassword123!"
  }'
```

#### Expected Response:
```json
{
  "message": "New password and confirm password do not match"
}
```

### 3. **Invalid Email Format**

#### Test:
```bash
curl -X POST http://localhost:8082/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email"
  }'
```

#### Expected Response:
```json
{
  "message": "Invalid email format"
}
```

## 📧 Email Notification Tests

### 1. **Check Notification Service Logs**
```bash
# Check if password reset email was sent
docker logs notification-service | grep "Password Reset Request"

# Check if password change notification was sent
docker logs notification-service | grep "Password Changed Successfully"
```

### 2. **Check Notification Database**
```bash
# Connect to PostgreSQL
docker exec -it notification-postgres psql -U postgres -d notification_db

# Check notifications
SELECT type, category, title, status, created_at 
FROM notifications 
WHERE category = 'SECURITY' 
ORDER BY created_at DESC 
LIMIT 10;
```

## 🔒 Security Tests

### 1. **Email Enumeration Protection**

#### Test with Non-existent Email:
```bash
curl -X POST http://localhost:8082/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com"
  }'
```

#### Expected Response (Same as valid email):
```json
{
  "message": "If an account with that email exists, a password reset link has been sent."
}
```

### 2. **Invalid Token Test**

#### Test:
```bash
curl -X POST http://localhost:8082/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "invalid_token",
    "newPassword": "newPassword123!",
    "confirmPassword": "newPassword123!"
  }'
```

#### Expected Response:
```json
{
  "message": "Invalid or expired reset token"
}
```

## 🐛 Troubleshooting

### 1. **Service Connection Issues**
```bash
# Check if all services are running
docker ps

# Check service logs
docker logs auth-service
docker logs notification-service
docker logs keycloak
```

### 2. **Database Connection Issues**
```bash
# Check PostgreSQL connection
docker exec -it notification-postgres psql -U postgres -d notification_db -c "SELECT 1;"

# Check Keycloak database
docker exec -it keycloak-db psql -U keycloak -d keycloak -c "SELECT 1;"
```

### 3. **Network Issues**
```bash
# Test service connectivity
curl http://localhost:8082/auth/actuator/health
curl http://localhost:8086/actuator/health
curl http://localhost:8180/health
```

## 📊 Monitoring

### 1. **Check API Metrics**
```bash
# Auth Service metrics
curl http://localhost:8082/auth/actuator/metrics

# Notification Service metrics
curl http://localhost:8086/actuator/metrics
```

### 2. **Check Logs**
```bash
# Auth Service logs
tail -f auth-service.log | grep "password"

# Notification Service logs
tail -f notification-service.log | grep "SECURITY"
```

## ✅ Success Criteria

- [ ] Forgot password API returns success message
- [ ] Change password API validates current password
- [ ] Reset password API validates token
- [ ] Email notifications are sent successfully
- [ ] Password strength validation works
- [ ] Email enumeration protection works
- [ ] All error cases return appropriate messages
- [ ] Security headers are present
- [ ] Rate limiting works (if implemented) 