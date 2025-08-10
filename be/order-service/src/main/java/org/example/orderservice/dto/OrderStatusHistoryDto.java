package org.example.orderservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryDto {
    private String status;
    private String note;
    private LocalDateTime changedAt;
} 