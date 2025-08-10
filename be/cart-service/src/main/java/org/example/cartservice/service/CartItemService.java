package org.example.cartservice.service;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.dao.CartItemDao;
import org.example.cartservice.dto.CartItemBodyDto;
import org.example.cartservice.entity.CartItemEntity;
import org.example.cartservice.util.UserContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemDao cartItemDao;

    public CartItemEntity addItem(CartItemBodyDto cartItemBodyDto) {
        String cartId = UserContext.getCurrentUserId();
        CartItemEntity cartItemEntity = new CartItemEntity();
        cartItemEntity.setCartId(cartId);
        cartItemEntity.setSku(cartItemBodyDto.getSku());
        cartItemEntity.setQuantity(cartItemBodyDto.getQuantity());
        cartItemEntity.setPrice(cartItemBodyDto.getPrice());
        return cartItemDao.save(cartItemEntity);
    }

    public Boolean deleteCartItem(String id) {
        String currentId = UserContext.getCurrentUserId();
        CartItemEntity cartItemEntity = cartItemDao.findById(id).orElse(null);
        if (cartItemEntity != null && cartItemEntity.getCartId().equals(currentId)){
            cartItemDao.delete(cartItemEntity);
            return true;
        }
        return false;
    }

    public Boolean deleteListCartItems(List<String> cartIds) {
        List<CartItemEntity> cartItems = cartItemDao.findAllByIdIn(cartIds);
        if (!cartItems.isEmpty()) {
            cartItemDao.deleteAll(cartItems);
            return true;
        }
        return false;
    }

    public CartItemEntity updateCartItem(String id, CartItemBodyDto cartItemBodyDto) {
        CartItemEntity cartItemEntity = cartItemDao.findById(id).orElse(null);
        if (cartItemEntity != null) {
            cartItemEntity.setQuantity(cartItemBodyDto.getQuantity());
            cartItemEntity.setPrice(cartItemBodyDto.getPrice());
            return cartItemDao.save(cartItemEntity);
        }
        return null;
    }
}
