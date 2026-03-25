package com.im.backend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户状态WebSocket处理器
 * 处理在线状态的实时广播
 */
@Component
public class UserProfileWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // userId -> WebSocket session
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Autowired
    private UserProfileService userProfileService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            // 广播用户上线
            broadcastUserStatus(userId, "ONLINE");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) data.get("type");
        
        switch (type) {
            case "STATUS_UPDATE":
                handleStatusUpdate(session, data);
                break;
            case "PING":
                session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            // 广播用户离线
            broadcastUserStatus(userId, "OFFLINE");
        }
    }

    private void handleStatusUpdate(WebSocketSession session, Map<String, Object> data) {
        Long userId = extractUserId(session);
        if (userId == null) return;
        
        String status = (String) data.get("status");
        String statusText = (String) data.get("statusText");
        
        Map<String, Object> update = new HashMap<>();
        update.put("type", "STATUS_CHANGED");
        update.put("userId", userId);
        update.put("status", status);
        update.put("statusText", statusText);
        update.put("timestamp", System.currentTimeMillis());
        
        // 广播给所有在线用户
        broadcastToAll(new TextMessage(objectMapper.writeValueAsString(update)));
    }

    private void broadcastUserStatus(Long userId, String status) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "USER_STATUS_CHANGED");
        msg.put("userId", userId);
        msg.put("status", status);
        msg.put("timestamp", System.currentTimeMillis());
        
        try {
            broadcastToAll(new TextMessage(objectMapper.writeValueAsString(msg)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastToAll(TextMessage message) {
        for (WebSocketSession session : userSessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    private Long extractUserId(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : "";
        for (String param : query.split("&")) {
            if (param.startsWith("userId=")) {
                return Long.parseLong(param.substring(7));
            }
        }
        return null;
    }

    /**
     * 获取当前在线用户数
     */
    public int getOnlineUserCount() {
        return userSessions.size();
    }

    /**
     * 获取所有在线用户ID列表
     */
    public List<Long> getOnlineUserIds() {
        return new ArrayList<>(userSessions.keySet());
    }
}
