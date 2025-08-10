package org.example.notification_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.entity.NotificationEntity;
import org.example.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(
        topics = "${notification.kafka.topics.email}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenEmailNotifications(
            @Payload NotificationEntity notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received email notification from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Processing email notification: id={}, recipient={}", notification.getId(), notification.getRecipient());
        
        try {
            notificationService.processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process email notification: id={}, error={}", notification.getId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
        topics = "${notification.kafka.topics.sms}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenSmsNotifications(
            @Payload NotificationEntity notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received SMS notification from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Processing SMS notification: id={}, recipient={}", notification.getId(), notification.getRecipient());
        
        try {
            notificationService.processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process SMS notification: id={}, error={}", notification.getId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
        topics = "${notification.kafka.topics.push}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenPushNotifications(
            @Payload NotificationEntity notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received push notification from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Processing push notification: id={}, recipient={}", notification.getId(), notification.getRecipient());
        
        try {
            notificationService.processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process push notification: id={}, error={}", notification.getId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
        topics = "${notification.kafka.topics.inapp}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenInAppNotifications(
            @Payload NotificationEntity notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received in-app notification from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Processing in-app notification: id={}, recipient={}", notification.getId(), notification.getRecipient());
        
        try {
            notificationService.processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process in-app notification: id={}, error={}", notification.getId(), e.getMessage(), e);
        }
    }

    @KafkaListener(
        topics = "${notification.kafka.topics.retry}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenRetryNotifications(
            @Payload NotificationEntity notification,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received retry notification from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        log.info("Processing retry notification: id={}, retryCount={}", notification.getId(), notification.getRetryCount());
        
        try {
            // Add delay before retry
            Thread.sleep(5000); // 5 seconds delay
            notificationService.processNotification(notification);
        } catch (Exception e) {
            log.error("Failed to process retry notification: id={}, error={}", notification.getId(), e.getMessage(), e);
        }
    }
} 