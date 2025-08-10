package org.example.chatservice.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.dto.ConversationRequestDto;
import org.example.chatservice.service.ConversationService;
import org.example.chatservice.util.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("conversations")
public class ChatController {
    private final ConversationService conversationService;

    @PostMapping
    public ResponseEntity<?> createConversation(@RequestBody ConversationRequestDto request) {
        try {
            return ResponseEntity.ok(conversationService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-conversations")
    public ResponseEntity<?> myConversations() {
        try {
            return ResponseEntity.ok(conversationService.getMyConversations());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 
