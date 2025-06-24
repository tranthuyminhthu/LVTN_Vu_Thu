package org.example.productsservice.dao;

import org.example.productsservice.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends MongoRepository<ProductEntity, String> {
}
