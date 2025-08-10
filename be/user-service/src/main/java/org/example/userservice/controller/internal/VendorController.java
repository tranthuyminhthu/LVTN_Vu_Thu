package org.example.userservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.VendorRequestDto;
import org.example.userservice.service.VendorService;
import org.example.userservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("vendors")
@RestController
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<?> getAllVendors() {
        try {
            return ResponseEntity.ok(vendorService.getAllVendors());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving vendors: " + e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> createVendor(@RequestBody VendorRequestDto vendor) {
        try {
            return ResponseEntity.ok(vendorService.createVendor(vendor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while creating the vendor: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserVendors() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "User ID cannot be null or empty"));
        }
        try {
            return ResponseEntity.ok(vendorService.getVendorById(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving the current user's vendors: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{vendorId}")
    public ResponseEntity<?> getVendorById(@PathVariable String vendorId) {
        try {
            return ResponseEntity.ok(vendorService.getVendorById(vendorId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving the vendor: " + e.getMessage()
            ));
        }
    }



}
