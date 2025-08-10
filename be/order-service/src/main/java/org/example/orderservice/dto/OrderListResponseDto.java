package org.example.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderListResponseDto {
    private List<OrderResponseDto> orders;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
} 