package org.example.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.dto.NotificationRequestDto;
import org.example.notification_service.entity.NotificationEntity;
import org.example.notification_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;
    private final KafkaTemplate<String, NotificationEntity> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${notification.kafka.topics.email}")
    private String emailTopic;
    
    @Value("${notification.kafka.topics.sms}")
    private String smsTopic;
    
    @Value("${notification.kafka.topics.push}")
    private String pushTopic;
    
    @Value("${notification.kafka.topics.inapp}")
    private String inappTopic;
    
    @Value("${notification.kafka.topics.retry}")
    private String retryTopic;
    
    public NotificationEntity sendNotification(NotificationRequestDto request) {
        log.info("Sending notification: type={}, recipient={}", request.getType(), request.getRecipient());
        
        try {
            // Create notification entity
            NotificationEntity notification = createNotificationEntity(request);
            notification = notificationRepository.save(notification);
            
            // Send to Kafka topic for async processing
            sendToKafkaTopic(notification);
            
            log.info("Notification sent to Kafka topic successfully: id={}", notification.getId());
            return notification;
            
        } catch (Exception e) {
            log.error("Failed to send notification: error={}", e.getMessage(), e);
            throw new RuntimeException("Failed to send notification", e);
        }
    }
    
    public NotificationEntity scheduleNotification(NotificationRequestDto request) {
        log.info("Scheduling notification: type={}, scheduledAt={}", request.getType(), request.getScheduledAt());
        
        try {
            NotificationEntity notification = createNotificationEntity(request);
            notification.setScheduledAt(request.getScheduledAt());
            notification.setStatus(NotificationEntity.NotificationStatus.SCHEDULED);
            
            notification = notificationRepository.save(notification);
            
            // Schedule for later processing
            scheduleForLater(notification);
            
            log.info("Notification scheduled successfully: id={}", notification.getId());
            return notification;
            
        } catch (Exception e) {
            log.error("Failed to schedule notification: error={}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule notification", e);
        }
    }
    
    public List<NotificationEntity> getUserNotifications(String userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, 
                org.springframework.data.domain.PageRequest.of(page, size));
    }
    
    public List<NotificationEntity> getRecentUserNotifications(String userId, LocalDateTime since) {
        return notificationRepository.findRecentNotificationsByUserId(userId, since);
    }
    
    public Optional<NotificationEntity> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    public NotificationEntity markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setStatus(NotificationEntity.NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("Notification deleted: id={}", notificationId);
    }
    
    public void processNotification(NotificationEntity notification) {
        log.info("Processing notification: id={}, type={}", notification.getId(), notification.getType());
        
        try {
            switch (notification.getType()) {
                case EMAIL:
                    emailService.sendEmail(notification);
                    break;
                case SMS:
                    smsService.sendSms(notification);
                    break;
                case PUSH:
                    pushNotificationService.sendPushNotification(notification);
                    break;
                case IN_APP:
                    // Handle in-app notification
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported notification type: " + notification.getType());
            }
            
            // Update status
            notification.setStatus(NotificationEntity.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            log.info("Notification processed successfully: id={}", notification.getId());
            
        } catch (Exception e) {
            log.error("Failed to process notification: id={}, error={}", notification.getId(), e.getMessage(), e);
            
            // Update status and retry count
            notification.setStatus(NotificationEntity.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            notificationRepository.save(notification);
            
            // Retry logic
            if (notification.getRetryCount() < 3) {
                retryNotification(notification);
            }
        }
    }
    
    public List<Object[]> getNotificationStats(LocalDateTime since) {
        return notificationRepository.getNotificationStats(since);
    }
    
    public List<Object[]> getDailyNotificationStats(LocalDateTime since) {
        return notificationRepository.getDailyNotificationStats(since);
    }
    
    public List<NotificationEntity> getNotificationsForRetry(Integer maxRetries, LocalDateTime since) {
        return notificationRepository.findNotificationsForRetry(maxRetries, since);
    }
    
    public int cleanupOldNotifications(LocalDateTime cutoffDate) {
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }
    
    private NotificationEntity createNotificationEntity(NotificationRequestDto request) {
        NotificationEntity notification = new NotificationEntity();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setCategory(request.getCategory());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setTemplateId(request.getTemplateId());
        notification.setRecipient(request.getRecipient());
        notification.setStatus(NotificationEntity.NotificationStatus.PENDING);
        
        // Convert data map to JSON string
        if (request.getData() != null) {
            try {
                notification.setData(objectMapper.writeValueAsString(request.getData()));
            } catch (Exception e) {
                log.error("Failed to serialize notification data", e);
            }
        }
        
        return notification;
    }
    
    private void sendToKafkaTopic(NotificationEntity notification) {
        String topic = getTopicForNotificationType(notification.getType());
        String key = notification.getId().toString();
        
        kafkaTemplate.send(topic, key, notification)
                .addCallback(
                    result -> log.info("Notification sent to Kafka topic {} successfully: id={}", topic, notification.getId()),
                    ex -> log.error("Failed to send notification to Kafka topic {}: id={}, error={}", topic, notification.getId(), ex.getMessage())
                );
    }
    
    private String getTopicForNotificationType(NotificationEntity.NotificationType type) {
        return switch (type) {
            case EMAIL -> emailTopic;
            case SMS -> smsTopic;
            case PUSH -> pushTopic;
            case IN_APP -> inappTopic;
        };
    }
    
    private void scheduleForLater(NotificationEntity notification) {
        // Implementation for scheduling notifications
        // This could use a scheduler like Quartz or a delayed queue
        log.info("Notification scheduled for later processing: id={}, scheduledAt={}", 
                notification.getId(), notification.getScheduledAt());
    }
    
    private void retryNotification(NotificationEntity notification) {
        // Implementation for retry logic
        log.info("Retrying notification: id={}, retryCount={}", 
                notification.getId(), notification.getRetryCount());
        
        // Send to retry topic with delay
        kafkaTemplate.send(retryTopic, notification.getId().toString(), notification)
                .addCallback(
                    result -> log.info("Retry notification sent to Kafka topic successfully: id={}", notification.getId()),
                    ex -> log.error("Failed to send retry notification to Kafka topic: id={}, error={}", notification.getId(), ex.getMessage())
                );
    }
} 