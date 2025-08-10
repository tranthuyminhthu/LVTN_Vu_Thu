package org.example.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequestDto {
    private Long userId;
    private List<OrderItemDto> items;
    private String shippingAddress;
    private String paymentMethod;

    @Data
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Double price;
    }
} 