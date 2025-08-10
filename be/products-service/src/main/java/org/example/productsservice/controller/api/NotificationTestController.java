package org.example.productsservice.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/notification-test")
@RequiredArgsConstructor
@Slf4j
public class NotificationTestController {

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/send-email-notification")
    public ResponseEntity<Map<String, String>> sendEmailNotification(@RequestBody Map<String, Object> request) {
        try {
            String topic = "notification.email";
            String key = "email-notification";
            
            log.info("Sending email notification from products-service to topic: {}, key: {}", topic, key);
            log.info("Notification details: userId={}, recipient={}, title={}", 
                    request.get("userId"), request.get("recipient"), request.get("title"));
            
            // Gửi trực tiếp Map object qua Kafka nếu có
            if (kafkaTemplate != null) {
                kafkaTemplate.send(topic, key, request)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Email notification sent successfully from products-service to topic: {}, partition: {}, offset: {}", 
                                    result.getRecordMetadata().topic(), 
                                    result.getRecordMetadata().partition(), 
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send email notification from products-service: {}", ex.getMessage(), ex);
                        }
                    });
            } else {
                log.info("Kafka not available - Email notification would be sent to topic: {}, key: {}", topic, key);
            }

            return ResponseEntity.ok(Map.of(
                "message", "Email notification sent successfully from products-service",
                "topic", topic,
                "key", key,
                "userId", (String) request.get("userId"),
                "recipient", (String) request.get("recipient"),
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to send email notification from products-service: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send email notification from products-service: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/send-password-reset-email")
    public ResponseEntity<Map<String, String>> sendPasswordResetEmail(@RequestBody Map<String, Object> request) {
        try {
            String topic = "notification.email";
            String key = "password-reset";
            
            // Tạo notification data cho password reset
            Map<String, Object> notification = Map.of(
                "userId", request.get("userId"),
                "type", "EMAIL",
                "category", "SECURITY",
                "title", "Password Reset Request",
                "content", "You requested a password reset for your account. Click the link below to reset your password.",
                "recipient", request.get("recipient"),
                "data", request.get("data")
            );

            log.info("Sending password reset email from products-service to: {}", request.get("recipient"));
            
            // Gửi qua Kafka nếu có
            if (kafkaTemplate != null) {
                kafkaTemplate.send(topic, key, notification)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Password reset email sent successfully from products-service");
                        } else {
                            log.error("Failed to send password reset email from products-service: {}", ex.getMessage(), ex);
                        }
                    });
            } else {
                log.info("Kafka not available - Password reset email would be sent to: {}", request.get("recipient"));
            }

            return ResponseEntity.ok(Map.of(
                "message", "Password reset email sent successfully from products-service",
                "recipient", (String) request.get("recipient"),
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to send password reset email from products-service: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send password reset email from products-service: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/send-product-approval-email")
    public ResponseEntity<Map<String, String>> sendProductApprovalEmail(@RequestBody Map<String, Object> request) {
        try {
            String topic = "notification.email";
            String key = "product-approval";
            
            // Tạo notification data cho product approval
            Map<String, Object> notification = Map.of(
                "userId", request.get("userId"),
                "type", "EMAIL",
                "category", "VENDOR_PRODUCT_APPROVAL",
                "title", "Sản phẩm mới từ " + request.get("vendorName"),
                "content", "Vendor " + request.get("vendorName") + " vừa có " + request.get("productCount") + " sản phẩm mới được chấp nhận!",
                "recipient", request.get("recipient"),
                "data", Map.of(
                    "vendorId", request.get("vendorId"),
                    "vendorName", request.get("vendorName"),
                    "productIds", request.get("productIds"),
                    "productCount", request.get("productCount"),
                    "approvedBy", request.get("approvedBy"),
                    "approvedAt", LocalDateTime.now().toString()
                )
            );

            log.info("Sending product approval email from products-service to: {}", request.get("recipient"));
            
            // Gửi qua Kafka nếu có
            if (kafkaTemplate != null) {
                kafkaTemplate.send(topic, key, notification)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Product approval email sent successfully from products-service");
                        } else {
                            log.error("Failed to send product approval email from products-service: {}", ex.getMessage(), ex);
                        }
                    });
            } else {
                log.info("Kafka not available - Product approval email would be sent to: {}", request.get("recipient"));
            }

            return ResponseEntity.ok(Map.of(
                "message", "Product approval email sent successfully from products-service",
                "recipient", (String) request.get("recipient"),
                "vendorName", (String) request.get("vendorName"),
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to send product approval email from products-service: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send product approval email from products-service: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        String kafkaStatus = kafkaTemplate != null ? "configured" : "not-available";
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "products-service",
            "kafka", kafkaStatus,
            "timestamp", LocalDateTime.now().toString()
        ));
    }
} 