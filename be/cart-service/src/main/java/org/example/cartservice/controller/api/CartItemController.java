package org.example.cartservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.dto.CartItemBodyDto;
import org.example.cartservice.entity.CartItemEntity;
import org.example.cartservice.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/carts/items")
@RestController
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemBodyDto cartItemBodyDto) {
        CartItemEntity cartItemEntity = cartItemService.addItem(cartItemBodyDto);
        if (cartItemEntity != null) {
            return ResponseEntity.ok(cartItemEntity);
        } else {
            return ResponseEntity.badRequest().body("Failed to add item to cart.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartItem(@PathVariable String id) {
        boolean success = cartItemService.deleteCartItem(id);
        if (success) {
            return ResponseEntity.ok("Cart item deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete cart item.");
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteListCartItems(@RequestBody List<String> cartIds) {
        boolean success = cartItemService.deleteListCartItems(cartIds);
        if (success) {
            return ResponseEntity.ok(
                Map.of("message", "Cart items deleted successfully."));
        } else {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Failed to delete cart items. Please check the provided IDs."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItem(@PathVariable String id, @RequestBody CartItemBodyDto cartItemBodyDto) {
        CartItemEntity updatedCartItem = cartItemService.updateCartItem(id, cartItemBodyDto);
        if (updatedCartItem != null) {
            return ResponseEntity.ok(updatedCartItem);
        } else {
            return ResponseEntity.badRequest().body("Failed to update cart item.");
        }
    }
}
