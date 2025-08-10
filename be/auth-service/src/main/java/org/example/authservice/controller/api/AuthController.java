package org.example.authservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.LoginResponseDto;
import org.example.authservice.dto.RegistrationDto;
import org.example.authservice.dto.VendorRequestDto;
import org.example.authservice.dto.ChangePasswordDto;
import org.example.authservice.dto.ForgotPasswordDto;
import org.example.authservice.dto.ResetPasswordDto;
import org.example.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> createProfile(@RequestBody RegistrationDto registrationDto) {
        Map<String, String> errors;
        try {
            String userId = authService.registerAccount(registrationDto);
            URI location = URI.create("/internal/profile/" + userId);
            return ResponseEntity.created(location).body("");
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (IllegalStateException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.status(409).body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while creating the profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Map<String, String> errors;
        try {
            LoginResponseDto response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> tokenExchangeParams) {
        Map<String, String> errors;
        try {
            String accessToken = authService.refreshToken(tokenExchangeParams.get("refreshToken"));
            return ResponseEntity.ok(Map.of("accessToken", accessToken));
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while exchanging the token: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        Map<String, String> errors;
        try {
            authService.requestPasswordReset(forgotPasswordDto.getEmail());
            return ResponseEntity.ok(Map.of(
                "message", "If an account with that email exists, a password reset link has been sent."
            ));
        } catch (Exception e) {
            // Don't expose whether email exists or not for security
            return ResponseEntity.ok(Map.of(
                "message", "If an account with that email exists, a password reset link has been sent."
            ));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Map<String, String> errors;
        try {
            // Get userId from current user context (you'll need to implement this based on your security setup)
            String userId = getCurrentUserId(); // This method needs to be implemented
            authService.changePassword(userId, changePasswordDto);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while changing password: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        Map<String, String> errors;
        try {
            authService.resetPassword(resetPasswordDto);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while resetting password: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    // Helper method to get current user ID - you'll need to implement this based on your security setup
    private String getCurrentUserId() {
        // This should extract the user ID from the current security context
        // For now, returning a placeholder - you'll need to implement this
        throw new UnsupportedOperationException("getCurrentUserId() needs to be implemented based on your security setup");
    }
}
