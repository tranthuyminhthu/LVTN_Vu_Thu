package org.example.orderservice.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "cart-client", url = "${service.cart-service}")
public interface CartClient {

    @DeleteMapping(value = "/carts/items", consumes = "application/json")
    ResponseEntity<?> deleteCart(@RequestBody List<String> productIds);
}
