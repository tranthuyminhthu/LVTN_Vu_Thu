package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.dao.ConversationDao;
import org.example.chatservice.dao.UserClient;
import org.example.chatservice.dto.ConversationRequestDto;
import org.example.chatservice.dto.ConversationResponseDto;
import org.example.chatservice.dto.ParticipantInfoDto;
import org.example.chatservice.entity.ConversationEntity;
import org.example.chatservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationDao conversationDao;
    private final UserContext userContext;
    private final UserClient userClient;

    public ConversationResponseDto create(ConversationRequestDto request) {
        // Fetch user infos
        String userId = userContext.getUserId();
        ParticipantInfoDto userInfoResponse = userClient.getUserInfo(userId);
        ParticipantInfoDto participantInfoResponse = userClient.getUserInfo(request.getParticipantIds().getFirst());

        if (Objects.isNull(userInfoResponse) || Objects.isNull(participantInfoResponse)) {
            throw new IllegalArgumentException("User information not found for the provided IDs.");
        }

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        userIds.add(participantInfoResponse.getUserId());

        List<String> sortedIds = userIds.stream().sorted().toList();
        String userIdHash = generateParticipantHash(sortedIds);

        List<ParticipantInfoDto> participantInfos = List.of(
                ParticipantInfoDto.builder()
                        .userId(userInfoResponse.getUserId())
                        .username(userInfoResponse.getUsername())
                        .avatar(userInfoResponse.getAvatar())
                        .build(),
                ParticipantInfoDto.builder()
                        .userId(participantInfoResponse.getUserId())
                        .username(participantInfoResponse.getUsername())
                        .avatar(participantInfoResponse.getAvatar())
                        .build()
        );

        // Build conversation info
        ConversationEntity conversation = ConversationEntity.builder()
                .participantsHash(userIdHash)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .participants(participantInfos)
                .build();
        if (conversationDao.findByParticipantsHash(userIdHash).isPresent()) {
            return toConversationResponse(conversationDao.findByParticipantsHash(userIdHash).get());
        }
        conversation = conversationDao.save(conversation);

        return toConversationResponse(conversation);
    }

    public List<ConversationResponseDto> getMyConversations() {
        String userId = userContext.getUserId();
        List<ConversationEntity> conversations = conversationDao.findAllByParticipantIdsContains(userId);

        return conversations.stream().map(this::toConversationResponse).toList();
    }

    private String generateParticipantHash(List<String> ids) {
        StringJoiner stringJoiner = new StringJoiner("_");
        ids.forEach(stringJoiner::add);

        // SHA 256

        return stringJoiner.toString();
    }

    private ConversationResponseDto toConversationResponse(ConversationEntity conversation) {
        String currentUserId = userContext.getUserId();

        ConversationResponseDto conversationResponse = new ConversationResponseDto();
        BeanUtils.copyProperties(conversation, conversationResponse);

        conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUserId))
                .findFirst().ifPresent(participantInfo -> {
                    conversationResponse.setConversationName(participantInfo.getUsername());
                    conversationResponse.setConversationAvatar(participantInfo.getAvatar());
                });

        return conversationResponse;
    }
}
