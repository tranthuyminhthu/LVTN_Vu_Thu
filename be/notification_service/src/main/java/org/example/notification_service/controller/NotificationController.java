package org.example.notification_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.dto.NotificationRequestDto;
import org.example.notification_service.dto.NotificationListResponseDto;
import org.example.notification_service.entity.NotificationEntity;
import org.example.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<NotificationEntity> sendNotification(@Valid @RequestBody NotificationRequestDto request) {
        log.info("Received notification request: type={}, recipient={}", request.getType(), request.getRecipient());
        
        try {
            NotificationEntity notification = notificationService.sendNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/schedule")
    public ResponseEntity<NotificationEntity> scheduleNotification(@Valid @RequestBody NotificationRequestDto request) {
        log.info("Received schedule notification request: type={}, scheduledAt={}", request.getType(), request.getScheduledAt());
        
        try {
            NotificationEntity notification = notificationService.scheduleNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (Exception e) {
            log.error("Failed to schedule notification: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<NotificationEntity>> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            // TODO: Get userId from authentication context
            String userId = "user123"; // Placeholder
            List<NotificationEntity> notifications = notificationService.getUserNotifications(userId, page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Failed to get user notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationEntity>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        
        try {
            log.info("Getting all notifications with filters: page={}, size={}, type={}, status={}, category={}", 
                    page, size, type, status, category);
            
            List<NotificationEntity> notifications = notificationService.getAllNotifications(page, size, type, status, category);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Failed to get all notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<NotificationEntity>> getAllNotificationsSimple(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        try {
            log.info("Getting all notifications: page={}, size={}", page, size);
            
            List<NotificationEntity> notifications = notificationService.getAllNotificationsSimple(page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Failed to get all notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list-paginated")
    public ResponseEntity<NotificationListResponseDto> getAllNotificationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            log.info("Getting all notifications with pagination: page={}, size={}", page, size);
            
            NotificationListResponseDto response = notificationService.getAllNotificationsWithPagination(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get all notifications with pagination: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationEntity>> getNotificationsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            log.info("Getting notifications for user: userId={}, page={}, size={}", userId, page, size);
            
            List<NotificationEntity> notifications = notificationService.getUserNotifications(userId, page, size);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Failed to get notifications for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationEntity> getNotificationById(@PathVariable Long id) {
        try {
            return notificationService.getNotificationById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get notification by id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationEntity> markAsRead(@PathVariable Long id) {
        try {
            NotificationEntity notification = notificationService.markAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            log.error("Failed to mark notification as read: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete notification: id={}, error={}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<List<Object[]>> getNotificationStats(
            @RequestParam(defaultValue = "7") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<Object[]> stats = notificationService.getNotificationStats(since);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get notification stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/stats/daily")
    public ResponseEntity<List<Object[]>> getDailyNotificationStats(
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            List<Object[]> stats = notificationService.getDailyNotificationStats(since);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get daily notification stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getNotificationCount() {
        try {
            long totalCount = notificationService.getTotalNotificationCount();
            long unreadCount = notificationService.getUnreadNotificationCount();
            long todayCount = notificationService.getTodayNotificationCount();
            
            Map<String, Object> countStats = Map.of(
                "totalNotifications", totalCount,
                "unreadNotifications", unreadCount,
                "todayNotifications", todayCount,
                "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(countStats);
        } catch (Exception e) {
            log.error("Failed to get notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "notification-service"
        );
        return ResponseEntity.ok(health);
    }
}
