package org.example.productsservice.dao;

import org.example.productsservice.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteDao extends JpaRepository<FavoriteEntity, String>{
    Optional<FavoriteEntity> findByUserIdAndProductId(String userId, String productId);
    Page<FavoriteEntity> findByUserId(String userId, Pageable pageable);
    List<FavoriteEntity> findByProductIdIn(List<String> productIds);
    
    // Tìm user có sản phẩm của vendor trong wishlist
    @Query("SELECT DISTINCT f.userId FROM FavoriteEntity f " +
           "JOIN ProductEntity p ON f.productId = CAST(p.id AS string) " +
           "WHERE p.createdBy = :vendorId")
    List<String> findUserIdsByVendorId(@Param("vendorId") String vendorId);
}
