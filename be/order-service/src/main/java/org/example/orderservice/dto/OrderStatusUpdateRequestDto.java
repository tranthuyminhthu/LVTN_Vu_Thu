package org.example.orderservice.dto;

import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    private String status;
    private String note;
} 