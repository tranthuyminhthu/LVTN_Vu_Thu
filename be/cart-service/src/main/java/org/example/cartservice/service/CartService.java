package org.example.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.dao.CartDao;
import org.example.cartservice.dao.CartItemDao;
import org.example.cartservice.dao.ProductClient;
import org.example.cartservice.dto.CartResponseDto;
import org.example.cartservice.dto.ProductDto;
import org.example.cartservice.entity.CartEntity;
import org.example.cartservice.entity.CartItemEntity;
import org.example.cartservice.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartDao cartDao;
    private final CartItemDao cartItemDao;
    private final ProductClient productClient;

    public CartEntity createCart(String userId) {
        if (cartDao.existsById(userId)) {
            return null;
        }
        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserId(userId);
        cartEntity.setTotalPrice(0.0);

        return cartDao.save(cartEntity);
    }

    public CartResponseDto getCart() {
        String currentId = UserContext.getCurrentUserId();
        if (currentId == null) {
            throw new IllegalArgumentException("User ID not found in request");
        }
        CartEntity cartEntity = cartDao.findById(currentId).orElse(null);
        CartResponseDto cartResponseDto = new CartResponseDto();
        List<CartItemEntity> items = cartItemDao.findAllByCartId(currentId);
        double totalPrice = 0.0;
        for (CartItemEntity item : items) {
            totalPrice += item.getPrice() * item.getQuantity();
            try {
                int productIdLength = item.getSku().indexOf('-') > 0 ? item.getSku().indexOf('-') : item.getSku().length();
                ProductDto productDto = productClient.getProductById(Long.valueOf(item.getSku().substring(0, productIdLength))).getBody();
                item.setProductDetails(productDto);
            } catch (Exception e) {
                item.setProductDetails(new ProductDto()); // Set empty product details if not found
            }

        }
        if (cartEntity != null) {
            cartResponseDto.setUserId(cartEntity.getUserId());
            cartResponseDto.setTotalPrice(totalPrice);
            cartResponseDto.setItems(items);
            return cartResponseDto;
        }
        return cartResponseDto;
    }

    public boolean deleteCart(String userId) {
        // Delete all cart items first
        List<CartItemEntity> cartItems = cartItemDao.findAllByCartId(userId);
        if (!cartItems.isEmpty()) {
            cartItemDao.deleteAll(cartItems);
        }
        
        // Delete cart entity
        if (cartDao.existsById(userId)) {
            cartDao.deleteById(userId);
            return true;
        }
        return false;
    }
}
