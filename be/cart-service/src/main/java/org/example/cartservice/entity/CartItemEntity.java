package org.example.cartservice.entity;

import lombok.Data;
import org.example.cartservice.dto.ProductDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cart_items")
public class CartItemEntity {
    @Id
    private String id;
    private String cartId;
    private String sku;
    private Integer quantity;
    private Double price;
    @Transient
    private ProductDto productDetails;
}
