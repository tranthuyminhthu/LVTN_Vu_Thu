package org.example.cartservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cartservice.entity.CartItemEntity;

import java.util.List;

@Data
@NoArgsConstructor
public class CartResponseDto {
    private String userId;
    private Double totalPrice;
    private List<CartItemEntity> items;
}
