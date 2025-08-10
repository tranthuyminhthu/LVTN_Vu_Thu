package org.example.chatservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationResponseDto {
    private String id;
    private String participantsHash;
    private String conversationAvatar;
    private String conversationName;
    private List<ParticipantInfoDto> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
