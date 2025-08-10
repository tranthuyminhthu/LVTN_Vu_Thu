package org.example.productsservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.dto.ImageDto;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private String status;
    private Integer viewCount;
    private Integer buyCount;
    private Boolean isFavorite;
    private List<ProductVariantEntity> variants;
    private List<String> images;
    private VendorInfoDto vendorInfo;
}
