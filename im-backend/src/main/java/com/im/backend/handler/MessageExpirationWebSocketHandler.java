package com.im.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息过期 WebSocket 处理器
 * 推送消息过期倒计时、即将过期提醒、消息已销毁通知
 */
@Component
public class MessageExpirationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    // userId -> sessions
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端可能发送: 消息阅读事件（启动计时）
        Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) data.get("type");
        if ("message_read".equals(type)) {
            // 收到客户端通知：某消息已被阅读
            Long messageId = ((Number) data.get("messageId")).longValue();
            Long userId = extractUserId(session);
            // 通知过期服务启动计时
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
    }

    /**
     * 发送消息即将过期提醒
     */
    public void sendPreExpireNotice(Long userId, Long messageId, Long remainingSeconds) throws Exception {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) return;

        Map<String, Object> payload = Map.of(
            "type", "message_pre_expire",
            "messageId", messageId,
            "remainingSeconds", remainingSeconds
        );
        String json = objectMapper.writeValueAsString(payload);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    /**
     * 发送消息已销毁通知
     */
    public void sendMessageDestroyed(Long userId, Long messageId) throws Exception {
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null) return;

        Map<String, Object> payload = Map.of(
            "type", "message_destroyed",
            "messageId", messageId
        );
        String json = objectMapper.writeValueAsString(payload);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    private Long extractUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        if (userId instanceof Long) return (Long) userId;
        if (userId instanceof String) return Long.parseLong((String) userId);
        return null;
    }
}
