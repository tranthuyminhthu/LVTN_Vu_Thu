package org.example.notification_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notification_service.entity.NotificationEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;
    
    public void sendEmail(NotificationEntity notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getTitle());
            
            // Determine template based on notification category
            String templateName = getTemplateName(notification);
            String htmlContent = generateHtmlContent(notification, templateName);
            
            helper.setText(htmlContent, true); // true = HTML content
            
            mailSender.send(message);
            
            log.info("HTML email sent successfully to: {}", notification.getRecipient());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", notification.getRecipient(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    private String getTemplateName(NotificationEntity notification) {
        if ("SECURITY".equals(notification.getCategory().name())) {
            if (notification.getTitle().contains("Reset") || notification.getTitle().contains("Đặt Lại")) {
                return "password-reset";
            } else if (notification.getTitle().contains("Changed") || notification.getTitle().contains("Thành Công")) {
                return "password-change-confirmation";
            }
        }
        return "default-email";
    }
    
    private String generateHtmlContent(NotificationEntity notification, String templateName) {
        try {
            Context context = new Context();
            
            // Add basic notification data
            context.setVariable("title", notification.getTitle());
            context.setVariable("content", notification.getContent());
            context.setVariable("recipient", notification.getRecipient());
            
            // Add data from notification.data if available
            if (notification.getData() != null) {
                Map<String, Object> data = objectMapper.readValue(notification.getData(), Map.class);
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
            }
            
            // Process template
            return templateEngine.process(templateName, context);
            
        } catch (Exception e) {
            log.warn("Failed to process template {}, using fallback: {}", templateName, e.getMessage());
            return generateFallbackHtml(notification);
        }
    }
    
    private String generateFallbackHtml(NotificationEntity notification) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .button { display: inline-block; background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <p>%s</p>
                        %s
                    </div>
                    <div class="footer">
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                notification.getTitle(),
                notification.getTitle(),
                notification.getContent(),
                generateActionButtons(notification)
            );
    }
    
    private String generateActionButtons(NotificationEntity notification) {

        Map<String, Object> data = objectMapper.convertValue(notification.getData(), Map.class);
        if (notification.getData() != null && data.containsKey("resetLink")) {
            String resetLink = data.get("resetLink").toString();
            return """
                <p><a href="%s" class="button">Reset Password</a></p>
                <p><small>This link will expire in 24 hours.</small></p>
                """.formatted(resetLink);
        }
        return "";
    }
} 
