package org.example.chatservice.controller.api;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chatservice.dao.UserClientApi;
import org.example.chatservice.entity.WebSocketSessionEntity;
import org.example.chatservice.service.WebSocketSessionService;
import org.springframework.stereotype.Component;
import org.example.chatservice.dto.ChatMessageRequestDto;
import org.example.chatservice.dto.ChatMessageResponseDto;
import org.example.chatservice.service.ChatMessageService;

@Component
@Slf4j
@RequiredArgsConstructor
public class SocketHandler {
    private final SocketIOServer socketIOServer;
    private final ChatMessageService chatMessageService;
    private final UserClientApi userClientApi;
    private final WebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        // get token
        String token = client.getHandshakeData().getSingleUrlParam("token");
        // validate token and user
        if (token == null || token.isEmpty()) {
            client.disconnect();
            log.warn("Client disconnected due to missing token: {}", client.getSessionId());
            return;
        }
        try {
            String userId = userClientApi.introspect("Bearer " + token)
                    .getHeaders()
                    .get("location")
                    .getFirst()
                    .replace("/internal/profile/", "");
            webSocketSessionService.saveSession(
                    client.getSessionId().toString(),
                    userId);
        } catch (Exception e) {
            client.disconnect();
            webSocketSessionService.deleteSession(client.getSessionId().toString());
            return;
        }
        log.info("Client connected: {} with token {}", client.getSessionId(), token);
    }

    @OnDisconnect
    public void clientDisConnected(SocketIOClient client) {
        log.info("Client disconnected: {}", client.getSessionId());
        webSocketSessionService.deleteSession(client.getSessionId().toString());
    }

    @PostConstruct
    public void init() {
        socketIOServer.start();
        socketIOServer.addListeners(this);
        log.info("Socket.IO server started on port {}", socketIOServer.getConfiguration().getPort());
    }

    @PreDestroy
    public void destroy() {
        socketIOServer.stop();
        log.info("Socket.IO server stopped");
    }
}
