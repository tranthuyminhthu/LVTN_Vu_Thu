package org.example.authservice.dao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-client", url = "${service.notification}")
public interface NotificationClient {
    
    @PostMapping(value = "/api/notifications/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> notification);
} 