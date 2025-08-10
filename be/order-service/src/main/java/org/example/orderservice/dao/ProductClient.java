package org.example.orderservice.dao;

import org.example.orderservice.dto.ProductItemQuantityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-client", url = "${service.product-service}")
public interface ProductClient {
    @PutMapping("/internal/product-variants")
    ResponseEntity<?> updateProductVariant(@RequestBody List<ProductItemQuantityDto> productItemQuantityDto);

}
