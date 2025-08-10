package org.example.cartservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.service.CartService;
import org.example.cartservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/carts")
@RestController("api-cart-controller")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCurrentUserCart() {
        // Log current user roles
        UserContext.logCurrentUserRoles();
        
        var cart = cartService.getCart();
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<?> createCurrentUserCart() {
        // Log current user roles
        UserContext.logCurrentUserRoles();
        
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID not found in request");
        }
        
        var cart = cartService.createCart(userId);
        if (cart == null) {
            return ResponseEntity.badRequest().body("Cart already exists for this user");
        }
        return ResponseEntity.ok(cart);
    }
}
