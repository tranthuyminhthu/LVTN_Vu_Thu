package org.example.productservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productservice.dao.ProductVariantDao;
import org.example.productservice.entity.ProductVariantEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductVariantService {
    private final ProductVariantDao productVariantDao;

    public List<ProductVariantEntity> insertProductVariant(List<ProductVariantEntity> productVariants) {
        if (productVariants == null || productVariants.isEmpty()) {
            throw new IllegalArgumentException("Product variants cannot be null or empty");
        }
        List<ProductVariantEntity> newList = productVariants.stream()
                .peek(productVariant -> {
                    String sku = productVariant.getProductId() + "-" + productVariant.getColorHex() + "-" + productVariant.getSize();
                    productVariant.setSku(sku);
                })
                .toList();
        return productVariantDao.saveAll(newList);
    }
}
