package org.example.productsservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.dto.ProductItemQuantityDto;
import org.example.productsservice.dto.ProductVariantRequestDto;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.service.ProductVariantService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/{productId}")
    public ResponseEntity<List<ProductVariantEntity>> insertProductVariant(
            @PathVariable Long productId,
            @RequestBody List<ProductVariantRequestDto> productVariantDtos) {
        List<ProductVariantEntity> savedVariant = productVariantService.insertProductVariant(productVariantDtos, productId);
        return new ResponseEntity<>(savedVariant, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<List<ProductVariantEntity>> updateProductVariant(@RequestBody List<ProductItemQuantityDto> productVariantDtos) {
        try {
            productVariantService.updateProductQuantity(productVariantDtos);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
