package org.example.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notification_service.entity.NotificationEntity;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponseDto {
    private List<NotificationEntity> notifications;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
} 