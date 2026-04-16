package com.im.websocket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * WebSocket会话管理器单元测试
 * 测试覆盖: 会话注册、注销、查询、心跳等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket会话管理器单元测试")
class WebSocketSessionManagerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private WebSocketSession session1;

    @Mock
    private WebSocketSession session2;

    @InjectMocks
    private WebSocketSessionManager sessionManager;

    private static final String USER_ID = "user_001";
    private static final String DEVICE_ID_1 = "device_001";
    private static final String DEVICE_ID_2 = "device_002";
    private static final String SESSION_ID_1 = "session_001";
    private static final String SESSION_ID_2 = "session_002";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(session1.getId()).thenReturn(SESSION_ID_1);
        when(session2.getId()).thenReturn(SESSION_ID_2);
    }

    @Test
    @DisplayName("注册会话 - 成功")
    void registerSession_Success() {
        // When
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // Then
        WebSocketSession registered = sessionManager.getSessionByUserId(USER_ID, DEVICE_ID_1);
        assertNotNull(registered);
        assertEquals(SESSION_ID_1, registered.getId());
        
        // Redis应该存储在线状态
        verify(valueOperations).set(
            eq("online:" + USER_ID + ":" + DEVICE_ID_1),
            eq("online"),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("同一用户多设备注册 - 成功")
    void registerSession_MultiDevice_Success() {
        // When
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession(USER_ID, DEVICE_ID_2, session2);

        // Then
        Collection<WebSocketSession> sessions = sessionManager.getSessionsByUserId(USER_ID);
        assertEquals(2, sessions.size());
    }

    @Test
    @DisplayName("注销会话 - 成功")
    void unregisterSession_Success() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        sessionManager.unregisterSession(USER_ID, DEVICE_ID_1, SESSION_ID_1);

        // Then
        WebSocketSession registered = sessionManager.getSessionByUserId(USER_ID, DEVICE_ID_1);
        assertNull(registered);
        
        verify(redisTemplate).delete("online:" + USER_ID + ":" + DEVICE_ID_1);
    }

    @Test
    @DisplayName("按用户ID获取会话 - 成功")
    void getSessionByUserId_Success() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        WebSocketSession result = sessionManager.getSessionByUserId(USER_ID, DEVICE_ID_1);

        // Then
        assertNotNull(result);
        assertEquals(SESSION_ID_1, result.getId());
    }

    @Test
    @DisplayName("按用户ID获取所有会话 - 成功")
    void getSessionsByUserId_Success() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession(USER_ID, DEVICE_ID_2, session2);

        // When
        Collection<WebSocketSession> sessions = sessionManager.getSessionsByUserId(USER_ID);

        // Then
        assertEquals(2, sessions.size());
    }

    @Test
    @DisplayName("检查用户是否在线 - 在线")
    void isUserOnline_True() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        boolean isOnline = sessionManager.isUserOnline(USER_ID);

        // Then
        assertTrue(isOnline);
    }

    @Test
    @DisplayName("检查用户是否在线 - 不在线")
    void isUserOnline_False() {
        // When
        boolean isOnline = sessionManager.isUserOnline("nonexistent_user");

        // Then
        assertFalse(isOnline);
    }

    @Test
    @DisplayName("获取在线用户数量")
    void getOnlineUserCount() {
        // Given
        sessionManager.registerSession("user_001", DEVICE_ID_1, session1);
        sessionManager.registerSession("user_002", DEVICE_ID_1, session2);

        // When
        int count = sessionManager.getOnlineUserCount();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("获取用户设备数量")
    void getUserDeviceCount() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession(USER_ID, DEVICE_ID_2, session2);

        // When
        int count = sessionManager.getUserDeviceCount(USER_ID);

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("心跳更新 - 成功")
    void heartbeat_Update() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        sessionManager.updateHeartbeat(USER_ID, DEVICE_ID_1);

        // Then - Redis TTL应该被刷新
        verify(valueOperations, atLeastOnce()).set(
            eq("online:" + USER_ID + ":" + DEVICE_ID_1),
            eq("online"),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("检查会话是否过期 - 未过期")
    void isSessionExpired_NotExpired() {
        // Given - 刚注册的会话
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        boolean isExpired = sessionManager.isSessionExpired(USER_ID, DEVICE_ID_1);

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("向用户发送消息 - 单设备")
    void sendMessageToUser_SingleDevice() throws IOException {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        String message = "{\"type\":\"test\"}";

        // When
        boolean sent = sessionManager.sendMessageToUser(USER_ID, message);

        // Then
        assertTrue(sent);
        verify(session1).sendMessage(any());
    }

    @Test
    @DisplayName("向用户发送消息 - 多设备")
    void sendMessageToUser_MultiDevice() throws IOException {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession(USER_ID, DEVICE_ID_2, session2);
        String message = "{\"type\":\"test\"}";

        // When
        boolean sent = sessionManager.sendMessageToUser(USER_ID, message);

        // Then
        assertTrue(sent);
        verify(session1).sendMessage(any());
        verify(session2).sendMessage(any());
    }

    @Test
    @DisplayName("向用户特定设备发送消息 - 成功")
    void sendMessageToDevice_Success() throws IOException {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession(USER_ID, DEVICE_ID_2, session2);
        String message = "{\"type\":\"test\"}";

        // When
        boolean sent = sessionManager.sendMessageToDevice(USER_ID, DEVICE_ID_1, message);

        // Then
        assertTrue(sent);
        verify(session1).sendMessage(any());
        verify(session2, never()).sendMessage(any());
    }

    @Test
    @DisplayName("广播消息给所有在线用户")
    void broadcastMessage() throws IOException {
        // Given
        sessionManager.registerSession("user_001", DEVICE_ID_1, session1);
        sessionManager.registerSession("user_002", DEVICE_ID_1, session2);
        String message = "{\"type\":\"broadcast\"}";

        // When
        int count = sessionManager.broadcastMessage(message);

        // Then
        assertEquals(2, count);
        verify(session1).sendMessage(any());
        verify(session2).sendMessage(any());
    }

    @Test
    @DisplayName("清理过期会话")
    void cleanupExpiredSessions() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        int cleaned = sessionManager.cleanupExpiredSessions();

        // Then - 刚注册的会话不应该被清理
        assertEquals(0, cleaned);
    }

    @Test
    @DisplayName("获取会话统计信息")
    void getSessionStatistics() {
        // Given
        sessionManager.registerSession("user_001", DEVICE_ID_1, session1);
        sessionManager.registerSession("user_002", DEVICE_ID_1, session2);

        // When
        var stats = sessionManager.getSessionStatistics();

        // Then
        assertEquals(2, stats.getTotalOnlineUsers());
        assertEquals(2, stats.getTotalSessions());
    }

    @Test
    @DisplayName("关闭所有会话")
    void closeAllSessions() throws IOException {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);
        sessionManager.registerSession("user_002", DEVICE_ID_1, session2);

        // When
        sessionManager.closeAllSessions();

        // Then
        verify(session1).close();
        verify(session2).close();
    }

    @Test
    @DisplayName("检查会话是否存在 - 存在")
    void hasSession_True() {
        // Given
        sessionManager.registerSession(USER_ID, DEVICE_ID_1, session1);

        // When
        boolean hasSession = sessionManager.hasSession(USER_ID, DEVICE_ID_1);

        // Then
        assertTrue(hasSession);
    }

    @Test
    @DisplayName("获取所有在线用户ID")
    void getAllOnlineUserIds() {
        // Given
        sessionManager.registerSession("user_001", DEVICE_ID_1, session1);
        sessionManager.registerSession("user_002", DEVICE_ID_1, session2);

        // When
        Set<String> userIds = sessionManager.getAllOnlineUserIds();

        // Then
        assertEquals(2, userIds.size());
        assertTrue(userIds.contains("user_001"));
        assertTrue(userIds.contains("user_002"));
    }
}
