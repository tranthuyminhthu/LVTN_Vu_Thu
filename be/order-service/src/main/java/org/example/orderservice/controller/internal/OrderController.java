package org.example.orderservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.VendorRevenueStatsDto;
import org.example.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime startDateTime = parseDateTime(startDate);
            LocalDateTime endDateTime = parseDateTime(endDate);
            
            VendorRevenueStatsDto stats = orderService.getVendorRevenueStats(vendorId, startDateTime, endDateTime);
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

    /**
     * Lấy thống kê doanh thu toàn bộ cho vendor (không filter theo ngày)
     */
    @GetMapping("/vendors/revenue-stats/total")
    public ResponseEntity<?> getVendorTotalRevenueStats(
            @RequestParam String vendorId) {
        try {
            VendorRevenueStatsDto stats = orderService.getVendorTotalRevenueStats(vendorId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "An error occurred while retrieving total revenue statistics: " + e.getMessage()
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