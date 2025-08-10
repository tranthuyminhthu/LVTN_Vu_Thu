package org.example.productsservice.dao;

import org.example.productsservice.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDao extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCreatedBy(String createdBy);
    Page<ProductEntity> findByStatus(String status, Pageable pageable);
    Page<ProductEntity> findByStatusNot(String status, Pageable pageable);
    Page<ProductEntity> findByCreatedByAndStatus(String createdBy, String status, Pageable pageable);
    Page<ProductEntity> findByCreatedBy(String createdBy, Pageable pageable);
    Page<ProductEntity> findByCreatedByAndStatusNot(String createdBy, String status, Pageable pageable);
    
    // Filter by gender
    Page<ProductEntity> findByGender(String gender, Pageable pageable);
    Page<ProductEntity> findByStatusAndGender(String status, String gender, Pageable pageable);
    Page<ProductEntity> findByStatusNotAndGender(String status, String gender, Pageable pageable);
    Page<ProductEntity> findByCreatedByAndGender(String createdBy, String gender, Pageable pageable);
    Page<ProductEntity> findByCreatedByAndStatusAndGender(String createdBy, String status, String gender, Pageable pageable);
    Page<ProductEntity> findByCreatedByAndStatusNotAndGender(String createdBy, String status, String gender, Pageable pageable);
    
    // Lấy sản phẩm mới nhất (sắp xếp theo createdAt giảm dần)
    List<ProductEntity> findTop4ByStatusOrderByCreatedAtDesc(String status);
    
    // Lấy sản phẩm có lượt xem cao nhất
    List<ProductEntity> findTop4ByStatusOrderByViewCountDesc(String status);
}
