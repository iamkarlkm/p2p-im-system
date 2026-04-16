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
import org.springframework.data.redis.core.SetOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 在线状态服务单元测试
 * 测试覆盖: 状态设置、查询、好友状态、最后在线时间等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("在线状态服务单元测试")
class OnlineStatusServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private OnlineStatusService onlineStatusService;

    private static final String USER_ID = "user_001";
    private static final String DEVICE_ID = "device_001";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    @DisplayName("设置用户在线状态 - 成功")
    void setUserOnline_Success() {
        // When
        onlineStatusService.setUserOnline(USER_ID, DEVICE_ID);

        // Then
        verify(valueOperations).set(
            eq("status:online:" + USER_ID + ":" + DEVICE_ID),
            eq("online"),
            anyLong(),
            any()
        );
        verify(setOperations).add("status:online:users", USER_ID);
    }

    @Test
    @DisplayName("设置用户离线状态 - 成功")
    void setUserOffline_Success() {
        // When
        onlineStatusService.setUserOffline(USER_ID, DEVICE_ID);

        // Then
        verify(redisTemplate).delete("status:online:" + USER_ID + ":" + DEVICE_ID);
        verify(valueOperations).set(
            eq("status:lastseen:" + USER_ID),
            anyString(),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("获取用户在线状态 - 在线")
    void getUserStatus_Online() {
        // Given
        when(redisTemplate.hasKey("status:online:" + USER_ID + ":" + DEVICE_ID)).thenReturn(true);

        // When
        var status = onlineStatusService.getUserStatus(USER_ID);

        // Then
        assertEquals("online", status.getStatus());
    }

    @Test
    @DisplayName("获取用户在线状态 - 离线")
    void getUserStatus_Offline() {
        // Given
        when(redisTemplate.hasKey("status:online:" + USER_ID + ":" + DEVICE_ID)).thenReturn(false);
        when(valueOperations.get("status:lastseen:" + USER_ID)).thenReturn("1700000000");

        // When
        var status = onlineStatusService.getUserStatus(USER_ID);

        // Then
        assertEquals("offline", status.getStatus());
    }

    @Test
    @DisplayName("检查用户是否在线 - 在线")
    void isUserOnline_True() {
        // Given
        when(redisTemplate.hasKey("status:online:" + USER_ID + ":" + DEVICE_ID)).thenReturn(true);

        // When
        boolean isOnline = onlineStatusService.isUserOnline(USER_ID, DEVICE_ID);

        // Then
        assertTrue(isOnline);
    }

    @Test
    @DisplayName("检查用户是否在线 - 离线")
    void isUserOnline_False() {
        // Given
        when(redisTemplate.hasKey("status:online:" + USER_ID + ":" + DEVICE_ID)).thenReturn(false);

        // When
        boolean isOnline = onlineStatusService.isUserOnline(USER_ID, DEVICE_ID);

        // Then
        assertFalse(isOnline);
    }

    @Test
    @DisplayName("获取用户任何设备的在线状态 - 在线")
    void isUserOnlineAnyDevice_True() {
        // Given
        when(setOperations.isMember("status:online:users", USER_ID)).thenReturn(true);

        // When
        boolean isOnline = onlineStatusService.isUserOnlineAnyDevice(USER_ID);

        // Then
        assertTrue(isOnline);
    }

    @Test
    @DisplayName("获取好友在线状态列表")
    void getFriendsStatus() {
        // Given
        List<String> friendIds = Arrays.asList("user_002", "user_003");
        when(setOperations.isMember("status:online:users", "user_002")).thenReturn(true);
        when(setOperations.isMember("status:online:users", "user_003")).thenReturn(false);
        when(valueOperations.get("status:lastseen:user_003")).thenReturn("1700000000");

        // When
        var statuses = onlineStatusService.getFriendsStatus(USER_ID, friendIds);

        // Then
        assertEquals(2, statuses.size());
    }

    @Test
    @DisplayName("更新最后在线时间 - 成功")
    void updateLastSeen_Success() {
        // When
        onlineStatusService.updateLastSeen(USER_ID);

        // Then
        verify(valueOperations).set(
            eq("status:lastseen:" + USER_ID),
            anyString(),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("获取最后在线时间 - 成功")
    void getLastSeen_Success() {
        // Given
        long timestamp = System.currentTimeMillis() / 1000;
        when(valueOperations.get("status:lastseen:" + USER_ID)).thenReturn(String.valueOf(timestamp));

        // When
        LocalDateTime lastSeen = onlineStatusService.getLastSeen(USER_ID);

        // Then
        assertNotNull(lastSeen);
    }

    @Test
    @DisplayName("获取最后在线时间 - 从未在线")
    void getLastSeen_Never() {
        // Given
        when(valueOperations.get("status:lastseen:" + USER_ID)).thenReturn(null);

        // When
        LocalDateTime lastSeen = onlineStatusService.getLastSeen(USER_ID);

        // Then
        assertNull(lastSeen);
    }

    @Test
    @DisplayName("心跳检测 - 更新TTL")
    void heartbeat_UpdateTTL() {
        // When
        onlineStatusService.heartbeat(USER_ID, DEVICE_ID);

        // Then
        verify(valueOperations).set(
            eq("status:online:" + USER_ID + ":" + DEVICE_ID),
            eq("online"),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("批量获取用户在线状态")
    void getUsersStatusBatch() {
        // Given
        List<String> userIds = Arrays.asList("user_001", "user_002", "user_003");
        when(setOperations.isMember("status:online:users", "user_001")).thenReturn(true);
        when(setOperations.isMember("status:online:users", "user_002")).thenReturn(false);
        when(setOperations.isMember("status:online:users", "user_003")).thenReturn(true);

        // When
        var statuses = onlineStatusService.getUsersStatusBatch(userIds);

        // Then
        assertEquals(3, statuses.size());
    }

    @Test
    @DisplayName("获取在线用户数量")
    void getOnlineUserCount() {
        // Given
        when(setOperations.size("status:online:users")).thenReturn(10L);

        // When
        long count = onlineStatusService.getOnlineUserCount();

        // Then
        assertEquals(10L, count);
    }

    @Test
    @DisplayName("获取用户的在线设备列表")
    void getUserOnlineDevices() {
        // Given
        Set<String> keys = Set.of(
            "status:online:user_001:device_001",
            "status:online:user_001:device_002"
        );
        when(redisTemplate.keys("status:online:" + USER_ID + ":*")).thenReturn(keys);

        // When
        List<String> devices = onlineStatusService.getUserOnlineDevices(USER_ID);

        // Then
        assertEquals(2, devices.size());
        assertTrue(devices.contains("device_001"));
        assertTrue(devices.contains("device_002"));
    }

    @Test
    @DisplayName("清理过期状态 - 成功")
    void cleanupExpiredStatus() {
        // Given
        Set<String> allOnlineKeys = Set.of(
            "status:online:user_001:device_001",
            "status:online:user_002:device_001"
        );
        when(redisTemplate.keys("status:online:*")).thenReturn(allOnlineKeys);
        when(redisTemplate.hasKey("status:online:user_001:device_001")).thenReturn(false);
        when(redisTemplate.hasKey("status:online:user_002:device_001")).thenReturn(true);

        // When
        int cleaned = onlineStatusService.cleanupExpiredStatus();

        // Then
        assertEquals(1, cleaned);
        verify(setOperations).remove("status:online:users", "user_001");
    }

    @Test
    @DisplayName("设置用户自定义状态")
    void setCustomStatus_Success() {
        // Given
        when(setOperations.isMember("status:online:users", USER_ID)).thenReturn(true);

        // When
        onlineStatusService.setCustomStatus(USER_ID, "busy", "In a meeting");

        // Then
        verify(valueOperations).set(
            eq("status:custom:" + USER_ID),
            anyString(),
            anyLong(),
            any()
        );
    }

    @Test
    @DisplayName("获取用户自定义状态")
    void getCustomStatus_Success() {
        // Given
        String customStatusJson = "{\"status\":\"busy\",\"message\":\"In a meeting\"}";
        when(valueOperations.get("status:custom:" + USER_ID)).thenReturn(customStatusJson);

        // When
        var status = onlineStatusService.getCustomStatus(USER_ID);

        // Then
        assertNotNull(status);
        assertEquals("busy", status.getStatus());
    }

    @Test
    @DisplayName("清除用户自定义状态")
    void clearCustomStatus_Success() {
        // When
        onlineStatusService.clearCustomStatus(USER_ID);

        // Then
        verify(redisTemplate).delete("status:custom:" + USER_ID);
    }

    @Test
    @DisplayName("检查用户是否隐身 - 是")
    void isUserInvisible_True() {
        // Given
        when(valueOperations.get("status:invisible:" + USER_ID)).thenReturn("true");

        // When
        boolean isInvisible = onlineStatusService.isUserInvisible(USER_ID);

        // Then
        assertTrue(isInvisible);
    }

    @Test
    @DisplayName("设置隐身状态 - 成功")
    void setInvisible_Success() {
        // When
        onlineStatusService.setInvisible(USER_ID, true);

        // Then
        verify(valueOperations).set(
            eq("status:invisible:" + USER_ID),
            eq("true"),
            anyLong(),
            any()
        );
    }
}
