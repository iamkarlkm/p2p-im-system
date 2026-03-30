package com.im.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * WebSocket服务器
 * 功能 #2: WebSocket实时推送服务 - 核心处理器
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class WebSocketServer extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    
    // 所有连接会话
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 用户ID到会话映射
    private final ConcurrentHashMap<String, Set<String>> userSessions = new ConcurrentHashMap<>();
    
    // 组播组
    private final ConcurrentHashMap<String, Set<String>> groupSubscriptions = new ConcurrentHashMap<>();
    
    // 心跳检测
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    
    // 消息统计
    private final AtomicLong messageCounter = new AtomicLong(0);
    
    public WebSocketServer() {
        // 启动心跳检测
        heartbeatExecutor.scheduleAtFixedRate(this::checkHeartbeats, 30, 30, TimeUnit.SECONDS);
    }
    
    // ==================== 连接管理 ====================
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        
        // 从URL参数获取用户ID
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            logger.info("WebSocket connected: session={}, user={}", sessionId, userId);
        } else {
            logger.info("WebSocket connected: session={} (anonymous)", sessionId);
        }
        
        // 发送连接确认
        sendMessage(sessionId, createSystemMessage("connected", "Connection established"));
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        
        // 从用户会话中移除
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            Set<String> userSessionIds = userSessions.get(userId);
            if (userSessionIds != null) {
                userSessionIds.remove(sessionId);
                if (userSessionIds.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        
        // 从所有组播组移除
        groupSubscriptions.forEach((group, members) -> members.remove(sessionId));
        
        logger.info("WebSocket disconnected: session={}, status={}", sessionId, status);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = session.getId();
        
        logger.debug("Received message from {}: {}", sessionId, payload);
        
        // 解析消息
        WebSocketMessage wsMessage = parseMessage(payload);
        if (wsMessage == null) return;
        
        // 处理不同消息类型
        switch (wsMessage.getType()) {
            case "heartbeat":
                handleHeartbeat(sessionId);
                break;
            case "subscribe":
                handleSubscribe(sessionId, wsMessage.getGroup());
                break;
            case "unsubscribe":
                handleUnsubscribe(sessionId, wsMessage.getGroup());
                break;
            case "ack":
                handleAck(sessionId, wsMessage.getMessageId());
                break;
            case "chat":
                handleChatMessage(sessionId, wsMessage);
                break;
            default:
                logger.warn("Unknown message type: {}", wsMessage.getType());
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error: session={}", session.getId(), exception);
    }
    
    // ==================== 消息推送 ====================
    
    /**
     * 单播推送
     */
    public boolean pushToUser(String userId, String message) {
        Set<String> sessionIds = userSessions.get(userId);
        if (sessionIds == null || sessionIds.isEmpty()) return false;
        
        boolean sent = false;
        for (String sessionId : sessionIds) {
            if (sendMessage(sessionId, message)) {
                sent = true;
            }
        }
        return sent;
    }
    
    /**
     * 单播推送（指定会话）
     */
    public boolean pushToSession(String sessionId, String message) {
        return sendMessage(sessionId, message);
    }
    
    /**
     * 广播推送
     */
    public int broadcast(String message) {
        int sent = 0;
        for (String sessionId : sessions.keySet()) {
            if (sendMessage(sessionId, message)) {
                sent++;
            }
        }
        return sent;
    }
    
    /**
     * 组播推送
     */
    public int pushToGroup(String group, String message) {
        Set<String> members = groupSubscriptions.get(group);
        if (members == null || members.isEmpty()) return 0;
        
        int sent = 0;
        for (String sessionId : new ArrayList<>(members)) {
            if (sendMessage(sessionId, message)) {
                sent++;
            }
        }
        return sent;
    }
    
    // ==================== 内部方法 ====================
    
    private boolean sendMessage(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) return false;
        
        try {
            session.sendMessage(new TextMessage(message));
            messageCounter.incrementAndGet();
            return true;
        } catch (IOException e) {
            logger.error("Failed to send message to session: {}", sessionId, e);
            return false;
        }
    }
    
    private void handleHeartbeat(String sessionId) {
        sendMessage(sessionId, createSystemMessage("heartbeat", "pong"));
    }
    
    private void handleSubscribe(String sessionId, String group) {
        if (group == null) return;
        groupSubscriptions.computeIfAbsent(group, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sendMessage(sessionId, createSystemMessage("subscribed", group));
        logger.debug("Session {} subscribed to group {}", sessionId, group);
    }
    
    private void handleUnsubscribe(String sessionId, String group) {
        if (group == null) return;
        Set<String> members = groupSubscriptions.get(group);
        if (members != null) {
            members.remove(sessionId);
        }
        sendMessage(sessionId, createSystemMessage("unsubscribed", group));
    }
    
    private void handleAck(String sessionId, String messageId) {
        logger.debug("Message {} acknowledged by session {}", messageId, sessionId);
    }
    
    private void handleChatMessage(String sessionId, WebSocketMessage message) {
        // 转发消息到目标用户或组
        if (message.getTargetUser() != null) {
            pushToUser(message.getTargetUser(), message.toJson());
        } else if (message.getTargetGroup() != null) {
            pushToGroup(message.getTargetGroup(), message.toJson());
        }
    }
    
    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        List<String> deadSessions = new ArrayList<>();
        
        sessions.forEach((sessionId, session) -> {
            Long lastHeartbeat = (Long) session.getAttributes().get("lastHeartbeat");
            if (lastHeartbeat != null && now - lastHeartbeat > 60000) {
                deadSessions.add(sessionId);
            }
        });
        
        deadSessions.forEach(this::closeSession);
    }
    
    private void closeSession(String sessionId) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.close(CloseStatus.GOING_AWAY);
            } catch (IOException e) {
                logger.error("Error closing session: {}", sessionId, e);
            }
        }
    }
    
    private String getUserIdFromSession(WebSocketSession session) {
        // 从URI参数或attributes获取用户ID
        return (String) session.getAttributes().get("userId");
    }
    
    private WebSocketMessage parseMessage(String payload) {
        try {
            // 简化解析，实际使用JSON解析器
            return new WebSocketMessage(payload);
        } catch (Exception e) {
            logger.error("Failed to parse message: {}", payload, e);
            return null;
        }
    }
    
    private String createSystemMessage(String type, String content) {
        return String.format("{\"type\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}", 
            type, content, System.currentTimeMillis());
    }
    
    // ==================== 统计 ====================
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", sessions.size());
        stats.put("totalUsers", userSessions.size());
        stats.put("totalGroups", groupSubscriptions.size());
        stats.put("totalMessages", messageCounter.get());
        return stats;
    }
    
    /**
     * WebSocket消息内部类
     */
    public static class WebSocketMessage {
        private String type;
        private String messageId;
        private String content;
        private String group;
        private String targetUser;
        private String targetGroup;
        private long timestamp;
        
        public WebSocketMessage(String payload) {
            this.timestamp = System.currentTimeMillis();
            // 简化解析
        }
        
        // Getters
        public String getType() { return type; }
        public String getMessageId() { return messageId; }
        public String getContent() { return content; }
        public String getGroup() { return group; }
        public String getTargetUser() { return targetUser; }
        public String getTargetGroup() { return targetGroup; }
        
        public String toJson() {
            return String.format("{\"type\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                type, content, timestamp);
        }
    }
}
