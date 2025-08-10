package org.example.cartservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemBodyDto {
    private String sku;
    private Integer quantity;
    private Double price;
}
