package org.example.userservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.LoginDto;
import org.example.userservice.dto.LoginResponseDto;
import org.example.userservice.dto.RegistrationDto;
import org.example.userservice.entity.ProfileEntity;
import org.example.userservice.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        Map<String, String> errors;
        try {
            LoginResponseDto response = profileService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            errors = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        } catch (Exception e) {
            errors = Map.of("message", "An error occurred during login: " + e.getMessage());
            return ResponseEntity.status(500).body(errors);
        }
    }
}
