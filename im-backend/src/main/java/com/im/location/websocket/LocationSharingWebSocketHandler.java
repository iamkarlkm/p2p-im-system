package com.im.location.websocket;

import com.alibaba.fastjson2.JSON;
import com.im.location.dto.LocationSharingMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 位置共享WebSocket处理器
 * 处理实时位置推送
 */
@Slf4j
@Component
public class LocationSharingWebSocketHandler extends TextWebSocketHandler {
    
    // 会话ID -> WebSocket会话集合
    private static final Map<String, Set<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
    
    // 用户ID -> 会话ID 映射
    private static final Map<Long, String> userSessionMap = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionIdFromUri(session);
        Long userId = getUserIdFromSession(session);
        
        if (sessionId != null) {
            sessionMap.computeIfAbsent(sessionId, k -> new CopyOnWriteArraySet<>()).add(session);
            if (userId != null) {
                userSessionMap.put(userId, sessionId);
            }
            log.info("用户{}加入位置共享会话: {}", userId, sessionId);
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到位置更新消息: {}", payload);
        // 处理客户端发送的位置更新
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = getSessionIdFromUri(session);
        Long userId = getUserIdFromSession(session);
        
        if (sessionId != null) {
            Set<WebSocketSession> sessions = sessionMap.get(sessionId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    sessionMap.remove(sessionId);
                }
            }
        }
        
        if (userId != null) {
            userSessionMap.remove(userId);
        }
        
        log.info("用户{}断开位置共享连接", userId);
    }
    
    /**
     * 广播位置更新给会话所有成员
     */
    public void broadcastLocationUpdate(String sessionId, LocationSharingMemberResponse memberLocation) {
        Set<WebSocketSession> sessions = sessionMap.get(sessionId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        
        String message = JSON.toJSONString(Map.of(
            "type", "LOCATION_UPDATE",
            "data", memberLocation
        ));
        
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {
                log.error("发送位置更新失败", e);
            }
        }
    }
    
    /**
     * 广播围栏触发事件
     */
    public void broadcastGeofenceTrigger(String sessionId, Long userId, String triggerType, String message) {
        Set<WebSocketSession> sessions = sessionMap.get(sessionId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        
        String payload = JSON.toJSONString(Map.of(
            "type", "GEOFENCE_TRIGGER",
            "userId", userId,
            "triggerType", triggerType,
            "message", message
        ));
        
        TextMessage textMessage = new TextMessage(payload);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {
                log.error("发送围栏触发消息失败", e);
            }
        }
    }
    
    /**
     * 广播成员到达通知
     */
    public void broadcastMemberArrival(String sessionId, Long userId, String nickname) {
        Set<WebSocketSession> sessions = sessionMap.get(sessionId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        
        String message = JSON.toJSONString(Map.of(
            "type", "MEMBER_ARRIVAL",
            "userId", userId,
            "nickname", nickname,
            "message", nickname + " 已到达目的地"
        ));
        
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (IOException e) {
                log.error("发送到达通知失败", e);
            }
        }
    }
    
    private String getSessionIdFromUri(WebSocketSession session) {
        String uri = session.getUri().toString();
        // 从URI中提取会话ID: /ws/location/{sessionId}
        String[] parts = uri.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }
    
    private Long getUserIdFromSession(WebSocketSession session) {
        // 从session attributes获取用户ID
        Object userId = session.getAttributes().get("userId");
        return userId != null ? Long.valueOf(userId.toString()) : null;
    }
}
