package org.example.productservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "product_variants")
public class ProductVariantEntity {
    @Id
    private String sku;
    private String productId;
    private String size;
    private String colorName;
    private String colorHex;
    private Double price;
    private Integer stockQuantity;

}
