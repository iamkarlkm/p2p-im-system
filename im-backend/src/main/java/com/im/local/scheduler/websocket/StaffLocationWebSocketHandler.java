package com.im.local.scheduler.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.local.scheduler.dto.UpdateStaffLocationRequest;
import com.im.local.scheduler.service.IDeliveryStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 骑手位置WebSocket处理器
 * 实时推送骑手位置更新
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaffLocationWebSocketHandler extends TextWebSocketHandler {
    
    private final IDeliveryStaffService staffService;
    private final ObjectMapper objectMapper;
    
    // staffId -> Session映射
    private static final ConcurrentHashMap<Long, WebSocketSession> staffSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long staffId = extractStaffId(session);
        if (staffId != null) {
            staffSessions.put(staffId, session);
            log.info("骑手WebSocket连接建立: staffId={}, sessionId={}", staffId, session.getId());
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("收到骑手位置消息: {}", payload);
        
        try {
            UpdateStaffLocationRequest request = objectMapper.readValue(payload, UpdateStaffLocationRequest.class);
            boolean success = staffService.updateStaffLocation(request);
            
            if (success) {
                session.sendMessage(new TextMessage("{\"status\":\"ok\"}"));
            } else {
                session.sendMessage(new TextMessage("{\"status\":\"error\",\"message\":\"更新失败\"}"));
            }
        } catch (Exception e) {
            log.error("处理骑手位置消息失败", e);
            session.sendMessage(new TextMessage("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}"));
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long staffId = extractStaffId(session);
        if (staffId != null) {
            staffSessions.remove(staffId);
            log.info("骑手WebSocket连接关闭: staffId={}, status={}", staffId, status);
        }
    }
    
    /**
     * 广播消息给指定围栏内的骑手
     */
    public void broadcastToGeofence(Long geofenceId, String message) {
        staffSessions.forEach((staffId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("广播消息失败: staffId={}", staffId, e);
                }
            }
        });
    }
    
    /**
     * 发送消息给指定骑手
     */
    public void sendToStaff(Long staffId, String message) {
        WebSocketSession session = staffSessions.get(staffId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送消息失败: staffId={}", staffId, e);
            }
        }
    }
    
    private Long extractStaffId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        if (parts.length > 0) {
            try {
                return Long.valueOf(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
