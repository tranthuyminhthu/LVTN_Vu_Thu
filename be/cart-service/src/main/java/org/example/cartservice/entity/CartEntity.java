package org.example.cartservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "carts")
@Data
public class CartEntity {
    @Id
    private String userId;
    private Double totalPrice;

}
