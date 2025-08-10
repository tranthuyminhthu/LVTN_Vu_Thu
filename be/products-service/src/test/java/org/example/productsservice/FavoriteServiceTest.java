package org.example.productsservice;

import org.example.productsservice.dao.FavoriteDao;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dao.ProductVariantDao;
import org.example.productsservice.dto.ProductListResponseDto;
import org.example.productsservice.entity.FavoriteEntity;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.service.FavoriteService;
import org.example.productsservice.util.UserContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteDao favoriteDao;

    @Mock
    private ProductDao productDao;

    @Mock
    private ProductVariantDao productVariantDao;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private FavoriteService favoriteService;

    private FavoriteEntity favoriteEntity;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        favoriteEntity = new FavoriteEntity();
        favoriteEntity.setId("favorite1");
        favoriteEntity.setUserId("user1");
        favoriteEntity.setProductId("123");

        productEntity = new ProductEntity();
        productEntity.setId(123L);
        productEntity.setName("Test Product");
        productEntity.setDescription("Test Description");
        productEntity.setPrice(100.0);
        productEntity.setStatus("ACTIVE");
    }

    @Test
    void testAddFavorite() {
        // Given
        String userId = "user1";
        String productId = "123";
        when(favoriteDao.save(any(FavoriteEntity.class))).thenReturn(favoriteEntity);

        // When
        favoriteService.addFavorite(userId, productId);

        // Then
        verify(favoriteDao, times(1)).save(any(FavoriteEntity.class));
    }

    @Test
    void testRemoveFavorite() {
        // Given
        String userId = "user1";
        String productId = "123";
        when(favoriteDao.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.of(favoriteEntity));

        // When
        favoriteService.removeFavorite(userId, productId);

        // Then
        verify(favoriteDao, times(1)).findByUserIdAndProductId(userId, productId);
        verify(favoriteDao, times(1)).delete(favoriteEntity);
    }

    @Test
    void testRemoveFavoriteNotFound() {
        // Given
        String userId = "user1";
        String productId = "123";
        when(favoriteDao.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            favoriteService.removeFavorite(userId, productId);
        });
    }

    @Test
    void testGetFavorites() {
        // Given
        String userId = "user1";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<FavoriteEntity> favorites = Arrays.asList(favoriteEntity);
        Page<FavoriteEntity> favoritePage = new PageImpl<>(favorites, pageable, 1);

        when(favoriteDao.findByUserId(userId, pageable)).thenReturn(favoritePage);
        when(productDao.findById(123L)).thenReturn(Optional.of(productEntity));
        when(productVariantDao.findByProductId(123L)).thenReturn(Arrays.asList());

        // When
        ProductListResponseDto result = favoriteService.getFavorites(userId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getProducts().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testIsFavorite() {
        // Given
        String userId = "user1";
        String productId = "123";
        when(favoriteDao.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.of(favoriteEntity));

        // When
        boolean result = favoriteService.isFavorite(userId, productId);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsNotFavorite() {
        // Given
        String userId = "user1";
        String productId = "123";
        when(favoriteDao.findByUserIdAndProductId(userId, productId))
                .thenReturn(Optional.empty());

        // When
        boolean result = favoriteService.isFavorite(userId, productId);

        // Then
        assertFalse(result);
    }
} 