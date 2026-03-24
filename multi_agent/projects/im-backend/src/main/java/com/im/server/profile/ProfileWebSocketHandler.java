package com.im.server.profile;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 用户状态WebSocket处理器
 */
@Component
public class ProfileWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final UserProfileService profileService;

    public ProfileWebSocketHandler(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            profileService.setUserOnline(userId);
            broadcastStatusChange(userId, "ONLINE");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserId(session);
        if (userId == null) return;

        Map<String, Object> payload = parseJson(message.getPayload());
        String action = (String) payload.get("action");

        switch (action) {
            case "status_update" -> {
                String statusStr = (String) payload.get("status");
                UserProfile.UserStatus status = UserProfile.UserStatus.valueOf(statusStr);
                profileService.updateStatus(userId, status);
                broadcastStatusChange(userId, statusStr);
            }
            case "typing" -> {
                String chatId = (String) payload.get("chatId");
                boolean isTyping = (Boolean) payload.getOrDefault("isTyping", false);
                broadcastTyping(userId, chatId, isTyping);
            }
            case "profile_update" -> {
                // 广播资料更新
                broadcastProfileUpdate(userId, payload);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            profileService.setUserOffline(userId);
            broadcastStatusChange(userId, "OFFLINE");
        }
    }

    private void broadcastStatusChange(String userId, String status) {
        String json = String.format(
                "{\"type\":\"status_change\",\"userId\":\"%s\",\"status\":\"%s\"}", userId, status);
        broadcast(json, Set.of(userId));
    }

    private void broadcastTyping(String userId, String chatId, boolean isTyping) {
        String json = String.format(
                "{\"type\":\"typing\",\"userId\":\"%s\",\"chatId\":\"%s\",\"isTyping\":%b}",
                userId, chatId, isTyping);
        // 广播给聊天相关用户（简化：广播给所有在线用户）
        broadcast(json, userSessions.keySet());
    }

    private void broadcastProfileUpdate(String userId, Map<String, Object> payload) {
        String json = String.format(
                "{\"type\":\"profile_update\",\"userId\":\"%s\",\"changes\":%s}",
                userId, payload.get("changes"));
        broadcast(json, userSessions.keySet());
    }

    private void broadcast(String message, Set<String> excludeUsers) {
        TextMessage textMsg = new TextMessage(message);
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            if (!excludeUsers.contains(entry.getKey()) && entry.getValue().isOpen()) {
                try {
                    entry.getValue().sendMessage(textMsg);
                } catch (Exception ignored) {}
            }
        }
    }

    private String getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? userId.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJson(String json) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
