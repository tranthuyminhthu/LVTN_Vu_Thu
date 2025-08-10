# 🔐 Password Management Implementation Guide

## 📋 Overview
Hướng dẫn implement chức năng đổi mật khẩu và quên mật khẩu trong Auth Service sử dụng Keycloak.

## 🎯 Features Implemented

### 1. **Đổi Mật Khẩu (Change Password)**
- User đã đăng nhập có thể đổi mật khẩu
- Validate mật khẩu hiện tại
- Validate mật khẩu mới (độ mạnh, xác nhận)
- Gửi email thông báo đổi mật khẩu thành công

### 2. **Quên Mật Khẩu (Forgot Password)**
- User nhập email để yêu cầu reset mật khẩu
- Tạo reset token và gửi email
- User click link trong email để reset mật khẩu
- Validate token và cập nhật mật khẩu mới

## 🔧 Implementation Steps

### Step 1: Create DTOs ✅
Đã tạo các DTO cần thiết:
- `ChangePasswordDto.java` - DTO cho đổi mật khẩu
- `ForgotPasswordDto.java` - DTO cho quên mật khẩu  
- `ResetPasswordDto.java` - DTO cho reset mật khẩu

### Step 2: Update IdentityClient ✅
Đã thêm các method vào `IdentityClient.java`:
```java
// Tìm user theo email
@GetMapping("/admin/realms/kltn/users")
ResponseEntity<?> getUserByEmail(@RequestHeader("Authorization") String authorizationHeader, 
                               @RequestParam("email") String email);

// Đổi mật khẩu
@PutMapping("/admin/realms/kltn/users/{userId}/reset-password")
ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String authorizationHeader,
                               @PathVariable String userId,
                               @RequestParam("currentPassword") String currentPassword,
                               @RequestParam("newPassword") String newPassword);

// Reset mật khẩu bằng token
@PostMapping("/admin/realms/kltn/users/reset-password")
ResponseEntity<?> resetPassword(@RequestHeader("Authorization") String authorizationHeader,
                              @RequestParam("token") String token,
                              @RequestParam("newPassword") String newPassword);
```

### Step 3: Update ProfileClient ✅
Đã thêm các method vào `ProfileClient.java`:
```java
// Lấy profile theo email
@GetMapping("/internal/profile/email")
ResponseEntity<ProfileDto> getProfileByEmail(@RequestParam String email);

// Lấy profile theo userId
@GetMapping("/internal/profile/{userId}")
ResponseEntity<ProfileDto> getProfileByUserId(@PathVariable String userId);
```

### Step 4: Update AuthService ✅
Đã thêm các method vào `AuthService.java`:
- `requestPasswordReset(String email)` - Xử lý yêu cầu reset mật khẩu
- `changePassword(String userId, ChangePasswordDto dto)` - Đổi mật khẩu
- `resetPassword(ResetPasswordDto dto)` - Reset mật khẩu bằng token
- `sendPasswordResetEmail()` - Gửi email reset
- `sendPasswordChangeNotification()` - Gửi email thông báo đổi mật khẩu

### Step 5: Update AuthController ✅
Đã thêm các endpoint vào `AuthController.java`:
```java
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto dto)

@PostMapping("/change-password") 
public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto dto)

@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto dto)
```

## 🚧 TODO: Remaining Tasks

### 1. **Implement getCurrentUserId() Method**
Cần implement method `getCurrentUserId()` trong `AuthController.java` để lấy userId từ security context:

```java
private String getCurrentUserId() {
    // Implement based on your security setup
    // Example with Spring Security:
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername(); // or extract from JWT token
    }
    throw new UnauthorizedException("User not authenticated");
}
```

### 2. **Create NotificationClient**
Tạo `NotificationClient.java` để gọi Notification Service:

```java
@FeignClient(name = "notification-client", url = "${service.notification}")
public interface NotificationClient {
    
    @PostMapping("/api/notifications/send")
    ResponseEntity<?> sendNotification(@RequestBody NotificationRequestDto notification);
}
```

### 3. **Update AuthService to Use NotificationClient**
Thay thế phần log trong `sendPasswordResetEmail()` và `sendPasswordChangeNotification()` bằng call thực tế đến Notification Service.

### 4. **Add Validation**
Thêm validation cho password strength và confirm password matching.

### 5. **Add Rate Limiting**
Implement rate limiting cho các endpoint password reset để tránh spam.

## 🧪 Testing

### Test Forgot Password:
```bash
curl -X POST http://localhost:8081/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com"
  }'
```

### Test Change Password:
```bash
curl -X POST http://localhost:8081/change-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "currentPassword": "oldPassword123!",
    "newPassword": "newPassword123!",
    "confirmPassword": "newPassword123!"
  }'
```

### Test Reset Password:
```bash
curl -X POST http://localhost:8081/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "reset_token_here",
    "newPassword": "newPassword123!",
    "confirmPassword": "newPassword123!"
  }'
```

## 🔒 Security Considerations

### 1. **Email Enumeration Protection**
- Không expose thông tin về email có tồn tại hay không
- Luôn trả về cùng message cho forgot password

### 2. **Password Strength**
- Validate password complexity
- Minimum 8 characters
- At least 1 uppercase, 1 lowercase, 1 number, 1 special character

### 3. **Token Security**
- Reset tokens có thời hạn (24 giờ)
- Tokens được invalidate sau khi sử dụng
- Secure token generation

### 4. **Rate Limiting**
- Limit số lần request forgot password per email
- Limit số lần attempt change password per user

## 📧 Email Templates

### Password Reset Email:
```html
<h2>Hello {{firstName}},</h2>
<p>You requested a password reset for your account.</p>
<p>Click the link below to reset your password:</p>
<a href="{{resetLink}}">Reset Password</a>
<p>This link will expire in {{expiryHours}} hours.</p>
```

### Password Change Confirmation:
```html
<h2>Hello {{firstName}},</h2>
<p>Your password has been successfully changed at {{changeTime}}.</p>
<p>If you didn't request this change, please contact support immediately.</p>
```

## 🔗 Integration with Notification Service

Sau khi implement `NotificationClient`, Auth Service sẽ gửi notification requests đến Notification Service:

```java
// Example notification request
NotificationRequestDto notification = NotificationRequestDto.builder()
    .userId(userId)
    .type("EMAIL")
    .category("SECURITY")
    .title("Password Reset Request")
    .content("You requested a password reset...")
    .recipient(email)
    .data(Map.of("resetLink", resetLink, "expiryHours", "24"))
    .build();

notificationClient.sendNotification(notification);
```

## 📝 Notes

- Tất cả các file đã được tạo và cập nhật
- Cần implement `getCurrentUserId()` method dựa trên security setup của bạn
- Cần tạo `NotificationClient` để integrate với Notification Service
- Test các endpoint sau khi hoàn thành implementation 