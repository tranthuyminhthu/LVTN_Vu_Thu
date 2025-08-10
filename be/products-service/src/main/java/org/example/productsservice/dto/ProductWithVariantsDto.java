package org.example.productsservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductWithVariantsDto {
    private String name;
    private String description;
    private Double price;
    private List<ProductVariantRequestDto> variants;
} 