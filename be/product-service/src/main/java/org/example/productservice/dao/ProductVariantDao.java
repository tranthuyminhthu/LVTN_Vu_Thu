package org.example.productservice.dao;

import org.example.productservice.entity.ProductVariantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantDao  extends MongoRepository<ProductVariantEntity, String> {
    default List<ProductVariantEntity> findByProductId(String productId) {
        return findAll().stream()
                .filter(variant -> variant.getProductId().equals(productId))
                .toList();
    }
}
