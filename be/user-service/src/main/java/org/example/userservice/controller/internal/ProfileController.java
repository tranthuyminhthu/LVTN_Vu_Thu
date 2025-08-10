package org.example.userservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.LoginDto;
import org.example.userservice.dto.LoginResponseDto;
import org.example.userservice.dto.RegistrationDto;
import org.example.userservice.entity.ProfileEntity;
import org.example.userservice.service.ProfileService;
import org.example.userservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController("internalProfileController")
@RequiredArgsConstructor
@RequestMapping("/internal/profile")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody RegistrationDto registrationDto) {
        Map<String, String> errors;
        try {
            ProfileEntity profile = profileService.createProfile(registrationDto);
            URI location = URI.create("/internal/profile/" + profile.getUserId());
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

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfileById(@PathVariable String userId) {
        Map<String, String> errors;
        try {
            ProfileEntity profile = profileService.getProfileById(userId);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while retrieving the profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getProfileByUserName(@RequestParam String userName) {
        Map<String, String> errors;
        try {
            ProfileEntity profile = profileService.getProfileByUserName(userName);
            profile.setRole(UserContext.getCurrentUserRoles());
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while retrieving the profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUserProfile() {
        Map<String, String> errors;
        try {
            ProfileEntity profile = profileService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred while retrieving the current user profile: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }


}
