package org.example.productsservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.productsservice.annotation.RequireRole;
import org.example.productsservice.dto.ProductDto;
import org.example.productsservice.dto.ProductWithVariantsDto;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.service.ProductService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("internalProductController")
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<ProductEntity> insertProduct(@RequestBody ProductEntity product) {
        return new ResponseEntity<>(productService.insertProduct(product), HttpStatus.CREATED);
    }

//    @PostMapping("/with-variants")
//    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
//    public ResponseEntity<ProductEntity> insertProductWithVariants(@RequestBody ProductWithVariantsDto productWithVariantsDto) {
//        return new ResponseEntity<>(productService.insertProductWithVariants(productWithVariantsDto), HttpStatus.CREATED);
//    }

    @GetMapping("/{id}")
    @RequireRole({"USER", "SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        try {
            ProductDto product = productService.getProductById(id);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
