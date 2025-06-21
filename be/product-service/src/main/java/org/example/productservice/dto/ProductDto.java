package org.example.productservice.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.productservice.entity.ProductVariantEntity;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private String status;
    private List<ProductVariantEntity> variants;

}
