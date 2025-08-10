package org.example.userservice.dao;

import org.example.userservice.dto.VendorRevenueStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@FeignClient(name = "order-client", url = "${service.order-service}")
public interface OrderClient {
    
    @GetMapping(value = "/internal/vendors/revenue-stats", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<VendorRevenueStatsDto> getVendorRevenueStats(
        @RequestParam String vendorId,
        @RequestParam LocalDateTime startDate,
        @RequestParam LocalDateTime endDate
    );
    
    @GetMapping(value = "/internal/vendors/revenue-stats/monthly", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<VendorRevenueStatsDto> getVendorMonthlyRevenueStats(
        @RequestParam String vendorId,
        @RequestParam Integer year
    );
} 