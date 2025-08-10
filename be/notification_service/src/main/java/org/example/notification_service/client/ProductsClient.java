package org.example.notification_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "products-service", 
    url = "${service.product}",
    configuration = org.example.notification_service.config.FeignConfig.class
)
public interface ProductsClient {

    @GetMapping("/favorites/users-by-products")
    List<String> getUsersByProductIds(@RequestParam("productIds") List<String> productIds);

    @GetMapping("/favorites/users-by-vendor")
    List<String> getUsersByVendorId(@RequestParam("vendorId") String vendorId);
} 