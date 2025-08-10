package org.example.chatservice.entity;

import lombok.*;
import org.example.chatservice.dto.ParticipantInfoDto;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_message")
public class ChatMessageEntity {
    @MongoId
    private String id;
    @Indexed
    private String conversationId;
    private String message;
    private ParticipantInfoDto sender;
    @Indexed
    LocalDateTime createdDate;
}
