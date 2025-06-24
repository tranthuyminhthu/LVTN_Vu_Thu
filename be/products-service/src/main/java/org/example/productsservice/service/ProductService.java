package org.example.productsservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.constant.Status;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dao.ProductVariantDao;
import org.example.productsservice.dto.ProductDto;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.entity.ProductVariantEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;
    private final ProductVariantDao productVariantDao;

    public List<ProductEntity> getAllProducts() {
        return productDao.findAll();
    }

    public ProductEntity insertProduct(ProductEntity product) {
        product.setStatus(Status.IN_PROGRESS);
        return productDao.save(product);
    }

    public Boolean acceptProduct(String id) {
        ProductEntity product = productDao.findById(id).orElseThrow(() -> new EmptyResultDataAccessException("Product not found", 1));
        if (product != null) {
            product.setStatus(Status.ACCEPTED);
            productDao.save(product);
            return true;
        }
        return false;
    }

    public ProductDto getProductById(String id) {
        ProductEntity productEntity =  productDao.findById(id).orElseThrow(() -> new EmptyResultDataAccessException("Product not found", 1));
        ProductDto productDto = new ProductDto();
        BeanUtils.copyProperties(productEntity, productDto);
        List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(id);
        if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
            productDto.setVariants(productVariantEntities);
        }
        return productDto;
    }
}
