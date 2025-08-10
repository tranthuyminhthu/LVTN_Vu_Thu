package org.example.authservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authservice.dao.CartClient;
import org.example.authservice.dao.IdentityClient;
import org.example.authservice.dao.NotificationClient;
import org.example.authservice.dao.PasswordResetTokenDao;
import org.example.authservice.dao.ProfileClient;
import org.example.authservice.dto.*;
import org.example.authservice.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final IdentityClient identityClient;
    private final ProfileClient profileClient;
    private final CartClient cartClient;
    private final NotificationClient notificationClient;
    private final PasswordResetTokenDao passwordResetTokenDao;
    @Value("${fe.url}")
    private String feUrl;

    @Value("${idp.client-id}")
    private String clientId;

    @Value("${idp.client-secret}")
    private String clientSecret;

    public String registerAccount(RegistrationDto registrationDto) {
        String userId = null;
        try {
            // exchange client token
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build();
            String token = identityClient.exchangeToken(tokenExchangeParam).getAccessToken();
            
            // Create user in keycloak
            ResponseEntity<?> response = identityClient.createUser(
                    UserCreationParamDto.builder()
                            .username(registrationDto.getUsername())
                            .email(registrationDto.getEmail())
                            .firstName("temp")
                            .lastName("temp")
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(List.of(
                                    CredentialDto.builder()
                                            .type("password")
                                            .value(registrationDto.getPassword())
                                            .temporary(false)
                                            .build()
                            ))
                            .build(), "Bearer " + token
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusCode());
            }

            // Get userId from keycloak response
            userId = extractUserId(response);
            
            // Get roles and assign role to user
            try {
                List<RoleResponseDto> roles = identityClient.getRoles("Bearer " + token).getBody();
                if (roles == null) {
                    throw new RuntimeException("Failed to get roles from Keycloak");
                }
                
                RoleResponseDto currentRole = roles.stream()
                        .filter(role -> registrationDto.getIsVendor() ? role.getName().equals("VENDOR") : role.getName().equals("USER"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Role not found in Keycloak"));
                
                identityClient.assignRole("Bearer " + token, List.of(currentRole), userId);
            } catch (Exception e) {
                // If role assignment fails, delete the user from Keycloak
                if (userId != null) {
                    try {
                        identityClient.deleteUser("Bearer " + token, userId);
                        log.warn("Deleted user {} from Keycloak due to role assignment failure", userId);
                    } catch (Exception deleteException) {
                        log.error("Failed to delete user {} from Keycloak after role assignment failure", userId, deleteException);
                    }
                }
                throw new RuntimeException("Failed to assign role to user: " + e.getMessage(), e);
            }
            
            // Create cart for user (only for non-vendor users)
            if (!registrationDto.getIsVendor()) {
                try {
                    ProfileDto profileDto = ProfileDto.builder()
                            .userId(userId)
                            .username(registrationDto.getUsername())
                            .email(registrationDto.getEmail())
                            .build();
                    profileClient.createProfile(profileDto);
                    cartClient.createCart(userId);
                } catch (Exception e) {
                    // If cart creation fails, delete the user from Keycloak
                    try {
                        identityClient.deleteUser("Bearer " + token, userId);
                        log.warn("Deleted user {} from Keycloak due to cart creation failure", userId);
                    } catch (Exception deleteException) {
                        log.error("Failed to delete user {} from Keycloak after cart creation failure", userId, deleteException);
                    }
                    throw new RuntimeException("Failed to create cart for user: " + e.getMessage(), e);
                }
            } else {
                VendorRequestDto vendorRequestDto = new VendorRequestDto();
                vendorRequestDto.setId(userId);
                profileClient.createVendor(vendorRequestDto);
            }
            
            return userId;
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid request data: " + e.getMessage());
                case 409 -> throw new IllegalStateException("User already exists: " + e.getMessage());
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().getLocation().toString();
        List<String> locationParts = List.of(location.split("/"));
        return locationParts.getLast();
    }

    public LoginResponseDto login(LoginDto loginDto) {
        try {
            // Call Keycloak login endpoint
            TokenExchangeParamDto loginParam = TokenExchangeParamDto.builder()
                    .grant_type("password")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .username(loginDto.getUsername())
                    .password(loginDto.getPassword())
                    .scope("openid")
                    .build();
            
            TokenExchangeResponseDto response = identityClient.login(loginParam);
            // Return the login response
            ProfileDto profile = profileClient.getProfileByUserName(loginDto.getUsername()).getBody();
            
            // Log user login with role information
            if (profile != null) {
                log.info("Auth Service - User {} (email: {}) logged in successfully", 
                        profile.getUserId(), 
                        profile.getEmail() != null ? profile.getEmail() : "N/A");
            } else {
                log.info("Auth Service - User {} logged in successfully (profile not found)", 
                        loginDto.getUsername());
            }
            
            return LoginResponseDto.builder()
                    .accessToken(response.getAccessToken())
                    .refreshToken(response.getRefreshToken())
                    .tokenType(response.getTokenType())
                    .userProfile(profile)
                    .expiresIn(Long.parseLong(response.getExpiresIn()))
                    .build();
                    
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid credentials: " + e.getMessage());
                case 401 -> throw new IllegalArgumentException("Invalid username or password");
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    public String refreshToken(String refreshToken) {
        try {
            // Call Keycloak token exchange endpoint
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("refresh_token")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .refresh_token(refreshToken)
                    .build();

            TokenExchangeResponseDto response = identityClient.exchangeToken(tokenExchangeParam);
            return response.getAccessToken();
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid refresh token: " + e.getMessage());
                case 401 -> throw new IllegalArgumentException("Refresh token expired or invalid");
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }

    public void requestPasswordReset(String email) {
        try {
            // Get client token first
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build();
            String adminToken = identityClient.exchangeToken(tokenExchangeParam).getAccessToken();
            
            // Find user by email in Keycloak
            ResponseEntity<List<UserDto>> userResponse = identityClient.getUserByEmail("Bearer " + adminToken, email);
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                // Don't throw exception to avoid email enumeration attack
                log.info("Password reset requested for non-existent email: {}", email);
                return;
            }
            
            // Generate custom reset token
            String resetToken = generateResetToken();
            
            // Save reset token to database
            PasswordResetToken tokenEntity = PasswordResetToken.builder()
                    .token(resetToken)
                    .email(email)
                    .expiryDate(java.time.LocalDateTime.now().plusHours(24))
                    .used(false)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            passwordResetTokenDao.save(tokenEntity);
            
            // Send password reset email via Notification Service
            sendPasswordResetEmail(userResponse, resetToken);
            
            log.info("Password reset email sent to: {}", email);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email for: {}", email, e);
            // Don't throw exception to avoid email enumeration attack
        }
    }
    
    public void changePassword(String userId, ChangePasswordDto changePasswordDto) {
        try {
            // Validate passwords match
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
                throw new IllegalArgumentException("New password and confirm password do not match");
            }
            
            // Get client token
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build();
            String token = identityClient.exchangeToken(tokenExchangeParam).getAccessToken();
            
            // Update password in Keycloak
            ResponseEntity<?> response = identityClient.updatePassword(
                "Bearer " + token, 
                userId, 
                changePasswordDto.getCurrentPassword(),
                changePasswordDto.getNewPassword()
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("Failed to change password. Please check your current password.");
            }
            
            // Get user profile for notification
            ProfileDto profile = profileClient.getProfileByUserId(userId).getBody();
            if (profile != null) {
                sendPasswordChangeNotification(profile);
            }
            
            log.info("Password changed successfully for user: {}", userId);
            
        } catch (FeignException e) {
            int status = e.status();
            switch (status) {
                case 400 -> throw new IllegalArgumentException("Invalid password data: " + e.getMessage());
                case 401 -> throw new IllegalArgumentException("Current password is incorrect");
                case 404 -> throw new IllegalArgumentException("User not found");
                case 500 -> throw new RuntimeException("Internal server error: " + e.getMessage());
                default -> throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
            }
        }
    }
    
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        try {
            // Validate passwords match
            if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
                throw new IllegalArgumentException("New password and confirm password do not match");
            }
            
            // Find and validate reset token
            PasswordResetToken tokenEntity = passwordResetTokenDao
                    .findByTokenAndUsedFalseAndExpiryDateAfter(
                        resetPasswordDto.getToken(), 
                        java.time.LocalDateTime.now()
                    )
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
            
            // Get client token for Keycloak admin operations
            TokenExchangeParamDto tokenExchangeParam = TokenExchangeParamDto.builder()
                    .grant_type("client_credentials")
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .scope("openid")
                    .build();
            String adminToken = identityClient.exchangeToken(tokenExchangeParam).getAccessToken();
            
            // Find user by email in Keycloak
            ResponseEntity<List<UserDto>> userResponse = identityClient.getUserByEmail("Bearer " + adminToken, tokenEntity.getEmail());
            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("User not found");
            }
            
            // Parse user ID from response
            String userId = extractUserIdFromResponse(userResponse);
            
            // Update password in Keycloak using admin API
            Map<String, String> passwordData = Map.of("value", resetPasswordDto.getNewPassword());
            ResponseEntity<?> response = identityClient.adminResetPassword(
                "Bearer " + adminToken, 
                userId, 
                passwordData
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalArgumentException("Failed to reset password");
            }
            
            // Mark token as used
            tokenEntity.setUsed(true);
            passwordResetTokenDao.save(tokenEntity);
            
            log.info("Password reset successfully for user: {}", userId);
            
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            log.error("Failed to reset password: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while resetting password: " + e.getMessage());
        }
    }
    
    private void sendPasswordResetEmail(ResponseEntity<List<UserDto>> body, String token) {
        UserDto userDto = body.getBody().getFirst();
        try {
            // Create notification request
            Map<String, Object> notification = Map.of(
                "userId", userDto.getId(),
                "type", "EMAIL",
                "category", "SECURITY",
                "title", "Password Reset Request",
                "content", "You requested a password reset for your account. Click the link below to reset your password.",
                "recipient", userDto.getEmail(),
                "data", Map.of(
                    "firstName", "User",
                    "resetLink", feUrl + "/reset-password?token=" + token,
                    "expiryHours", "24"
                )
            );
            
            // Call Notification Service
            ResponseEntity<?> response = notificationClient.sendNotification(notification);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Password reset email sent successfully to: ");
            } else {
                log.warn("Failed to send password reset email to: status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to:", e);
        }
    }
    
    private void sendPasswordChangeNotification(ProfileDto profile) {
        try {
            // Create notification request for password change confirmation
            Map<String, Object> notification = Map.of(
                "userId", profile.getUserId(),
                "type", "EMAIL",
                "category", "SECURITY",
                "title", "Password Changed Successfully",
                "content", "Your password has been successfully changed. If you didn't request this change, please contact support immediately.",
                "recipient", profile.getEmail(),
                "data", Map.of(
                    "firstName", "User",
                    "changeTime", java.time.LocalDateTime.now().toString(),
                    "supportEmail", "support@example.com"
                )
            );
            
            // Call Notification Service
            ResponseEntity<?> response = notificationClient.sendNotification(notification);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Password change notification sent successfully to: {}", profile.getEmail());
            } else {
                log.warn("Failed to send password change notification to: {}, status: {}", profile.getEmail(), response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Failed to send password change notification to: {}", profile.getEmail(), e);
        }
    }
    
    private String generateResetToken() {
        // Generate a secure random token
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
    
    private String extractUserIdFromResponse(ResponseEntity<List<UserDto>> response) {
        try {
            UserDto userDto = response.getBody().getFirst();
            if (userDto == null || userDto.getId() == null) {
                throw new IllegalArgumentException("User not found in Keycloak response");
            }
            return userDto.getId();
            
        } catch (Exception e) {
            log.error("Failed to extract user ID from response", e);
            throw new RuntimeException("Failed to extract user ID: " + e.getMessage());
        }
    }
}
