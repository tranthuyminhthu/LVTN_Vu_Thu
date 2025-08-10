package org.example.productsservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductListResponseDto {
    private List<ProductDto> products;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
} 