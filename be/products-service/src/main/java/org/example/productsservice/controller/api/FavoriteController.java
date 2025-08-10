package org.example.productsservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.annotation.RequireRole;
import org.example.productsservice.dto.ProductListResponseDto;
import org.example.productsservice.service.FavoriteService;
import org.example.productsservice.util.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("apiFavoriteController")
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final UserContext userContext;

    @PostMapping()
    @RequireRole({"USER", "SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> addFavorite(@RequestBody Map<String, String> body) {
        try {
            String productId = body.get("productId");
            String userId = userContext.getUserId();
            favoriteService.addFavorite(userId, productId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add favorite: " + e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    @RequireRole({"USER", "SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> removeFavorite(@PathVariable String productId) {
        try {
            String userId = userContext.getUserId();
            favoriteService.removeFavorite(userId, productId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to remove favorite: " + e.getMessage());
        }
    }

    @GetMapping
    @RequireRole({"USER", "SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<ProductListResponseDto> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String userId = userContext.getUserId();
            ProductListResponseDto response = favoriteService.getFavorites(userId, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check/{productId}")
    @RequireRole({"USER", "SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<Boolean> isFavorite(@PathVariable String productId) {
        try {
            String userId = userContext.getUserId();
            boolean isFavorite = favoriteService.isFavorite(userId, productId);
            return ResponseEntity.ok(isFavorite);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 
