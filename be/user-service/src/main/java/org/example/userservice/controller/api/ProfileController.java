package org.example.userservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.*;
import org.example.userservice.entity.ProfileEntity;
import org.example.userservice.service.ProfileService;
import org.example.userservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController("apiProfileController")
@RequiredArgsConstructor
@RequestMapping()
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUserProfile() {
        // Log current user roles
        UserContext.logCurrentUserRoles();
        
        Map<String, String> errors;
        try {
            ProfileEntity profile = profileService.getCurrentUserProfile();
            profile.setRole(UserContext.getCurrentUserRoles());
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while retrieving the current user profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @GetMapping()
    public ResponseEntity<ProfileListResponseDto> getProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ProfileListResponseDto response = profileService.getProfiles(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequestDto profile) {
        try {
            ProfileEntity updatedProfile = profileService.updateProfile(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred while updating the profile: " + e.getMessage()));
        }
    }

    @GetMapping("introspect")
    public ResponseEntity<?> introspectProfile() {
        try {
            String userId = UserContext.getCurrentUserId();
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("User ID is not available in the context.");
            }
            return ResponseEntity.noContent()
                    .location(URI.create("/internal/profile/" + userId))
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred while introspecting the profile: " + e.getMessage()));
        }
    }
}
