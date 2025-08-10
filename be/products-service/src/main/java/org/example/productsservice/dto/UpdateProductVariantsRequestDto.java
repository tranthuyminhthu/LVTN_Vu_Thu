package org.example.productsservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdateProductVariantsRequestDto {
    private Long productId;
    private List<ProductVariantRequestDto> variants;
} 