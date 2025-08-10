package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.dao.WebSocketSessionDao;
import org.example.chatservice.entity.WebSocketSessionEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketSessionService {
    private final WebSocketSessionDao webSocketSessionDao;

    public void saveSession(String sessionId, String userId) {
        WebSocketSessionEntity sessionEntity = WebSocketSessionEntity.builder()
                .sessionId(sessionId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        webSocketSessionDao.save(sessionEntity);
    }

    public void deleteSession(String sessionId) {
        webSocketSessionDao.deleteBySessionId(sessionId);
    }

    public List<WebSocketSessionEntity> getSessionByUserId(List<String> userIds) {
        return webSocketSessionDao.findAllByUserIdIn(userIds);
    }

    public String getUserIdBySessionId(String sessionId) {
        WebSocketSessionEntity entity = webSocketSessionDao.findBySessionId(sessionId);
        return entity != null ? entity.getUserId() : null;
    }
}
