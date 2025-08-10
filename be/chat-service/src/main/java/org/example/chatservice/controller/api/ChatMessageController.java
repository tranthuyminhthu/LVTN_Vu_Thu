package org.example.chatservice.controller.api;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.dto.ChatMessageRequestDto;
import org.example.chatservice.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ChatMessageRequestDto request) {
        try {
            return ResponseEntity.ok(chatMessageService.create(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMessages(@RequestParam String conversationId) {
        try {
            return ResponseEntity.ok(chatMessageService.getMessages(conversationId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
