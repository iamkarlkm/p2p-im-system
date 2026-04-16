package com.im.service.websocket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OnlineStatusService 单元测试
 * 
 * 测试覆盖:
 * - 设置用户在线
 * - 设置用户离线
 * - 获取用户状态
 * - 获取好友状态
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("在线状态服务单元测试")
class OnlineStatusServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private OnlineStatusService onlineStatusService;

    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_DEVICE_ID = "device_001";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    // ========== 在线状态设置测试 ==========

    @Test
    @DisplayName("设置用户在线成功")
    void setUserOnline() {
        // Prepare
        when(hashOperations.putAll(anyString(), anyMap())).thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(true);

        // Act
        onlineStatusService.userOnline(TEST_USER_ID, TEST_DEVICE_ID);

        // Assert
        verify(hashOperations, times(1)).putAll(anyString(), anyMap());
        verify(redisTemplate, times(1)).expire(anyString(), any(Duration.class));
        verify(valueOperations, times(1)).set(anyString(), any(), any(Duration.class));
    }

    @Test
    @DisplayName("设置用户在线-参数为空")
    void setUserOnline_NullParams() {
        // Act
        onlineStatusService.userOnline(null, TEST_DEVICE_ID);
        onlineStatusService.userOnline(TEST_USER_ID, null);

        // Assert - 不应抛出异常，也不应调用Redis
        verify(hashOperations, never()).putAll(anyString(), anyMap());
    }

    @Test
    @DisplayName("设置用户离线成功")
    void setUserOffline() {
        // Prepare - 先设置在线
        when(hashOperations.putAll(anyString(), anyMap())).thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(true);
        onlineStatusService.userOnline(TEST_USER_ID, TEST_DEVICE_ID);

        // Prepare - 离线操作
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(true);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act
        onlineStatusService.userOffline(TEST_USER_ID, TEST_DEVICE_ID);

        // Assert
        verify(valueOperations, times(2)).set(anyString(), any(), any(Duration.class));
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    @DisplayName("设置用户离线-还有其他设备在线")
    void setUserOffline_OtherDevicesOnline() {
        // Prepare - 先设置两个设备在线
        when(hashOperations.putAll(anyString(), anyMap())).thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(true);
        onlineStatusService.userOnline(TEST_USER_ID, TEST_DEVICE_ID);
        onlineStatusService.userOnline(TEST_USER_ID, "device_002");

        // Act - 一个设备离线
        onlineStatusService.userOffline(TEST_USER_ID, TEST_DEVICE_ID);

        // Assert - 不应删除Redis中的在线状态
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("更新心跳时间")
    void updateHeartbeat() {
        // Prepare
        when(hashOperations.put(anyString(), anyString(), any())).thenReturn(true);

        // Act
        onlineStatusService.updateHeartbeat(TEST_USER_ID, TEST_DEVICE_ID);

        // Assert
        verify(hashOperations, times(1)).put(anyString(), eq("lastHeartbeat"), any());
    }

    @Test
    @DisplayName("更新在线状态")
    void updatePresence() {
        // Prepare - 先设置在线
        when(hashOperations.putAll(anyString(), anyMap())).thenReturn(true);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);
        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(true);
        onlineStatusService.userOnline(TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        onlineStatusService.updatePresence(TEST_USER_ID, "busy");

        // Assert
        verify(hashOperations, times(2)).putAll(anyString(), anyMap());
    }

    // ========== 状态查询测试 ==========

    @Test
    @DisplayName("检查用户是否在线-在线")
    void isUserOnline_True() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // Act
        boolean result = onlineStatusService.isUserOnline(TEST_USER_ID);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate, times(1)).hasKey(anyString());
    }

    @Test
    @DisplayName("检查用户是否在线-离线")
    void isUserOnline_False() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Act
        boolean result = onlineStatusService.isUserOnline(TEST_USER_ID);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("获取用户在线状态")
    void getUserStatus() {
        // Prepare
        Map<Object, Object> entries = new HashMap<>();
        entries.put("userId", TEST_USER_ID.toString());
        entries.put("status", "online");
        entries.put("lastHeartbeat", System.currentTimeMillis());
        entries.put("onlineTime", System.currentTimeMillis());

        when(hashOperations.entries(anyString())).thenReturn(entries);
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // Act
        OnlineStatusService.UserOnlineStatus status = onlineStatusService.getUserOnlineStatus(TEST_USER_ID);

        // Assert
        assertThat(status).isNotNull();
        assertThat(status.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(status.isOnline()).isTrue();
        assertThat(status.getStatus()).isEqualTo("online");
    }

    @Test
    @DisplayName("获取用户在线状态-离线状态")
    void getUserStatus_Offline() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(valueOperations.get(anyString())).thenReturn(System.currentTimeMillis() - 3600000);

        // Act
        OnlineStatusService.UserOnlineStatus status = onlineStatusService.getUserOnlineStatus(TEST_USER_ID);

        // Assert
        assertThat(status).isNotNull();
        assertThat(status.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(status.isOnline()).isFalse();
        assertThat(status.getStatus()).isEqualTo("offline");
    }

    @Test
    @DisplayName("获取最后在线时间")
    void getLastSeenTime() {
        // Prepare
        long timestamp = System.currentTimeMillis();
        when(valueOperations.get(anyString())).thenReturn(timestamp);

        // Act
        LocalDateTime result = onlineStatusService.getLastSeenTime(TEST_USER_ID);

        // Assert
        assertThat(result).isNotNull();
        verify(valueOperations, times(1)).get(anyString());
    }

    @Test
    @DisplayName("获取所有在线用户ID")
    void getAllOnlineUsers() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        onlineStatusService.userOnline(1L, "device1");
        onlineStatusService.userOnline(2L, "device1");

        // Act
        Set<Long> onlineUsers = onlineStatusService.getAllOnlineUsers();

        // Assert
        assertThat(onlineUsers).contains(1L, 2L);
    }

    @Test
    @DisplayName("获取在线用户数量")
    void getOnlineUserCount() {
        // Prepare
        onlineStatusService.userOnline(1L, "device1");
        onlineStatusService.userOnline(2L, "device1");
        onlineStatusService.userOnline(3L, "device1");

        // Act
        long count = onlineStatusService.getOnlineUserCount();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    // ========== 好友状态测试 ==========

    @Test
    @DisplayName("获取好友在线状态")
    void getFriendsStatus() {
        // Prepare
        List<Long> friendIds = List.of(2L, 3L, 4L);
        
        Map<Object, Object> entries = new HashMap<>();
        entries.put("userId", "2");
        entries.put("status", "online");
        entries.put("lastHeartbeat", System.currentTimeMillis());

        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        when(hashOperations.entries(anyString())).thenReturn(entries);

        // Act
        Map<Long, OnlineStatusService.UserOnlineStatus> result = 
                onlineStatusService.getFriendsOnlineStatus(TEST_USER_ID, friendIds);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).containsKey(2L);
    }

    @Test
    @DisplayName("批量获取用户在线状态")
    void getBatchOnlineStatus() {
        // Prepare
        List<Long> userIds = List.of(1L, 2L, 3L);
        
        Map<Object, Object> entries = new HashMap<>();
        entries.put("userId", "1");
        entries.put("status", "online");
        entries.put("lastHeartbeat", System.currentTimeMillis());

        when(redisTemplate.hasKey(anyString())).thenReturn(true);
        when(hashOperations.entries(anyString())).thenReturn(entries);

        // Act
        List<OnlineStatusService.UserOnlineStatus> result = 
                onlineStatusService.getBatchOnlineStatus(userIds);

        // Assert
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("订阅好友在线状态")
    void subscribeFriendStatus() {
        // Prepare
        when(setOperations.add(anyString(), any())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);

        // Act
        onlineStatusService.subscribeFriendStatus(1L, 2L);

        // Assert
        verify(setOperations, times(1)).add(anyString(), eq("2"));
    }

    @Test
    @DisplayName("取消订阅好友在线状态")
    void unsubscribeFriendStatus() {
        // Prepare
        when(setOperations.remove(anyString(), any())).thenReturn(1L);

        // Act
        onlineStatusService.unsubscribeFriendStatus(1L, 2L);

        // Assert
        verify(setOperations, times(1)).remove(anyString(), eq("2"));
    }
}
