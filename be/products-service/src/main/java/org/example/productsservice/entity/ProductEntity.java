package org.example.productsservice.entity;

import lombok.Data;
import org.example.productsservice.constant.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class ProductEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private Status status;
}
