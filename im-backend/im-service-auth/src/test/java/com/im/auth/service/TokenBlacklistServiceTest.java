package com.im.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Token黑名单服务单元测试
 * 测试覆盖: Token加入黑名单、检查、清理等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Token黑名单服务单元测试")
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TokenBlacklistService tokenBlacklistService;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("将Token加入黑名单 - 成功")
    void addToBlacklist_Success() {
        // Given
        String token = "test_token_123";
        long expirySeconds = 3600;

        // When
        tokenBlacklistService.addToBlacklist(token, expirySeconds);

        // Then
        verify(valueOperations).set(
            eq(BLACKLIST_PREFIX + token),
            eq("blacklisted"),
            eq(expirySeconds),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("检查Token是否在黑名单中 - 是")
    void isBlacklisted_True() {
        // Given
        String token = "blacklisted_token";
        when(valueOperations.get(BLACKLIST_PREFIX + token)).thenReturn("blacklisted");

        // When
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Then
        assertTrue(isBlacklisted);
    }

    @Test
    @DisplayName("检查Token是否在黑名单中 - 否")
    void isBlacklisted_False() {
        // Given
        String token = "valid_token";
        when(valueOperations.get(BLACKLIST_PREFIX + token)).thenReturn(null);

        // When
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Then
        assertFalse(isBlacklisted);
    }

    @Test
    @DisplayName("批量将Token加入黑名单 - 成功")
    void addToBlacklistBatch_Success() {
        // Given
        java.util.List<String> tokens = java.util.Arrays.asList("token1", "token2", "token3");
        long expirySeconds = 3600;

        // When
        tokenBlacklistService.addToBlacklistBatch(tokens, expirySeconds);

        // Then
        verify(valueOperations, times(3)).set(
            argThat(key -> key.startsWith(BLACKLIST_PREFIX)),
            eq("blacklisted"),
            eq(expirySeconds),
            eq(TimeUnit.SECONDS)
        );
    }

    @Test
    @DisplayName("从黑名单移除Token - 成功")
    void removeFromBlacklist_Success() {
        // Given
        String token = "token_to_remove";

        // When
        tokenBlacklistService.removeFromBlacklist(token);

        // Then
        verify(redisTemplate).delete(BLACKLIST_PREFIX + token);
    }

    @Test
    @DisplayName("获取黑名单中的Token数量")
    void getBlacklistCount() {
        // Given
        when(redisTemplate.keys(BLACKLIST_PREFIX + "*"))
            .thenReturn(java.util.Set.of(
                BLACKLIST_PREFIX + "token1",
                BLACKLIST_PREFIX + "token2",
                BLACKLIST_PREFIX + "token3"
            ));

        // When
        long count = tokenBlacklistService.getBlacklistCount();

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("清理过期黑名单条目")
    void cleanupExpiredTokens() {
        // Given - Redis会自动清理过期的key，这里只是验证方法调用

        // When
        tokenBlacklistService.cleanupExpiredTokens();

        // Then - 方法执行不抛出异常即可
        verify(redisTemplate, atMost(1)).keys(anyString());
    }

    @Test
    @DisplayName("检查Token是否有效 - 有效")
    void isTokenValid_True() {
        // Given
        String token = "valid_token";
        when(valueOperations.get(BLACKLIST_PREFIX + token)).thenReturn(null);

        // When
        boolean isValid = tokenBlacklistService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("检查Token是否有效 - 无效")
    void isTokenValid_False() {
        // Given
        String token = "invalid_token";
        when(valueOperations.get(BLACKLIST_PREFIX + token)).thenReturn("blacklisted");

        // When
        boolean isValid = tokenBlacklistService.isTokenValid(token);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("添加永久黑名单 - 成功")
    void addToBlacklistPermanent_Success() {
        // Given
        String token = "permanent_blacklist_token";

        // When
        tokenBlacklistService.addToBlacklistPermanent(token);

        // Then
        verify(valueOperations).set(
            eq(BLACKLIST_PREFIX + token),
            eq("blacklisted_permanent")
        );
        // 不设置过期时间
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("获取Token黑名单状态详情")
    void getBlacklistStatus() {
        // Given
        String token = "status_token";
        when(valueOperations.get(BLACKLIST_PREFIX + token)).thenReturn("blacklisted");

        // When
        String status = tokenBlacklistService.getBlacklistStatus(token);

        // Then
        assertEquals("blacklisted", status);
    }

    @Test
    @DisplayName("批量检查Token黑名单状态")
    void isBlacklistedBatch() {
        // Given
        java.util.List<String> tokens = java.util.Arrays.asList("token1", "token2", "token3");
        when(valueOperations.get(BLACKLIST_PREFIX + "token1")).thenReturn("blacklisted");
        when(valueOperations.get(BLACKLIST_PREFIX + "token2")).thenReturn(null);
        when(valueOperations.get(BLACKLIST_PREFIX + "token3")).thenReturn("blacklisted");

        // When
        java.util.Map<String, Boolean> result = tokenBlacklistService.isBlacklistedBatch(tokens);

        // Then
        assertEquals(3, result.size());
        assertTrue(result.get("token1"));
        assertFalse(result.get("token2"));
        assertTrue(result.get("token3"));
    }
}
