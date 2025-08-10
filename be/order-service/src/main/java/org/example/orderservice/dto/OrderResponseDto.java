package org.example.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String status;
    private Double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentTransactionId;
    private LocalDateTime createdAt;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
} 