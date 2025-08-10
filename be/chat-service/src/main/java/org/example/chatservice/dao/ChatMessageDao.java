package org.example.chatservice.dao;

import org.example.chatservice.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageDao extends MongoRepository<ChatMessageEntity, String> {
    List<ChatMessageEntity> findAllByConversationIdOrderByCreatedDateAsc(String conversationId);
}
