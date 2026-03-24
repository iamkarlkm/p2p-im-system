package com.im.server.reaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ReactionWebSocketHandler implements WebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("Reaction WS connected: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        log.debug("Reaction WS message from {}: {}", session.getId(), message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("Reaction WS transport error: {}", exception.getMessage());
        sessions.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
        log.info("Reaction WS closed: {}", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() { return false; }

    public void broadcastReactionUpdate(String messageId, Object stats) {
        String payload = String.format(
            "{\"type\":\"reaction_update\",\"messageId\":\"%s\",\"stats\":%s}",
            messageId, toJson(stats)
        );
        for (WebSocketSession s : sessions.values()) {
            try {
                if (s.isOpen()) s.sendMessage(new TextMessage(payload));
            } catch (Exception e) {
                log.warn("Failed to send reaction update: {}", e.getMessage());
            }
        }
    }

    private String toJson(Object obj) {
        if (obj instanceof MessageReactionService.ReactionStats rs) {
            StringBuilder sb = new StringBuilder("{");
            rs.counts().forEach((k, v) -> sb.append("\"").append(k).append("\":").append(v).append(","));
            if (!rs.counts().isEmpty()) sb.setLength(sb.length() - 1);
            return sb.append("}").toString();
        }
        return "{}";
    }
}
