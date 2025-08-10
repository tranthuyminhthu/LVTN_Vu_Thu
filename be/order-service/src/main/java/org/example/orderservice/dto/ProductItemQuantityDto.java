package org.example.orderservice.dto;

import lombok.Data;

@Data
public class ProductItemQuantityDto {
    private String sku;
    private Integer quantity;
}
