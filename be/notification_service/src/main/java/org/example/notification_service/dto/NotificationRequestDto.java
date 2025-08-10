package org.example.notification_service.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.notification_service.entity.NotificationEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Notification type is required")
    private NotificationEntity.NotificationType type; // EMAIL, SMS, PUSH, IN_APP
    
    @NotNull(message = "Category is required")
    private NotificationEntity.NotificationCategory category; // ORDER, PROMOTION, SYSTEM, etc.
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String content;
    
    private String templateId;
    
    @NotBlank(message = "Recipient is required")
    private String recipient; // email, phone, device token
    
    private Map<String, Object> data; // template variables
    
    private LocalDateTime scheduledAt;
    
    private String priority = "NORMAL"; // HIGH, NORMAL, LOW
} 