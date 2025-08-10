package org.example.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    private String id;
    private String conversationId;
    private Boolean isMe;
    private String message;
    private ParticipantInfoDto sender;
    private LocalDateTime createdDate;
}
