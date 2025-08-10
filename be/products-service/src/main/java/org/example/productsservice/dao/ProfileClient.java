package org.example.productsservice.dao;

import org.example.productsservice.dto.VendorInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "profile-client", url = "${service.profile}")
public interface ProfileClient {
    @GetMapping(value = "/internal/profile/vendors/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<VendorInfoDto> getVendorById(@PathVariable String id);
}
