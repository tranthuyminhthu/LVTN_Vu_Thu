package org.example.productsservice.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.example.productsservice.annotation.RequireRole;
import org.example.productsservice.dto.ProductDto;
import org.example.productsservice.dto.ProductListResponseDto;
import org.example.productsservice.dto.ProductVariantRequestDto;
import org.example.productsservice.dto.ProductWithVariantsDto;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.entity.ProductVariantEntity;
import org.example.productsservice.service.ProductService;
import org.example.productsservice.service.S3Service;
import org.example.productsservice.util.UserContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.io.IOException;
import java.util.Map;

@RestController("apiProductController")
@RequestMapping()
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final S3Service s3Service;

//    @PostMapping
//    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
//    public ResponseEntity<?> createProduct(@RequestBody ProductEntity product) {
//        ProductEntity createdProduct = productService.insertProduct(product);
//        URI location = URI.create("/products/" + createdProduct.getId());
//        return ResponseEntity.created(location).body("");
//    }

    @PostMapping
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> insertProductWithVariants(@RequestPart("product") String productWithVariantsDtoJson
    , @RequestPart("files") List<MultipartFile> files){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductWithVariantsDto productWithVariantsDto = objectMapper.readValue(productWithVariantsDtoJson, ProductWithVariantsDto.class);
            productService.insertProductWithVariants(productWithVariantsDto, files);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, String> productDto) {
        try {
            String name = productDto.get("name");
            String description = productDto.get("description");
            productService.updateProduct(id, name, description);
            return ResponseEntity.ok(
                    Map.of("message", "Product updated successfully", "productId", id));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while updating the product: " + e.getMessage()));
        }
    }

    @GetMapping
    @RequireRole({"USER"})
    public ResponseEntity<ProductListResponseDto> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender) {
        try {
            ProductListResponseDto response = productService.getProducts(page, size, status, gender);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/me")
    @RequireRole({"VENDOR"})
    public ResponseEntity<?> getProductBy(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gender) {
        try {
            ProductListResponseDto response = productService.getProductByCreatedBy(page, size, status, gender);
            return ResponseEntity.ok(response);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



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

    @PostMapping("/images")
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> urls = s3Service.uploadFiles(files);
            return ResponseEntity.ok(urls);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/accepted")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> acceptProduct(@RequestBody List<String> productIds) {
        try {
             productService.acceptProduct(productIds);
            return ResponseEntity.ok(Map.of("message", "Products accepted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", "One or more products not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while accepting the product: " + e.getMessage());
        }
    }

    @PostMapping("/rejected")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> rejectProduct(@RequestBody List<String> productIds) {
        try {
            productService.rejectProduct(productIds);
            return ResponseEntity.ok(Map.of("message", "Products accepted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "message", "One or more products not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while accepting the product: " + e.getMessage());
        }
    }


    @GetMapping("/search")
    @RequireRole({"USER"})
    public ResponseEntity<?> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ProductListResponseDto response = productService.searchProducts(query, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search/category")
    @RequireRole({"USER"})
    public ResponseEntity<?> searchByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ProductListResponseDto response = productService.searchByCategory(category, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search/price-range")
    @RequireRole({"USER"})
    public ResponseEntity<?> searchByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        try {
            ProductListResponseDto response = productService.searchByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/latest")
    @RequireRole({"USER"})
    public ResponseEntity<List<ProductDto>> getLatestProducts() {
        try {
            List<ProductDto> latestProducts = productService.getLatestProducts();
            return ResponseEntity.ok(latestProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/most-viewed")
    @RequireRole({"USER"})
    public ResponseEntity<List<ProductDto>> getMostViewedProducts() {
        try {
            List<ProductDto> mostViewedProducts = productService.getMostViewedProducts();
            return ResponseEntity.ok(mostViewedProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{productId}/variants")
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> updateProductVariants(
            @PathVariable Long productId,
            @RequestBody List<ProductVariantRequestDto> variants) {
        try {
            List<ProductVariantEntity> updatedVariants = productService.updateProductVariants(productId, variants);
            return ResponseEntity.ok(updatedVariants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> softDeleteProduct(@PathVariable Long productId) {
        try {
            productService.softDeleteProduct(productId);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Product not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while deleting the product: " + e.getMessage()));
        }
    }

    @DeleteMapping("/batch")
    @RequireRole({"SELLER", "ADMIN", "MANAGER"})
    public ResponseEntity<?> softDeleteProducts(@RequestBody List<Long> productIds) {
        try {
            productService.softDeleteProducts(productIds);
            return ResponseEntity.ok(Map.of("message", "Products deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "One or more products not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while deleting the products: " + e.getMessage()));
        }
    }

    @PostMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestPart("image") MultipartFile image) {
        try {
            List<ProductDto> recommendations = productService.getRecommendProduct(image);
            return ResponseEntity.ok(recommendations);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
