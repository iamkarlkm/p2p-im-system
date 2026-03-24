package com.im.server.typing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TypingWebSocketHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("Typing WS connected: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            String payload = message.getPayload().toString();
            log.debug("Typing WS message: {}", payload);
        } catch (Exception e) {
            log.warn("Typing WS handle error: {}", e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() { return false; }

    public void broadcastTyping(String conversationId, String userId, TypingIndicator.TypingState state) {
        String payload = String.format(
            "{\"type\":\"typing\",\"conversationId\":\"%s\",\"userId\":\"%s\",\"state\":\"%s\"}",
            conversationId, userId, state.name()
        );
        for (WebSocketSession s : sessions.values()) {
            try {
                if (s.isOpen()) s.sendMessage(new TextMessage(payload));
            } catch (Exception e) {
                log.warn("Failed to send typing: {}", e.getMessage());
            }
        }
    }
}
