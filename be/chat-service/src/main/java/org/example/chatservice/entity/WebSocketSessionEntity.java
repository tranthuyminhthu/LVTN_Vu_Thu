package org.example.chatservice.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "websocket_session")
public class WebSocketSessionEntity {
    private String id;
    private String sessionId;
    private String userId;
    private LocalDateTime createdAt;
}
