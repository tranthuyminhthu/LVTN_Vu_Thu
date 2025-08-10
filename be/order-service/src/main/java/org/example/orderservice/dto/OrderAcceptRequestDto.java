package org.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderAcceptRequestDto {
    private String orderId;
    private String note;
    private String requiredNote;
    private String content;
    private Integer serviceId;
    private String coupon;
}
