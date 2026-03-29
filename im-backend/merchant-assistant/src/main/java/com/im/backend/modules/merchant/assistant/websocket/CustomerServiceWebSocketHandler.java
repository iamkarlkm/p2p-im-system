package com.im.backend.modules.merchant.assistant.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.backend.modules.merchant.assistant.dto.MessageResponse;
import com.im.backend.modules.merchant.assistant.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客服消息WebSocket处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerServiceWebSocketHandler extends TextWebSocketHandler {
    
    private final ObjectMapper objectMapper;
    private final IChatbotService chatbotService;
    
    // 会话ID -> WebSocketSession 映射
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    
    // 用户ID -> 会话ID 映射
    private final Map<Long, String> userSessionMap = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = getSessionIdFromUri(session);
        Long userId = getUserIdFromUri(session);
        
        if (sessionId != null) {
            sessionMap.put(sessionId, session);
        }
        if (userId != null) {
            userSessionMap.put(userId, sessionId);
        }
        
        log.info("客服WebSocket连接建立: sessionId={}, userId={}", sessionId, userId);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到消息: {}", payload);
        
        try {
            // 解析消息
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            // 根据消息类型处理
            switch (wsMessage.getType()) {
                case "ping":
                    sendPong(session);
                    break;
                case "read_receipt":
                    handleReadReceipt(wsMessage);
                    break;
                default:
                    log.warn("未知消息类型: {}", wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = getSessionIdFromUri(session);
        Long userId = getUserIdFromUri(session);
        
        if (sessionId != null) {
            sessionMap.remove(sessionId);
        }
        if (userId != null) {
            userSessionMap.remove(userId);
        }
        
        log.info("客服WebSocket连接关闭: sessionId={}, userId={}, status={}", sessionId, userId, status);
    }
    
    /**
     * 推送消息到用户
     */
    public void pushMessage(Long userId, MessageResponse message) {
        String sessionId = userSessionMap.get(userId);
        if (sessionId == null) {
            return;
        }
        
        WebSocketSession session = sessionMap.get(sessionId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            String payload = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            log.error("推送消息失败: userId={}", userId, e);
        }
    }
    
    /**
     * 推送消息到会话
     */
    public void pushMessageToSession(String sessionId, MessageResponse message) {
        WebSocketSession session = sessionMap.get(sessionId);
        if (session == null || !session.isOpen()) {
            return;
        }
        
        try {
            String payload = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            log.error("推送消息失败: sessionId={}", sessionId, e);
        }
    }
    
    // ============ 私有方法 ============
    
    private String getSessionIdFromUri(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && "sessionId".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
    
    private Long getUserIdFromUri(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            String[] kv = param.split("=");
            if (kv.length == 2 && "userId".equals(kv[0])) {
                try {
                    return Long.parseLong(kv[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    private void sendPong(WebSocketSession session) throws IOException {
        WebSocketMessage pong = new WebSocketMessage();
        pong.setType("pong");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
    }
    
    private void handleReadReceipt(WebSocketMessage message) {
        // 处理已读回执
        log.debug("收到已读回执: {}", message.getData());
    }
    
    /**
     * WebSocket消息结构
     */
    @lombok.Data
    public static class WebSocketMessage {
        private String type;
        private Object data;
    }
}
