package org.example.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequestDto {
    private List<OrderItemDto> items;
    private String shippingAddress;
    private String paymentMethod;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String note;
    private Double fee;
    private String vendorId;
    private Integer receiverDistrictId;
    private String receiverWardCode;

    @Data
    public static class OrderItemDto {
        private String cartItemId;
        private String productSku;
        private String productName;
        private Integer quantity;
        private Double price;
        private String image;
    }
} 
