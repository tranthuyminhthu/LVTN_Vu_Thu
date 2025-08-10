package org.example.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.client.ProductsClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductsClientService {

    private final ProductsClient productsClient;

    public List<String> getUsersByProductIds(List<String> productIds) {
        try {
            log.info("Calling products-service to get users by product IDs: {}", productIds);
            List<String> users = productsClient.getUsersByProductIds(productIds);
            log.info("Successfully got {} users from products-service", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to get users by product IDs from products-service: {}", e.getMessage(), e);
            return List.of(); // Return empty list on error
        }
    }

    public List<String> getUsersByVendorId(String vendorId) {
        try {
            log.info("Calling products-service to get users by vendor ID: {}", vendorId);
            List<String> users = productsClient.getUsersByVendorId(vendorId);
            log.info("Successfully got {} users from products-service for vendor: {}", users.size(), vendorId);
            return users;
        } catch (Exception e) {
            log.error("Failed to get users by vendor ID from products-service: {}", e.getMessage(), e);
            return List.of(); // Return empty list on error
        }
    }
} 