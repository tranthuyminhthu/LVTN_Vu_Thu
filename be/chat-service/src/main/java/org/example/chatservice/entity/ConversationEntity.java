package org.example.chatservice.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.chatservice.dto.ParticipantInfoDto;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationEntity {
    @MongoId
    String id;

    @Indexed(unique = true)
    String participantsHash;

    List<ParticipantInfoDto> participants;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
