package org.example.authservice.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cart-client", url = "${service.carts}")
public interface CartClient {
    @PostMapping(value = "/internal/carts", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createCart(@RequestBody String userId);

    @DeleteMapping(value = "/internal/carts/{userId}")
    ResponseEntity<?> deleteCart(@PathVariable String userId);
}
