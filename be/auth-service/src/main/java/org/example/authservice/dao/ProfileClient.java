package org.example.authservice.dao;

import org.example.authservice.dto.ProfileDto;
import org.example.authservice.dto.VendorRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "profile-client", url = "${service.profile}")
public interface ProfileClient {
    @PostMapping(value = "/internal/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createProfile(@RequestBody ProfileDto profileDto);

    @GetMapping(value = "/internal/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProfileDto> getProfileByUserName(@RequestParam String userName);

    @PostMapping(value = "/internal/profile/vendors", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createVendor(@RequestBody VendorRequestDto vendorRequestDto);

    @GetMapping(value = "/internal/profile/email", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProfileDto> getProfileByEmail(@RequestParam String email);

    @GetMapping(value = "/internal/profile/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProfileDto> getProfileByUserId(@PathVariable String userId);
}
