package org.example.cartservice.dao;

import org.example.cartservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-client", url = "${service.product}")
public interface ProductClient {
    @GetMapping(value = "/internal/products/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id);
}
