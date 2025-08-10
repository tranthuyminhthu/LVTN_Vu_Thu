package org.example.productsservice.dto;

import lombok.Data;

@Data
public class ImageDto {
    private Long id;
    private String url;
    private Long productId;
    private Integer order;
} 