package com.im.service.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.websocket.config.WebSocketConfig;
import com.im.service.websocket.manager.WebSocketSessionManager;
import com.im.service.websocket.model.WebSocketMessage;
import com.im.service.websocket.service.OnlineStatusService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 消息处理器
 * 
 * 核心功能:
 * 1. 处理 WebSocket 连接生命周期 (建立/关闭)
 * 2. 处理客户端发送的各类消息
 * 3. 消息实时推送 (单聊/群聊/系统通知)
 * 4. 心跳检测处理
 * 5. 已读回执处理
 * 6. 消息撤回通知
 * 
 * 消息类型:
 * - ping/pong           : 心跳检测
 * - chat                : 单聊消息
 * - group_chat          : 群聊消息
 * - read_receipt        : 已读回执
 * - recall              : 消息撤回
 * - typing              : 正在输入
 * - presence            : 在线状态更新
 * - ack                 : 消息确认
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;
    private final OnlineStatusService onlineStatusService;

    // ==================== 握手处理器 ====================
    
    @Getter
    private final HandshakeHandler handshakeHandler = new DefaultHandshakeHandler() {
        @Override
        protected Principal determineUser(ServerHttpRequest request, 
                                         WebSocketHandler wsHandler,
                                         Map<String, Object> attributes) {
            // 从请求属性中获取用户ID
            Long userId = (Long) attributes.get("userId");
            if (userId != null) {
                return new WebSocketPrincipal(userId.toString());
            }
            return null;
        }
    };

    // ==================== 连接生命周期处理 ====================

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        String deviceId = getDeviceIdFromSession(session);
        String sessionId = session.getId();
        
        if (userId == null) {
            log.warn("WebSocket 连接未携带用户ID，关闭连接: sessionId={}", sessionId);
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        
        // 注册会话
        sessionManager.registerSession(userId, deviceId, session);
        
        // 更新用户在线状态
        onlineStatusService.userOnline(userId, deviceId);
        
        // 发送连接成功消息
        sendConnectionEstablished(session, userId);
        
        log.info("WebSocket 连接建立: userId={}, deviceId={}, sessionId={}", 
                userId, deviceId, sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Long userId = getUserIdFromSession(session);
        
        if (userId == null) {
            log.warn("收到消息但未识别用户: sessionId={}", session.getId());
            return;
        }
        
        try {
            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            
            log.debug("收到 WebSocket 消息: userId={}, type={}", userId, wsMessage.getType());
            
            // 根据消息类型分发处理
            switch (wsMessage.getType()) {
                case "ping":
                    handlePing(session, wsMessage);
                    break;
                case "chat":
                    handleChatMessage(session, userId, wsMessage);
                    break;
                case "group_chat":
                    handleGroupChatMessage(session, userId, wsMessage);
                    break;
                case "read_receipt":
                    handleReadReceipt(session, userId, wsMessage);
                    break;
                case "recall":
                    handleRecallMessage(session, userId, wsMessage);
                    break;
                case "typing":
                    handleTypingNotification(session, userId, wsMessage);
                    break;
                case "presence":
                    handlePresenceUpdate(session, userId, wsMessage);
                    break;
                case "ack":
                    handleAck(session, userId, wsMessage);
                    break;
                default:
                    log.warn("未知消息类型: type={}, userId={}", wsMessage.getType(), userId);
                    sendError(session, "UNKNOWN_MESSAGE_TYPE", "Unknown message type: " + wsMessage.getType());
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息失败: userId={}, payload={}", userId, payload, e);
            sendError(session, "MESSAGE_PARSE_ERROR", e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = getUserIdFromSession(session);
        log.error("WebSocket 传输错误: userId={}, sessionId={}, error={}", 
                userId, session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        String deviceId = getDeviceIdFromSession(session);
        String sessionId = session.getId();
        
        if (userId != null) {
            // 注销会话
            sessionManager.unregisterSession(userId, deviceId, sessionId);
            
            // 更新用户离线状态 (如果该用户没有活跃会话)
            if (!sessionManager.hasActiveSession(userId)) {
                onlineStatusService.userOffline(userId, deviceId);
            }
        }
        
        log.info("WebSocket 连接关闭: userId={}, deviceId={}, sessionId={}, status={}", 
                userId, deviceId, sessionId, status);
    }

    // ==================== 消息类型处理 ====================

    /**
     * 处理心跳 ping
     */
    private void handlePing(WebSocketSession session, WebSocketMessage message) throws IOException {
        WebSocketMessage pong = WebSocketMessage.builder()
                .type("pong")
                .timestamp(System.currentTimeMillis())
                .data(Map.of("serverTime", System.currentTimeMillis()))
                .build();
        
        sendMessage(session, pong);
        
        // 更新最后活动时间
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessionManager.updateLastActivityTime(userId, session.getId());
        }
    }

    /**
     * 处理单聊消息
     */
    private void handleChatMessage(WebSocketSession session, Long senderId, WebSocketMessage message) {
        Long receiverId = message.getReceiverId();
        if (receiverId == null) {
            sendError(session, "MISSING_RECEIVER", "Receiver ID is required");
            return;
        }
        
        // 添加发送者信息
        message.setSenderId(senderId);
        message.setTimestamp(System.currentTimeMillis());
        
        // 发送消息确认给发送者
        sendAck(session, message.getMessageId());
        
        // 推送给接收者
        boolean delivered = pushMessageToUser(receiverId, message);
        
        // 发送送达回执给发送者
        if (delivered) {
            sendDeliveryReceipt(session, message.getMessageId(), receiverId);
        }
        
        log.debug("单聊消息处理完成: senderId={}, receiverId={}, messageId={}, delivered={}",
                senderId, receiverId, message.getMessageId(), delivered);
    }

    /**
     * 处理群聊消息
     */
    private void handleGroupChatMessage(WebSocketSession session, Long senderId, WebSocketMessage message) {
        Long groupId = message.getGroupId();
        if (groupId == null) {
            sendError(session, "MISSING_GROUP_ID", "Group ID is required");
            return;
        }
        
        // 添加发送者信息
        message.setSenderId(senderId);
        message.setTimestamp(System.currentTimeMillis());
        
        // 发送消息确认给发送者
        sendAck(session, message.getMessageId());
        
        // 广播给群组成员 (排除发送者)
        broadcastToGroup(groupId, message, senderId);
        
        log.debug("群聊消息处理完成: senderId={}, groupId={}, messageId={}",
                senderId, groupId, message.getMessageId());
    }

    /**
     * 处理已读回执
     */
    private void handleReadReceipt(WebSocketSession session, Long readerId, WebSocketMessage message) {
        String messageId = message.getMessageId();
        Long senderId = message.getSenderId();
        
        if (messageId == null || senderId == null) {
            sendError(session, "INVALID_READ_RECEIPT", "Message ID and Sender ID are required");
            return;
        }
        
        // 构建已读回执消息
        WebSocketMessage receipt = WebSocketMessage.builder()
                .type("read_receipt")
                .messageId(messageId)
                .senderId(readerId)
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "messageId", messageId,
                        "readerId", readerId,
                        "readTime", System.currentTimeMillis()
                ))
                .build();
        
        // 推送给原消息发送者
        pushMessageToUser(senderId, receipt);
        
        log.debug("已读回执处理完成: readerId={}, senderId={}, messageId={}",
                readerId, senderId, messageId);
    }

    /**
     * 处理消息撤回
     */
    private void handleRecallMessage(WebSocketSession session, Long operatorId, WebSocketMessage message) {
        String messageId = message.getMessageId();
        Long conversationId = message.getConversationId();
        Boolean isGroup = message.getIsGroup();
        
        if (messageId == null) {
            sendError(session, "MISSING_MESSAGE_ID", "Message ID is required");
            return;
        }
        
        // 构建撤回通知
        WebSocketMessage recallNotification = WebSocketMessage.builder()
                .type("recall")
                .messageId(messageId)
                .senderId(operatorId)
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "messageId", messageId,
                        "operatorId", operatorId,
                        "recallTime", System.currentTimeMillis()
                ))
                .build();
        
        // 根据会话类型推送
        if (Boolean.TRUE.equals(isGroup) && conversationId != null) {
            // 群聊撤回 - 广播给所有群成员
            broadcastToGroup(conversationId, recallNotification, null);
        } else if (message.getReceiverId() != null) {
            // 单聊撤回 - 推送给对方
            pushMessageToUser(message.getReceiverId(), recallNotification);
        }
        
        log.debug("消息撤回处理完成: operatorId={}, messageId={}, isGroup={}",
                operatorId, messageId, isGroup);
    }

    /**
     * 处理正在输入通知
     */
    private void handleTypingNotification(WebSocketSession session, Long senderId, WebSocketMessage message) {
        Long receiverId = message.getReceiverId();
        Long groupId = message.getGroupId();
        
        message.setSenderId(senderId);
        message.setTimestamp(System.currentTimeMillis());
        
        if (groupId != null) {
            // 群聊正在输入 - 广播给群组成员
            broadcastToGroup(groupId, message, senderId);
        } else if (receiverId != null) {
            // 单聊正在输入 - 推送给对方
            pushMessageToUser(receiverId, message);
        }
    }

    /**
     * 处理在线状态更新
     */
    private void handlePresenceUpdate(WebSocketSession session, Long userId, WebSocketMessage message) {
        String status = message.getStatus();
        if (status == null) {
            return;
        }
        
        // 更新用户在线状态
        onlineStatusService.updatePresence(userId, status);
        
        log.debug("在线状态更新: userId={}, status={}", userId, status);
    }

    /**
     * 处理消息确认 (ACK)
     */
    private void handleAck(WebSocketSession session, Long userId, WebSocketMessage message) {
        String messageId = message.getMessageId();
        if (messageId != null) {
            // 确认消息已收到
            log.debug("收到消息确认: userId={}, messageId={}", userId, messageId);
        }
    }

    // ==================== 消息推送方法 ====================

    /**
     * 推送消息给指定用户 (所有设备)
     * 
     * @param userId  目标用户ID
     * @param message 消息内容
     * @return 是否成功推送到至少一个设备
     */
    public boolean pushMessageToUser(Long userId, WebSocketMessage message) {
        Set<WebSocketSession> sessions = sessionManager.getUserSessions(userId);
        
        if (sessions == null || sessions.isEmpty()) {
            log.debug("用户不在线，消息未推送: userId={}, messageId={}", 
                    userId, message.getMessageId());
            return false;
        }
        
        boolean delivered = false;
        String payload;
        
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("消息序列化失败", e);
            return false;
        }
        
        for (WebSocketSession session : sessions) {
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(payload));
                    delivered = true;
                } catch (IOException e) {
                    log.error("推送消息失败: userId={}, sessionId={}", 
                            userId, session.getId(), e);
                }
            }
        }
        
        return delivered;
    }

    /**
     * 推送消息给指定用户的特定设备
     */
    public boolean pushMessageToDevice(Long userId, String deviceId, WebSocketMessage message) {
        WebSocketSession session = sessionManager.getDeviceSession(userId, deviceId);
        
        if (session == null || !session.isOpen()) {
            return false;
        }
        
        try {
            String payload = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(payload));
            return true;
        } catch (IOException e) {
            log.error("推送消息到设备失败: userId={}, deviceId={}", userId, deviceId, e);
            return false;
        }
    }

    /**
     * 广播消息给群组
     * 
     * @param groupId       群组ID
     * @param message       消息内容
     * @param excludeUserId 排除的用户ID (可为null)
     */
    public void broadcastToGroup(Long groupId, WebSocketMessage message, Long excludeUserId) {
        // 获取群组成员 (这里应该调用 group-service 获取成员列表)
        // 简化实现：假设从消息数据中获取接收者列表
        @SuppressWarnings("unchecked")
        java.util.List<Long> memberIds = (java.util.List<Long>) message.getData().get("memberIds");
        
        if (memberIds == null || memberIds.isEmpty()) {
            log.warn("群组消息没有成员列表: groupId={}", groupId);
            return;
        }
        
        for (Long memberId : memberIds) {
            if (excludeUserId != null && excludeUserId.equals(memberId)) {
                continue;
            }
            pushMessageToUser(memberId, message);
        }
        
        log.debug("群组消息广播完成: groupId={}, memberCount={}", groupId, memberIds.size());
    }

    /**
     * 推送系统通知
     */
    public void pushSystemNotification(Long userId, String notificationType, Map<String, Object> data) {
        WebSocketMessage notification = WebSocketMessage.builder()
                .type("system_notification")
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "notificationType", notificationType,
                        "content", data,
                        "createTime", System.currentTimeMillis()
                ))
                .build();
        
        pushMessageToUser(userId, notification);
    }

    /**
     * 广播系统通知给所有在线用户
     */
    public void broadcastSystemNotification(String notificationType, Map<String, Object> data) {
        WebSocketMessage notification = WebSocketMessage.builder()
                .type("system_notification")
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "notificationType", notificationType,
                        "content", data,
                        "createTime", System.currentTimeMillis()
                ))
                .build();
        
        Set<Long> onlineUsers = sessionManager.getAllOnlineUsers();
        for (Long userId : onlineUsers) {
            pushMessageToUser(userId, notification);
        }
        
        log.debug("系统通知广播完成: notificationType={}, onlineUsers={}", 
                notificationType, onlineUsers.size());
    }

    // ==================== 响应消息发送 ====================

    private void sendConnectionEstablished(WebSocketSession session, Long userId) throws IOException {
        WebSocketMessage message = WebSocketMessage.builder()
                .type("connected")
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "userId", userId,
                        "serverTime", System.currentTimeMillis(),
                        "heartbeatInterval", WebSocketConfig.HEARTBEAT_SEND_INTERVAL
                ))
                .build();
        
        sendMessage(session, message);
    }

    private void sendAck(WebSocketSession session, String messageId) {
        try {
            WebSocketMessage ack = WebSocketMessage.builder()
                    .type("ack")
                    .messageId(messageId)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            sendMessage(session, ack);
        } catch (IOException e) {
            log.error("发送 ACK 失败", e);
        }
    }

    private void sendDeliveryReceipt(WebSocketSession session, String messageId, Long receiverId) {
        try {
            WebSocketMessage receipt = WebSocketMessage.builder()
                    .type("delivery_receipt")
                    .messageId(messageId)
                    .timestamp(System.currentTimeMillis())
                    .data(Map.of(
                            "messageId", messageId,
                            "receiverId", receiverId,
                            "deliveryTime", System.currentTimeMillis()
                    ))
                    .build();
            
            sendMessage(session, receipt);
        } catch (IOException e) {
            log.error("发送送达回执失败", e);
        }
    }

    private void sendError(WebSocketSession session, String errorCode, String errorMessage) {
        try {
            WebSocketMessage error = WebSocketMessage.builder()
                    .type("error")
                    .timestamp(System.currentTimeMillis())
                    .data(Map.of(
                            "code", errorCode,
                            "message", errorMessage
                    ))
                    .build();
            
            sendMessage(session, error);
        } catch (IOException e) {
            log.error("发送错误消息失败", e);
        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) throws IOException {
        if (session == null || !session.isOpen()) {
            return;
        }
        
        String payload = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(payload));
    }

    // ==================== 工具方法 ====================

    private Long getUserIdFromSession(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        if (userId instanceof Long) {
            return (Long) userId;
        }
        return null;
    }

    private String getDeviceIdFromSession(WebSocketSession session) {
        Object deviceId = session.getAttributes().get("deviceId");
        if (deviceId instanceof String) {
            return (String) deviceId;
        }
        return "unknown";
    }

    // ==================== 内部类 ====================

    /**
     * WebSocket 用户身份标识
     */
    public static class WebSocketPrincipal implements Principal {
        private final String name;

        public WebSocketPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
