package org.example.chatservice.dao;

import org.example.chatservice.entity.WebSocketSessionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WebSocketSessionDao extends MongoRepository<WebSocketSessionEntity, String> {
    void deleteBySessionId(String sessionId);

    List<WebSocketSessionEntity> findAllByUserIdIn(List<String> userIds);

    WebSocketSessionEntity findBySessionId(String sessionId);
}
