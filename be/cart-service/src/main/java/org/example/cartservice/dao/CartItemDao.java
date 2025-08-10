package org.example.cartservice.dao;

import org.example.cartservice.entity.CartEntity;
import org.example.cartservice.entity.CartItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemDao extends MongoRepository<CartItemEntity, String> {
    List<CartItemEntity> findAllByCartId(String cartId);

    CartItemEntity findByCartIdAndId(String cartId, String id);
    
    // Lấy danh sách CartItemEntity dựa vào list ID
    @Query("{'_id': {'$in': ?0}}")
    List<CartItemEntity> findAllByIdIn(List<String> ids);
}
