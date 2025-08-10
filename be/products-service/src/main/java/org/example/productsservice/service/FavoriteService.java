package org.example.productsservice.service;

import lombok.AllArgsConstructor;
import org.example.productsservice.dao.FavoriteDao;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dto.ProductDto;
import org.example.productsservice.dto.ProductListResponseDto;
import org.example.productsservice.entity.FavoriteEntity;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.entity.ImageEntity;
import org.example.productsservice.dao.ProductVariantDao;
import org.example.productsservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FavoriteService {
    private final FavoriteDao favoriteDao;
    private final ProductDao productDao;
    private final ProductVariantDao productVariantDao;
    private final UserContext userContext;

    public void addFavorite(String userId, String productId) {
        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteDao.save(favorite);
    }

    public void removeFavorite(String userId, String productId) {
        FavoriteEntity favorite = favoriteDao.findByUserIdAndProductId(userId, productId).orElseThrow(
            () -> new IllegalArgumentException("Favorite not found for userId: " + userId + " and productId: " + productId)
        );
        favoriteDao.delete(favorite);
    }

    public ProductListResponseDto getFavorites(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoriteEntity> favoritePage = favoriteDao.findByUserId(userId, pageable);
        
        ProductListResponseDto response = new ProductListResponseDto();
        response.setProducts(favoritePage.getContent().stream()
            .map(favorite -> {
                try {
                    Long productId = Long.valueOf(favorite.getProductId());
                    ProductEntity productEntity = productDao.findById(productId).orElse(null);
                    if (productEntity == null) {
                        return null;
                    }
                    
                    ProductDto productDto = new ProductDto();
                    BeanUtils.copyProperties(productEntity, productDto);
                    
                    // Map variants
                    List<ProductVariantEntity> productVariantEntities = productVariantDao.findByProductId(productEntity.getId());
                    if (Objects.nonNull(productVariantEntities) && !productVariantEntities.isEmpty()) {
                        productDto.setVariants(productVariantEntities);
                    }
                    
                    // Map images
                    if (productEntity.getImages() != null) {
                        List<String> imageDtos = productEntity.getImages().stream().map(ImageEntity::getUrl).toList();
                        productDto.setImages(imageDtos);
                    }
                    
                    return productDto;
                } catch (NumberFormatException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList());
        
        response.setPage(favoritePage.getNumber());
        response.setSize(favoritePage.getSize());
        response.setTotalElements(favoritePage.getTotalElements());
        response.setTotalPages(favoritePage.getTotalPages());
        
        return response;
    }

    public boolean isFavorite(String userId, String productId) {
        return favoriteDao.findByUserIdAndProductId(userId, productId).isPresent();
    }

    public List<String> getUsersByProductIds(List<String> productIds) {
        return favoriteDao.findByProductIdIn(productIds)
            .stream()
            .map(FavoriteEntity::getUserId)
            .distinct()
            .toList();
    }

    public List<String> getUsersByVendorId(String vendorId) {
        return favoriteDao.findUserIdsByVendorId(vendorId);
    }
}
