package org.example.notification_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.entity.NotificationEntity;
import org.example.notification_service.service.SmsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl {
    
    public void sendSms(NotificationEntity notification) {
        try {
            // TODO: Implement actual SMS sending logic
            // This could integrate with Twilio, AWS SNS, or other SMS providers
            
            log.info("SMS sent successfully to: {}", notification.getRecipient());
            
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", notification.getRecipient(), e.getMessage(), e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
} 