package org.example.productsservice.dto;

import lombok.Data;

@Data
public class ProductVariantRequestDto {
    private String productId;
    private String size;
    private String colorName;
    private String colorHex;
    private Double price;
    private Integer stockQuantity;
} 