package org.example.productsservice.dao;

import org.example.productsservice.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDao extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByProductId(Long productId);
}
