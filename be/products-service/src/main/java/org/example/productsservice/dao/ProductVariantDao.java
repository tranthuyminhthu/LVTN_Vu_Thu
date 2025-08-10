package org.example.productsservice.dao;

import org.example.productsservice.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantDao extends JpaRepository<ProductVariantEntity, Long> {
    List<ProductVariantEntity> findByProductId(Long productId);
    Optional<ProductVariantEntity> findBySku(String sku);
}
