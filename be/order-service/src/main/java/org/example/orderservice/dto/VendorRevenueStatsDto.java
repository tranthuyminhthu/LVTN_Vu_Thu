package org.example.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VendorRevenueStatsDto {
    private String vendorId;
    private String vendorName;
    private Double totalRevenue;
    private Long totalOrders;
    private Double averageOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<MonthlyRevenueDto> monthlyRevenue;
    private List<DailyRevenueDto> dailyRevenue;
    private List<TopProductDto> topProducts;
    
    // Thống kê theo trạng thái đơn hàng
    private List<OrderStatusStatsDto> orderStatusStats;
    
    @Data
    public static class MonthlyRevenueDto {
        private Integer year;
        private Integer month;
        private Double revenue;
        private Long orderCount;
    }
    
    @Data
    public static class DailyRevenueDto {
        private LocalDateTime date;
        private Double revenue;
        private Long orderCount;
    }
    
    @Data
    public static class TopProductDto {
        private String productSku;
        private String productName;
        private Long quantitySold;
        private Double totalRevenue;
    }
    
    @Data
    public static class OrderStatusStatsDto {
        private String status;
        private Long count;
        private Double revenue;
    }
} 