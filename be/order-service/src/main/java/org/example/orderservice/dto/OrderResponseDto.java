package org.example.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private String orderId;
    private String userId;
    private Integer shopId;
    private String status;
    private Double totalAmount;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDto> items;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private Integer senderDistrictId;
    private String senderWardCode;
    private String receiverAddress;
    private String receiverWardCode;
    private Integer receiverDistrictId;
    private String note;
    private String paymentUrl;
    private List<OrderStatusHistoryDto> timeline;
    private String image;

    @Data
    public static class OrderItemDto {
        private String productSku;
        private String productName;
        private Integer quantity;
        private Double price;
        private String image;
    }
} 
