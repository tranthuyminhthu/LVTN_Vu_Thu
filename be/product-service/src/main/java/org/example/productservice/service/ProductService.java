package org.example.productservice.service;

import lombok.RequiredArgsConstructor;
import org.example.productservice.constant.Status;
import org.example.productservice.dao.ProductDao;
import org.example.productservice.entity.ProductEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;

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
}
