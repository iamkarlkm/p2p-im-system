package com.im.service.auth.service;

import com.im.service.auth.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TokenBlacklistService 单元测试
 * 
 * 测试覆盖:
 * - 添加Token到黑名单
 * - 检查Token是否在黑名单中
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Token黑名单服务单元测试")
class TokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private static final String TEST_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    private static final long EXPIRATION_TIME = 3600000L; // 1小时

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    // ========== 添加黑名单测试 ==========

    @Test
    @DisplayName("添加Token到黑名单成功")
    void addToBlacklist() {
        // Prepare
        when(valueOperations.set(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(setOperations.add(anyString(), any())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // Act
        tokenBlacklistService.addToBlacklist(TEST_TOKEN, EXPIRATION_TIME);

        // Assert
        verify(valueOperations, times(1)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(setOperations, times(1)).add(anyString(), any());
        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("检查Token在黑名单中-存在")
    void isBlacklisted_True() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        // Act
        boolean result = tokenBlacklistService.isBlacklisted(TEST_TOKEN);

        // Assert
        assertThat(result).isTrue();
        verify(redisTemplate, times(1)).hasKey(anyString());
    }

    @Test
    @DisplayName("检查Token在黑名单中-不存在")
    void isBlacklisted_False() {
        // Prepare
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Act
        boolean result = tokenBlacklistService.isBlacklisted(TEST_TOKEN);

        // Assert
        assertThat(result).isFalse();
        verify(redisTemplate, times(1)).hasKey(anyString());
    }

    @Test
    @DisplayName("从黑名单中移除Token")
    void removeFromBlacklist() {
        // Prepare
        when(redisTemplate.delete(anyString())).thenReturn(true);
        when(setOperations.remove(anyString(), any())).thenReturn(1L);

        // Act
        tokenBlacklistService.removeFromBlacklist(TEST_TOKEN);

        // Assert
        verify(redisTemplate, times(1)).delete(anyString());
        verify(setOperations, times(1)).remove(anyString(), any());
    }

    @Test
    @DisplayName("获取黑名单Token数量")
    void getBlacklistCount() {
        // Prepare
        when(setOperations.size(anyString())).thenReturn(5L);

        // Act
        long count = tokenBlacklistService.getBlacklistCount();

        // Assert
        assertThat(count).isEqualTo(5L);
        verify(setOperations, times(1)).size(anyString());
    }

    @Test
    @DisplayName("批量添加Token到黑名单")
    void addAllToBlacklist() {
        // Prepare
        String[] tokens = {"token1", "token2", "token3"};
        when(valueOperations.set(anyString(), any(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(setOperations.add(anyString(), any())).thenReturn(1L);
        when(redisTemplate.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        // Act
        tokenBlacklistService.addAllToBlacklist(tokens, EXPIRATION_TIME);

        // Assert
        verify(valueOperations, times(3)).set(anyString(), any(), anyLong(), any(TimeUnit.class));
        verify(setOperations, times(3)).add(anyString(), any());
    }

    @Test
    @DisplayName("检查多个Token是否有任一在黑名单中-存在")
    void isAnyBlacklisted_True() {
        // Prepare
        String[] tokens = {"token1", "token2"};
        when(redisTemplate.hasKey(contains("token1"))).thenReturn(false);
        when(redisTemplate.hasKey(contains("token2"))).thenReturn(true);

        // Act
        boolean result = tokenBlacklistService.isAnyBlacklisted(tokens);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("检查多个Token是否有任一在黑名单中-都不存在")
    void isAnyBlacklisted_False() {
        // Prepare
        String[] tokens = {"token1", "token2"};
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // Act
        boolean result = tokenBlacklistService.isAnyBlacklisted(tokens);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("清空黑名单")
    void clearBlacklist() {
        // Prepare
        Set<String> keys = Set.of("token:blacklist:hash1", "token:blacklist:hash2");
        when(redisTemplate.keys(anyString())).thenReturn(keys);
        when(redisTemplate.delete(anyCollection())).thenReturn(2L);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // Act
        tokenBlacklistService.clearBlacklist();

        // Assert
        verify(redisTemplate, times(1)).keys(anyString());
        verify(redisTemplate, times(1)).delete(anyCollection());
    }

    @Test
    @DisplayName("获取黑名单统计信息")
    void getBlacklistStats() {
        // Prepare
        when(setOperations.size(anyString())).thenReturn(10L);
        when(redisTemplate.opsForHash().get(anyString(), eq("totalAdded"))).thenReturn(100L);
        when(redisTemplate.opsForHash().get(anyString(), eq("lastCleanupTime"))).thenReturn(System.currentTimeMillis());

        // Act
        TokenBlacklistService.BlacklistStats stats = tokenBlacklistService.getBlacklistStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getCurrentCount()).isEqualTo(10L);
        assertThat(stats.getTotalAdded()).isEqualTo(100L);
        assertThat(stats.getLastCleanupTime()).isGreaterThan(0);
    }
}
