package com.im.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket连接管理器
 * 功能 #2: WebSocket实时推送服务
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class WebSocketConnectionManager extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionManager.class);
    
    // 活跃连接池
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 用户ID到Session映射
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    
    // 心跳检测
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    
    // 最后心跳时间
    private final Map<String, LocalDateTime> lastHeartbeat = new ConcurrentHashMap<>();
    
    public WebSocketConnectionManager() {
        // 启动心跳检测
        heartbeatExecutor.scheduleAtFixedRate(this::checkHeartbeat, 30, 30, TimeUnit.SECONDS);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        lastHeartbeat.put(sessionId, LocalDateTime.now());
        
        // 从URL参数获取用户ID
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessionMap.put(userId, sessionId);
        }
        
        logger.info("WebSocket connection established: {}, user: {}", sessionId, userId);
        
        // 发送连接成功消息
        sendMessage(sessionId, "{\"type\":\"connected\",\"sessionId\":\"" + sessionId + "\"}");
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sessionId = session.getId();
        
        // 更新心跳
        lastHeartbeat.put(sessionId, LocalDateTime.now());
        
        // 处理心跳消息
        if (payload.contains("\"type\":\"ping\"")) {
            sendMessage(sessionId, "{\"type\":\"pong\",\"timestamp\":" + System.currentTimeMillis() + "}");
            return;
        }
        
        logger.debug("Received message from {}: {}", sessionId, payload);
        
        // 处理认证消息
        if (payload.contains("\"type\":\"auth\"")) {
            handleAuth(session, payload);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        lastHeartbeat.remove(sessionId);
        
        // 清理用户映射
        userSessionMap.values().remove(sessionId);
        
        logger.info("WebSocket connection closed: {}, status: {}", sessionId, status);
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("WebSocket transport error: {}", session.getId(), exception);
    }
    
    // ==================== 消息推送 ====================
    
    /**
     * 单播推送
     */
    public boolean sendToUser(String userId, String message) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId != null) {
            return sendMessage(sessionId, message);
        }
        return false;
    }
    
    /**
     * 广播推送
     */
    public void broadcast(String message) {
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                logger.error("Failed to broadcast to: {}", session.getId(), e);
            }
        });
    }
    
    /**
     * 组播推送
     */
    public void multicast(String groupId, String message) {
        // 根据groupId筛选用户推送
        userSessionMap.forEach((userId, sessionId) -> {
            if (userId.startsWith(groupId)) {
                sendMessage(sessionId, message);
            }
        });
    }
    
    /**
     * 发送消息到指定session
     */
    public boolean sendMessage(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                return true;
            } catch (IOException e) {
                logger.error("Failed to send message to: {}", sessionId, e);
            }
        }
        return false;
    }
    
    // ==================== 内部方法 ====================
    
    private void handleAuth(WebSocketSession session, String payload) {
        // 解析认证信息并绑定用户
        String userId = extractUserId(payload);
        if (userId != null) {
            userSessionMap.put(userId, session.getId());
            logger.info("User authenticated: {}, session: {}", userId, session.getId());
        }
    }
    
    private String getUserIdFromSession(WebSocketSession session) {
        // 从URL参数或属性获取用户ID
        return null;
    }
    
    private String extractUserId(String payload) {
        // 简单解析，实际使用JSON解析
        int start = payload.indexOf("\"userId\":\"");
        if (start != -1) {
            start += 10;
            int end = payload.indexOf("\"", start);
            return payload.substring(start, end);
        }
        return null;
    }
    
    /**
     * 心跳检测
     */
    private void checkHeartbeat() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(60);
        
        lastHeartbeat.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(threshold)) {
                String sessionId = entry.getKey();
                WebSocketSession session = sessions.get(sessionId);
                if (session != null) {
                    try {
                        session.close(CloseStatus.SESSION_NOT_RELIABLE);
                        logger.warn("Session closed due to heartbeat timeout: {}", sessionId);
                    } catch (IOException e) {
                        logger.error("Failed to close session: {}", sessionId, e);
                    }
                }
                return true;
            }
            return false;
        });
    }
    
    /**
     * 获取在线用户数
     */
    public int getOnlineCount() {
        return sessions.size();
    }
    
    /**
     * 获取用户在线状态
     */
    public boolean isUserOnline(String userId) {
        return userSessionMap.containsKey(userId);
    }
    
    /**
     * 断开用户连接
     */
    public void disconnectUser(String userId) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId != null) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null) {
                try {
                    session.close(CloseStatus.NORMAL);
                } catch (IOException e) {
                    logger.error("Failed to disconnect user: {}", userId, e);
                }
            }
        }
    }
}
