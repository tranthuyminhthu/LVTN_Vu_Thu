package org.example.cartservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.entity.CartEntity;
import org.example.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/internal/carts")
@RestController("internal-cart-controller")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody String userId) {
        CartEntity cartEntity = cartService.createCart(userId);
        if (cartEntity == null) {
            return ResponseEntity.badRequest().body("Cart already exists for user: " + userId);
        }
        return ResponseEntity.created(
            URI.create("/internal/carts/" + userId)
        ).body("");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteCart(@PathVariable String userId) {
        boolean success = cartService.deleteCart(userId);
        if (success) {
            return ResponseEntity.ok("Cart deleted successfully for user: " + userId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
