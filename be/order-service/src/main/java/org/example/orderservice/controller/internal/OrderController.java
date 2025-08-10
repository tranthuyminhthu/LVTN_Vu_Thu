package org.example.orderservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.VendorRevenueStatsDto;
import org.example.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/internal")
@RestController("orderInternalController")
public class OrderController {
    private final OrderService orderService;

    /**
     * Lấy thống kê doanh thu cho vendor theo khoảng thời gian
     */
    @GetMapping("/vendors/revenue-stats")
    public ResponseEntity<?> getVendorRevenueStats(
            @RequestParam String vendorId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            VendorRevenueStatsDto stats = orderService.getVendorRevenueStats(vendorId, startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu theo tháng cho vendor
     */
    @GetMapping("/vendors/revenue-stats/monthly")
    public ResponseEntity<?> getVendorMonthlyRevenueStats(
            @RequestParam String vendorId,
            @RequestParam Integer year) {
        try {
            VendorRevenueStatsDto stats = orderService.getVendorMonthlyRevenueStats(vendorId, year);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving monthly revenue statistics: " + e.getMessage()
            ));
        }
    }
} 