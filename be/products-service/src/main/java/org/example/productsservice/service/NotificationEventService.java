package org.example.productsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productsservice.dao.FavoriteDao;
import org.example.productsservice.dao.ProductDao;
import org.example.productsservice.dto.ProductApprovalEventDto;
import org.example.productsservice.entity.FavoriteEntity;
import org.example.productsservice.entity.ProductEntity;
import org.example.productsservice.util.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {

    private final KafkaTemplate<String, ProductApprovalEventDto> kafkaTemplate;
    private final FavoriteDao favoriteDao;
    private final ProductDao productDao;
    private final UserContext userContext;

    @Value("${kafka.topics.product-approval:product.approval}")
    private String productApprovalTopic;

    public void sendProductApprovalNotification(List<String> productIds) {
        try {
            String approvedBy = userContext.getUserId();
            
            // Lấy thông tin vendor từ sản phẩm đầu tiên (giả sử tất cả sản phẩm cùng vendor)
            String vendorId = getVendorIdFromProducts(productIds);
            String vendorName = getVendorName(vendorId);
            
            ProductApprovalEventDto event = new ProductApprovalEventDto(
                "PRODUCT_APPROVED",
                productIds,
                approvedBy,
                LocalDateTime.now(),
                "Sản phẩm đã được chấp nhận bởi admin",
                vendorId,
                vendorName
            );

            // Gửi event qua Kafka
            kafkaTemplate.send(productApprovalTopic, "product-approval", event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Product approval event sent successfully for products: {} from vendor: {}", productIds, vendorId);
                    } else {
                        log.error("Failed to send product approval event: {}", ex.getMessage(), ex);
                    }
                });

        } catch (Exception e) {
            log.error("Error sending product approval notification: {}", e.getMessage(), e);
        }
    }

    private String getVendorIdFromProducts(List<String> productIds) {
        if (productIds.isEmpty()) {
            return null;
        }
        
        try {
            Long productId = Long.valueOf(productIds.get(0));
            ProductEntity product = productDao.findById(productId).orElse(null);
            return product != null ? product.getCreatedBy() : null;
        } catch (Exception e) {
            log.error("Error getting vendor ID from product: {}", e.getMessage(), e);
            return null;
        }
    }

    private String getVendorName(String vendorId) {
        if (vendorId == null) {
            return "Unknown Vendor";
        }
        
        try {
            // Gọi profile service để lấy tên vendor
            // TODO: Implement call to profile service
            return "Vendor " + vendorId;
        } catch (Exception e) {
            log.error("Error getting vendor name: {}", e.getMessage(), e);
            return "Unknown Vendor";
        }
    }

    public List<String> getUsersWithFavorites(List<String> productIds) {
        // Lấy danh sách user có sản phẩm trong wishlist
        return favoriteDao.findByProductIdIn(productIds)
            .stream()
            .map(FavoriteEntity::getUserId)
            .distinct()
            .toList();
    }

    public List<String> getUsersWithVendorFavorites(String vendorId) {
        // Lấy danh sách user có sản phẩm của vendor trong wishlist
        return favoriteDao.findUserIdsByVendorId(vendorId);
    }
} 
