package org.example.notification_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.entity.NotificationEntity;
import org.example.notification_service.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl {
    
    private final JavaMailSender mailSender;
    
    public void sendEmail(NotificationEntity notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getTitle());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            
            log.info("Email sent successfully to: {}", notification.getRecipient());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 