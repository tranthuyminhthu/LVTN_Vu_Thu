package org.example.productsservice.dto;

import lombok.Data;

@Data
public class ProductItemQuantityDto {
    private String sku;
    private Integer quantity;
}
