package org.example.productsservice.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.productsservice.dto.ProductApprovalEventDto;
import org.example.productsservice.service.NotificationEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka-test")
@RequiredArgsConstructor
@Slf4j
public class KafkaTestController {

    private final KafkaTemplate<String, ProductApprovalEventDto> kafkaTemplate;
    private final NotificationEventService notificationEventService;

    @PostMapping("/send-product-approval")
    public ResponseEntity<Map<String, String>> sendProductApprovalEvent(@RequestBody List<String> productIds) {
        try {
            log.info("Testing product approval notification for products: {}", productIds);
            
            // Gọi service để gửi notification
            notificationEventService.sendProductApprovalNotification(productIds);
            
            return ResponseEntity.ok(Map.of(
                "message", "Product approval event sent successfully",
                "productIds", productIds.toString(),
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to send product approval event: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send product approval event: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/send-direct-kafka")
    public ResponseEntity<Map<String, String>> sendDirectKafkaMessage(@RequestBody Map<String, Object> request) {
        try {
            String topic = (String) request.getOrDefault("topic", "product.approval");
            String key = (String) request.getOrDefault("key", "test-key");
            
            // Tạo test event
            ProductApprovalEventDto event = new ProductApprovalEventDto(
                "TEST_PRODUCT_APPROVED",
                List.of("1", "2", "3"),
                "test-admin",
                LocalDateTime.now(),
                "Test product approval message",
                "test-vendor-id",
                "Test Vendor"
            );

            log.info("Sending direct Kafka message to topic: {}, key: {}", topic, key);
            
            // Gửi trực tiếp qua KafkaTemplate
            kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Direct Kafka message sent successfully to topic: {}, partition: {}, offset: {}", 
                                result.getRecordMetadata().topic(), 
                                result.getRecordMetadata().partition(), 
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send direct Kafka message: {}", ex.getMessage(), ex);
                    }
                });

            return ResponseEntity.ok(Map.of(
                "message", "Direct Kafka message sent successfully",
                "topic", topic,
                "key", key,
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Failed to send direct Kafka message: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to send direct Kafka message: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "products-service",
            "kafka", "configured",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
} 