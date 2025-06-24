package org.example.productsservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.service.ProductVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping
    public ResponseEntity<List<ProductVariantEntity>> insertProductVariant(@RequestBody List<ProductVariantEntity> productVariants) {
        List<ProductVariantEntity> savedVariant = productVariantService.insertProductVariant(productVariants);
        return new ResponseEntity<>(savedVariant, HttpStatus.CREATED);
    }
}
