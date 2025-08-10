package org.example.cartservice.dao;

import org.example.cartservice.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDao extends MongoRepository<CartEntity, String> {
}
