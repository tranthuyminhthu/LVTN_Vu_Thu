package org.example.userservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.userservice.annotation.RequireRole;
import org.example.userservice.dto.VendorRequestDto;
import org.example.userservice.dto.VendorListResponseDto;
import org.example.userservice.dto.VendorRevenueStatsDto;
import org.example.userservice.entity.VendorEntity;
import org.example.userservice.service.VendorService;
import org.example.userservice.util.UserContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("vendors")
@RestController("vendorApiController")
public class VendorController {
    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<VendorListResponseDto> getVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            VendorListResponseDto response = vendorService.getVendors(page, size, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
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

    /**
     * Lấy thống kê doanh thu cho vendor hiện tại theo khoảng thời gian
     */
    @GetMapping("/me/revenue-stats")
    public ResponseEntity<?> getCurrentVendorRevenueStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = parseDateTime(startDate);
            LocalDateTime endDateTime = parseDateTime(endDate);
            
            VendorRevenueStatsDto stats = vendorService.getCurrentVendorRevenueStats(startDateTime, endDateTime);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu theo tháng cho vendor hiện tại
     */
    @GetMapping("/me/revenue-stats/monthly")
    public ResponseEntity<?> getCurrentVendorMonthlyRevenueStats(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().getYear()}") Integer year) {
        try {
            VendorRevenueStatsDto stats = vendorService.getCurrentVendorMonthlyRevenueStats(year);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving monthly revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor hiện tại (không filter theo ngày)
     */
    @GetMapping("/me/revenue-stats/total")
    public ResponseEntity<?> getCurrentVendorTotalRevenueStats() {
        try {
            VendorRevenueStatsDto stats = vendorService.getCurrentVendorTotalRevenueStats();
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving total revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu cho vendor cụ thể theo khoảng thời gian (Admin only)
     */
    @GetMapping("/{vendorId}/revenue-stats")
    public ResponseEntity<?> getVendorRevenueStats(
            @PathVariable String vendorId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = parseDateTime(startDate);
            LocalDateTime endDateTime = parseDateTime(endDate);
            
            VendorRevenueStatsDto stats = vendorService.getVendorRevenueStats(vendorId, startDateTime, endDateTime);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu theo tháng cho vendor cụ thể (Admin only)
     */
    @GetMapping("/{vendorId}/revenue-stats/monthly")
    public ResponseEntity<?> getVendorMonthlyRevenueStats(
            @PathVariable String vendorId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().getYear()}") Integer year) {
        try {
            VendorRevenueStatsDto stats = vendorService.getVendorMonthlyRevenueStats(vendorId, year);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving monthly revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor cụ thể (Admin only)
     */
    @GetMapping("/{vendorId}/revenue-stats/total")
    public ResponseEntity<?> getVendorTotalRevenueStats(@PathVariable String vendorId) {
        try {
            VendorRevenueStatsDto stats = vendorService.getVendorTotalRevenueStats(vendorId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving total revenue statistics: " + e.getMessage()
            ));
        }
    }

    /**
     * Accept vendors - chuyển status từ PENDING sang ACCEPTED (Admin only)
     */
    @PostMapping("/accepted")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> acceptVendor(@RequestBody List<String> vendorIds) {
        try {
            vendorService.acceptVendor(vendorIds);
            return ResponseEntity.ok(Map.of("message", "Vendors accepted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "An error occurred while accepting the vendors: " + e.getMessage()
            ));
        }
    }

    /**
     * Parse string thành LocalDateTime với nhiều format khác nhau
     */
    private LocalDateTime parseDateTime(String dateTimeStr) {
        // Loại bỏ các ký tự đặc biệt và chuẩn hóa
        String normalized = dateTimeStr.replaceAll("[,\\s]+", " ").trim();
        
        // Các format được hỗ trợ
        DateTimeFormatter[] formatters = {
            // ISO 8601 formats
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_DATE_TIME,
            
            // Custom formats
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            
            // US format
            DateTimeFormatter.ofPattern("MM/dd/yy h:mm a"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a"),
            DateTimeFormatter.ofPattern("MM/dd/yy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            
            // Vietnamese format
            DateTimeFormatter.ofPattern("dd/MM/yy h:mm a"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a"),
            DateTimeFormatter.ofPattern("dd/MM/yy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                if (normalized.length() <= 10) {
                    // Chỉ có ngày, thêm thời gian mặc định
                    return LocalDateTime.parse(normalized + " 00:00", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } else {
                    return LocalDateTime.parse(normalized, formatter);
                }
            } catch (DateTimeParseException e) {
                // Tiếp tục thử format tiếp theo
                continue;
            }
        }

        throw new IllegalArgumentException("Không thể parse ngày tháng: " + dateTimeStr + 
            ". Vui lòng sử dụng format ISO 8601 (yyyy-MM-ddTHH:mm:ss) hoặc MM/dd/yyyy h:mm a");
    }
}
