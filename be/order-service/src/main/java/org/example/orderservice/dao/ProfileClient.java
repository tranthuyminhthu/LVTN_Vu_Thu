package org.example.orderservice.dao;

import org.example.orderservice.dto.VendorResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "profile-client", url = "${service.profile-service}")
public interface ProfileClient {

    @GetMapping(value = "/internal/profile/vendors/{vendorId}")
    ResponseEntity<VendorResponseDto> getVendorProfile(@PathVariable String vendorId);
}
