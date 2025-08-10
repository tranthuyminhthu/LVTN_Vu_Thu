package org.example.chatservice.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final UserContext userContext;

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userContext.getUserId());
        userInfo.put("email", userContext.getEmail());
        userInfo.put("username", userContext.getUsername());
        userInfo.put("roles", userContext.getRoles());
        
        log.info("Chat Service - Getting user info for user: {}", userContext.getUserId());
        
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/send-message")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> messageRequest) {
        String message = messageRequest.get("message");
        
        // Kiểm tra quyền người dùng
        if (!userContext.hasRole("USER")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        
        log.info("Chat Service - User {} sending message: {}", userContext.getUserId(), message);
        
        // Xử lý logic gửi tin nhắn ở đây
        return ResponseEntity.ok(Map.of("status", "Message sent successfully"));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, String>> getAdminStats() {
        // Kiểm tra quyền admin
        if (!userContext.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        
        log.info("Chat Service - Admin {} accessing stats", userContext.getUserId());
        
        return ResponseEntity.ok(Map.of("stats", "Chat statistics for admin"));
    }
} 