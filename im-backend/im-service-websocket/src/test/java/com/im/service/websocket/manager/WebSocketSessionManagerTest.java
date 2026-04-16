package com.im.service.websocket.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * WebSocketSessionManager 单元测试
 * 
 * 测试覆盖:
 * - 会话注册
 * - 会话注销
 * - 根据用户ID获取会话
 * - 检查用户在线状态
 * - 心跳更新
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WebSocket会话管理器单元测试")
class WebSocketSessionManagerTest {

    @InjectMocks
    private WebSocketSessionManager sessionManager;

    @Mock
    private WebSocketSession mockSession;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_DEVICE_ID = "device_001";
    private static final String TEST_SESSION_ID = "session_abc123";

    @BeforeEach
    void setUp() {
        when(mockSession.getId()).thenReturn(TEST_SESSION_ID);
        when(mockSession.isOpen()).thenReturn(true);
    }

    // ========== 会话注册测试 ==========

    @Test
    @DisplayName("注册会话成功")
    void registerSession() {
        // Act
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Assert
        Set<WebSocketSession> sessions = sessionManager.getUserSessions(TEST_USER_ID);
        assertThat(sessions).hasSize(1);
        assertThat(sessionManager.getUserSessionCount(TEST_USER_ID)).isEqualTo(1);
        assertThat(sessionManager.hasActiveSession(TEST_USER_ID)).isTrue();
    }

    @Test
    @DisplayName("注册会话-参数为空不注册")
    void registerSession_NullParams() {
        // Act
        sessionManager.registerSession(null, TEST_DEVICE_ID, mockSession);
        sessionManager.registerSession(TEST_USER_ID, null, mockSession);
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, null);

        // Assert
        assertThat(sessionManager.getOnlineUserCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("同一设备重复登录-关闭旧会话")
    void registerSession_SameDevice() throws Exception {
        // Prepare
        WebSocketSession oldSession = mock(WebSocketSession.class);
        when(oldSession.getId()).thenReturn("old_session_id");

        // Act - 先注册旧会话
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, oldSession);
        // 再注册新会话
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Assert
        verify(oldSession, times(1)).close();
        Set<WebSocketSession> sessions = sessionManager.getUserSessions(TEST_USER_ID);
        assertThat(sessions).hasSize(1);
    }

    // ========== 会话注销测试 ==========

    @Test
    @DisplayName("注销会话成功")
    void unregisterSession() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        sessionManager.unregisterSession(TEST_USER_ID, TEST_DEVICE_ID, TEST_SESSION_ID);

        // Assert
        assertThat(sessionManager.getUserSessionCount(TEST_USER_ID)).isEqualTo(0);
        assertThat(sessionManager.hasActiveSession(TEST_USER_ID)).isFalse();
    }

    @Test
    @DisplayName("注销会话-参数为空")
    void unregisterSession_NullParams() {
        // Act
        sessionManager.unregisterSession(null, TEST_DEVICE_ID, TEST_SESSION_ID);

        // Assert - 不应抛出异常
        assertThat(sessionManager.getOnlineUserCount()).isEqualTo(0);
    }

    // ========== 会话查询测试 ==========

    @Test
    @DisplayName("获取用户特定设备会话")
    void getSessionByUserId() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        WebSocketSession result = sessionManager.getDeviceSession(TEST_USER_ID, TEST_DEVICE_ID);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_SESSION_ID);
    }

    @Test
    @DisplayName("获取会话信息")
    void getSessionInfo() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        WebSocketSessionManager.UserSessionInfo info = sessionManager.getSessionInfo(TEST_SESSION_ID);

        // Assert
        assertThat(info).isNotNull();
        assertThat(info.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(info.getDeviceId()).isEqualTo(TEST_DEVICE_ID);
        assertThat(info.getSessionId()).isEqualTo(TEST_SESSION_ID);
        assertThat(info.isOnline()).isTrue();
    }

    // ========== 在线状态测试 ==========

    @Test
    @DisplayName("检查用户在线-在线")
    void isUserOnline_True() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        boolean online = sessionManager.hasActiveSession(TEST_USER_ID);

        // Assert
        assertThat(online).isTrue();
    }

    @Test
    @DisplayName("检查用户在线-离线")
    void isUserOnline_False() {
        // Act
        boolean online = sessionManager.hasActiveSession(TEST_USER_ID);

        // Assert
        assertThat(online).isFalse();
    }

    // ========== 心跳更新测试 ==========

    @Test
    @DisplayName("更新最后活动时间")
    void heartbeat_Update() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);
        long oldTime = sessionManager.getSessionInfo(TEST_SESSION_ID).getLastActivityTime();

        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        sessionManager.updateLastActivityTime(TEST_USER_ID, TEST_SESSION_ID);

        // Assert
        long newTime = sessionManager.getSessionInfo(TEST_SESSION_ID).getLastActivityTime();
        assertThat(newTime).isGreaterThan(oldTime);
    }

    @Test
    @DisplayName("检查会话是否有效-有效")
    void isSessionValid_True() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        boolean valid = sessionManager.isSessionValid(TEST_SESSION_ID);

        // Assert
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("检查会话是否有效-无效")
    void isSessionValid_False() {
        // Act
        boolean valid = sessionManager.isSessionValid("non_existent_session");

        // Assert
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("获取在线用户数量")
    void getOnlineUserCount() {
        // Prepare
        sessionManager.registerSession(1L, "device1", mockSession);
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("session2");
        sessionManager.registerSession(2L, "device1", session2);

        // Act
        int count = sessionManager.getOnlineUserCount();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("获取总会话数量")
    void getTotalSessionCount() {
        // Prepare
        sessionManager.registerSession(1L, "device1", mockSession);
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("session2");
        sessionManager.registerSession(1L, "device2", session2);

        // Act
        int count = sessionManager.getTotalSessionCount();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("检查心跳超时-未超时")
    void isHeartbeatTimeout_False() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        boolean timeout = sessionManager.isHeartbeatTimeout(TEST_SESSION_ID);

        // Assert - 刚注册，不应超时
        assertThat(timeout).isFalse();
    }

    @Test
    @DisplayName("获取超时会话列表")
    void getTimeoutSessions() {
        // Prepare - 注册一个会话，但不更新活动时间
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        var timeoutSessions = sessionManager.getTimeoutSessions();

        // Assert - 刚注册，不应有超时会话
        assertThat(timeoutSessions).isEmpty();
    }

    @Test
    @DisplayName("获取会话统计信息")
    void getSessionStats() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);

        // Act
        WebSocketSessionManager.SessionStats stats = sessionManager.getSessionStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getOnlineUsers()).isEqualTo(1);
        assertThat(stats.getTotalSessions()).isEqualTo(1);
        assertThat(stats.getDeviceTypeDistribution()).isNotEmpty();
    }

    @Test
    @DisplayName("断线重连检查-可以重连")
    void canReconnect_True() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);
        sessionManager.unregisterSession(TEST_USER_ID, TEST_DEVICE_ID, TEST_SESSION_ID);

        // Act
        boolean canReconnect = sessionManager.canReconnect(TEST_SESSION_ID);

        // Assert - 刚断开，应该可以重连
        assertThat(canReconnect).isTrue();
    }

    @Test
    @DisplayName("获取断线会话信息")
    void getCachedSession() {
        // Prepare
        sessionManager.registerSession(TEST_USER_ID, TEST_DEVICE_ID, mockSession);
        sessionManager.unregisterSession(TEST_USER_ID, TEST_DEVICE_ID, TEST_SESSION_ID);

        // Act
        WebSocketSessionManager.CachedSessionInfo cached = sessionManager.getCachedSession(TEST_SESSION_ID);

        // Assert
        assertThat(cached).isNotNull();
        assertThat(cached.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(cached.getDeviceId()).isEqualTo(TEST_DEVICE_ID);
    }
}
