package org.example.chatservice.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.chatservice.dao.ChatMessageDao;
import org.example.chatservice.dao.ConversationDao;
import org.example.chatservice.dao.UserClient;
import org.example.chatservice.dto.ChatMessageRequestDto;
import org.example.chatservice.dto.ChatMessageResponseDto;
import org.example.chatservice.dto.ParticipantInfoDto;
import org.example.chatservice.entity.ChatMessageEntity;
import org.example.chatservice.entity.ConversationEntity;
import org.example.chatservice.entity.WebSocketSessionEntity;
import org.example.chatservice.util.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageDao chatMessageDao;
    private final UserContext userContext;
    private final ConversationDao conversationDao;
    private final UserClient userClient;
    private final SocketIOServer socketIOServer;
    private final WebSocketSessionService webSocketSessionService;
    private final ObjectMapper objectMapper;

    public List<ChatMessageResponseDto> getMessages(String conversationId) {
        String userId = userContext.getUserId();
        conversationDao.findById(conversationId)
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Conversation not found with id: " + conversationId, 1))
                .getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "User not part of the conversation with id: " + conversationId, 1));

        List<ChatMessageEntity> messages = chatMessageDao.findAllByConversationIdOrderByCreatedDateAsc(conversationId);
        return messages.stream().map(this::toChatMessageResponse).toList();
    }

    public ChatMessageResponseDto create(ChatMessageRequestDto request) throws JsonProcessingException {
        String userId = userContext.getUserId();
        // Validate conversationId
        ConversationEntity conversationEntity = conversationDao.findById(request.getConversationId())
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "Conversation not found with id: " + request.getConversationId(), 1));

        conversationEntity.getParticipants()
                .stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new EmptyResultDataAccessException(
                        "User not part of the conversation with id: " + request.getConversationId(), 1));

        // Get UserInfo from ProfileService
        ParticipantInfoDto userResponse = userClient.getUserInfo(userId);
        if (Objects.isNull(userResponse)) {
            throw new EmptyResultDataAccessException(
                    "User not found with id: " + userId, 1);
        }

        // Build Chat message Info
        ChatMessageEntity chatMessage = new ChatMessageEntity();
        BeanUtils.copyProperties(request, chatMessage);
        chatMessage.setSender(ParticipantInfoDto.builder()
                .userId(userResponse.getUserId())
                .username(userResponse.getUsername())
                .avatar(userResponse.getAvatar())
                .build());
        chatMessage.setCreatedDate(LocalDateTime.now());

        // Create chat message
        chatMessage = chatMessageDao.save(chatMessage);

        List<String> userIds = conversationEntity.getParticipants()
                .stream()
                .map(ParticipantInfoDto::getUserId).toList();

        Set<String> sectionIds = webSocketSessionService.getSessionByUserId(userIds).stream().map(
                WebSocketSessionEntity::getSessionId
        ).collect(Collectors.toSet());
        // publish socket event to clients
        ChatMessageEntity finalChatMessage = chatMessage;
        socketIOServer.getAllClients().forEach(client -> {
            if (sectionIds.contains(client.getSessionId().toString())) {
                try {
                    String clientUserId = webSocketSessionService.getUserIdBySessionId(client.getSessionId().toString());
                    // Chỉ gửi cho người nhận, không gửi cho người gửi
                    if (clientUserId != null && !clientUserId.equals(finalChatMessage.getSender().getUserId())) {
                        ChatMessageResponseDto responseForClient = toChatMessageResponseForUser(finalChatMessage, clientUserId);
                        client.sendEvent("message", objectMapper.writeValueAsString(responseForClient));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // convert to Response
        return toChatMessageResponse(chatMessage);
    }

    private ChatMessageResponseDto toChatMessageResponse(ChatMessageEntity chatMessage) {
        String userId = userContext.getUserId();
        ChatMessageResponseDto chatMessageResponse = new ChatMessageResponseDto();
        BeanUtils.copyProperties(chatMessage, chatMessageResponse);

        chatMessageResponse.setIsMe(userId.equals(chatMessage.getSender().getUserId()));

        return chatMessageResponse;
    }

    private ChatMessageResponseDto toChatMessageResponseForUser(ChatMessageEntity chatMessage, String userId) {
        ChatMessageResponseDto chatMessageResponse = new ChatMessageResponseDto();
        BeanUtils.copyProperties(chatMessage, chatMessageResponse);
        chatMessageResponse.setIsMe(userId != null && userId.equals(chatMessage.getSender().getUserId()));
        return chatMessageResponse;
    }
}
