package com.im.service.websocket.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.service.websocket.model.WebSocketMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * WebSocket 连接认证拦截器
 * 
 * 功能:
 * 1. WebSocket 握手阶段认证 (Token 验证)
 * 2. STOMP 消息阶段认证
 * 3. Token 解析与用户信息提取
 * 4. 权限校验
 * 
 * 认证流程:
 * 1. 握手阶段: 从 URL 参数或 Header 中提取 Token，验证后存储到 session attributes
 * 2. 消息阶段: 从 STOMP headers 中提取 Token，验证用户身份
 * 
 * Token 传递方式:
 * - URL 参数: ws://host/ws?token=xxx&deviceId=xxx
 * - Header: Authorization: Bearer xxx
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor, ChannelInterceptor {

    private final ObjectMapper objectMapper;

    // ==================== 配置常量 ====================
    
    /** JWT Secret Key (应该从配置文件读取) */
    private static final String JWT_SECRET = "im-modular-websocket-jwt-secret-key-2024-secure";
    
    /** Token URL 参数名 */
    private static final String TOKEN_PARAM = "token";
    
    /** 设备ID URL 参数名 */
    private static final String DEVICE_ID_PARAM = "deviceId";
    
    /** Authorization Header */
    private static final String AUTH_HEADER = "Authorization";
    
    /** Bearer 前缀 */
    private static final String BEARER_PREFIX = "Bearer ";
    
    /** Session 属性: 用户ID */
    public static final String SESSION_ATTR_USER_ID = "userId";
    
    /** Session 属性: 设备ID */
    public static final String SESSION_ATTR_DEVICE_ID = "deviceId";
    
    /** Session 属性: Token */
    public static final String SESSION_ATTR_TOKEN = "token";

    // ==================== 握手阶段拦截 ====================

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        log.debug("WebSocket 握手请求: uri={}", request.getURI());
        
        // 1. 提取 Token
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            log.warn("WebSocket 握手失败: 缺少 Token");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        
        // 2. 验证 Token
        Claims claims = validateToken(token);
        
        if (claims == null) {
            log.warn("WebSocket 握手失败: Token 无效");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        
        // 3. 提取用户ID
        Long userId = extractUserId(claims);
        
        if (userId == null) {
            log.warn("WebSocket 握手失败: 无法提取用户ID");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        
        // 4. 提取设备ID
        String deviceId = extractDeviceId(request);
        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = generateDeviceId(request);
        }
        
        // 5. 存储到 session attributes
        attributes.put(SESSION_ATTR_USER_ID, userId);
        attributes.put(SESSION_ATTR_DEVICE_ID, deviceId);
        attributes.put(SESSION_ATTR_TOKEN, token);
        
        log.info("WebSocket 握手成功: userId={}, deviceId={}", userId, deviceId);
        
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket 握手后处理异常", exception);
        }
    }

    // ==================== STOMP 消息阶段拦截 ====================

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor == null) {
            return message;
        }
        
        StompCommand command = accessor.getCommand();
        
        if (command == null) {
            return message;
        }
        
        switch (command) {
            case CONNECT:
                // 处理 STOMP CONNECT 帧认证
                handleStompConnect(accessor);
                break;
            case SUBSCRIBE:
                // 处理订阅权限校验
                handleSubscribe(accessor);
                break;
            case SEND:
                // 处理发送消息权限校验
                handleSend(accessor);
                break;
            case DISCONNECT:
                // 处理断开连接
                handleDisconnect(accessor);
                break;
            default:
                break;
        }
        
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        // 发送后处理
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (ex != null) {
            log.error("消息发送完成处理异常", ex);
        }
    }

    // ==================== STOMP 命令处理 ====================

    /**
     * 处理 STOMP CONNECT 认证
     */
    private void handleStompConnect(StompHeaderAccessor accessor) {
        // 从 headers 中提取 token
        String token = extractTokenFromHeaders(accessor);
        
        if (token == null) {
            log.warn("STOMP CONNECT 缺少 Token");
            throw new IllegalArgumentException("Missing authentication token");
        }
        
        // 验证 token
        Claims claims = validateToken(token);
        
        if (claims == null) {
            log.warn("STOMP CONNECT Token 无效");
            throw new IllegalArgumentException("Invalid authentication token");
        }
        
        Long userId = extractUserId(claims);
        String deviceId = extractDeviceIdFromHeaders(accessor);
        
        // 设置用户到 accessor
        accessor.setUser(new WebSocketUser(userId));
        
        // 存储到 session attributes
        accessor.setSessionAttribute(SESSION_ATTR_USER_ID, userId);
        accessor.setSessionAttribute(SESSION_ATTR_DEVICE_ID, deviceId);
        accessor.setSessionAttribute(SESSION_ATTR_TOKEN, token);
        
        log.debug("STOMP CONNECT 认证成功: userId={}", userId);
    }

    /**
     * 处理订阅权限校验
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        
        if (destination == null) {
            return;
        }
        
        // 获取当前用户
        WebSocketUser user = (WebSocketUser) accessor.getUser();
        if (user == null) {
            log.warn("订阅请求未认证: destination={}", destination);
            throw new IllegalArgumentException("Not authenticated");
        }
        
        // 校验订阅权限 (简化实现，实际应该根据业务逻辑校验)
        // 例如：用户只能订阅自己的消息队列
        if (destination.startsWith("/user/") && !destination.contains(user.getUserId().toString())) {
            log.warn("订阅权限不足: userId={}, destination={}", user.getUserId(), destination);
            throw new IllegalArgumentException("Subscription not allowed");
        }
        
        log.debug("订阅校验通过: userId={}, destination={}", user.getUserId(), destination);
    }

    /**
     * 处理发送消息权限校验
     */
    private void handleSend(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        
        // 获取当前用户
        WebSocketUser user = (WebSocketUser) accessor.getUser();
        if (user == null) {
            log.warn("发送消息请求未认证: destination={}", destination);
            throw new IllegalArgumentException("Not authenticated");
        }
        
        // 校验发送权限 (简化实现)
        log.debug("发送消息校验通过: userId={}, destination={}", user.getUserId(), destination);
    }

    /**
     * 处理断开连接
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        WebSocketUser user = (WebSocketUser) accessor.getUser();
        if (user != null) {
            log.debug("用户断开连接: userId={}", user.getUserId());
        }
    }

    // ==================== Token 提取与验证 ====================

    /**
     * 从请求中提取 Token
     */
    private String extractToken(ServerHttpRequest request) {
        // 1. 尝试从 URL 参数获取
        String query = request.getURI().getQuery();
        if (query != null) {
            String token = extractParamFromQuery(query, TOKEN_PARAM);
            if (token != null) {
                return token;
            }
        }
        
        // 2. 尝试从 Header 获取
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * 从 URL 查询字符串中提取参数
     */
    private String extractParamFromQuery(String query, String paramName) {
        if (query == null || paramName == null) {
            return null;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(paramName)) {
                return kv[1];
            }
        }
        
        return null;
    }

    /**
     * 从 STOMP headers 中提取 Token
     */
    private String extractTokenFromHeaders(StompHeaderAccessor accessor) {
        // 1. 尝试从原生 headers 获取
        List<String> authHeaders = accessor.getNativeHeader(AUTH_HEADER);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith(BEARER_PREFIX)) {
                return authHeader.substring(BEARER_PREFIX.length());
            }
            return authHeader;
        }
        
        // 2. 尝试从 session attributes 获取 (握手阶段已存储)
        Object token = accessor.getSessionAttribute(SESSION_ATTR_TOKEN);
        if (token != null) {
            return token.toString();
        }
        
        return null;
    }

    /**
     * 验证 JWT Token
     */
    private Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.debug("Token 验证失败: {}", e.getMessage());
            return null;
        }
    }

    // ==================== 用户信息提取 ====================

    /**
     * 从 Claims 中提取用户ID
     */
    private Long extractUserId(Claims claims) {
        Object userId = claims.get("userId");
        if (userId == null) {
            userId = claims.getSubject();
        }
        
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        
        if (userId instanceof String) {
            try {
                return Long.parseLong((String) userId);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * 从请求中提取设备ID
     */
    private String extractDeviceId(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        if (query != null) {
            return extractParamFromQuery(query, DEVICE_ID_PARAM);
        }
        return null;
    }

    /**
     * 从 STOMP headers 中提取设备ID
     */
    private String extractDeviceIdFromHeaders(StompHeaderAccessor accessor) {
        // 尝试从原生 headers 获取
        List<String> deviceIds = accessor.getNativeHeader("deviceId");
        if (deviceIds != null && !deviceIds.isEmpty()) {
            return deviceIds.get(0);
        }
        
        // 从 session attributes 获取
        Object deviceId = accessor.getSessionAttribute(SESSION_ATTR_DEVICE_ID);
        if (deviceId != null) {
            return deviceId.toString();
        }
        
        return generateDeviceId(null);
    }

    /**
     * 生成设备ID
     */
    private String generateDeviceId(ServerHttpRequest request) {
        StringBuilder sb = new StringBuilder("device_");
        sb.append(System.currentTimeMillis());
        sb.append("_");
        sb.append(new Random().nextInt(10000));
        
        if (request instanceof ServletServerHttpRequest) {
            String ip = ((ServletServerHttpRequest) request).getServletRequest().getRemoteAddr();
            sb.append("_").append(ip.hashCode() & 0xFFFF);
        }
        
        return sb.toString();
    }

    // ==================== 内部类 ====================

    /**
     * WebSocket 用户身份
     */
    public static class WebSocketUser implements java.security.Principal {
        private final Long userId;

        public WebSocketUser(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }

        @Override
        public String getName() {
            return userId != null ? userId.toString() : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WebSocketUser that = (WebSocketUser) o;
            return Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }
}
