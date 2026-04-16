package com.im.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JWT Token提供器单元测试
 * 测试覆盖: Token生成、验证、解析、刷新等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Token提供器单元测试")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    
    private static final String TEST_SECRET = "testSecretKeyForJwtTokenProviderTestingOnly12345678901234567890";
    private static final long ACCESS_TOKEN_EXPIRY = 3600000; // 1小时
    private static final long REFRESH_TOKEN_EXPIRY = 604800000; // 7天

    private String userId;
    private String deviceId;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiry", ACCESS_TOKEN_EXPIRY);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiry", REFRESH_TOKEN_EXPIRY);
        
        userId = "user_001";
        deviceId = "device_001";
    }

    @Test
    @DisplayName("生成Access Token - 成功")
    void generateAccessToken_Success() {
        // When
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // Then
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // 验证Token可以解析
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        assertEquals(userId, claims.getSubject());
        assertEquals(deviceId, claims.get("deviceId"));
        assertEquals("access", claims.get("type"));
    }

    @Test
    @DisplayName("生成Refresh Token - 成功")
    void generateRefreshToken_Success() {
        // When
        String token = jwtTokenProvider.generateRefreshToken(userId, deviceId);

        // Then
        assertNotNull(token);
        
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        assertEquals(userId, claims.getSubject());
        assertEquals(deviceId, claims.get("deviceId"));
        assertEquals("refresh", claims.get("type"));
    }

    @Test
    @DisplayName("验证有效Token - 成功")
    void validateToken_Valid() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证过期Token - 失败")
    void validateToken_Expired() {
        // Given - 创建已过期的Token
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        String expiredToken = Jwts.builder()
            .setSubject(userId)
            .claim("deviceId", deviceId)
            .claim("type", "access")
            .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2小时前
            .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1小时前过期
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        // When
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证无效Token - 失败")
    void validateToken_Invalid() {
        // Given - 格式错误的Token
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("从Token获取用户ID - 成功")
    void getUserIdFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        String extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("从Token获取设备ID - 成功")
    void getDeviceIdFromToken_Success() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        String extractedDeviceId = jwtTokenProvider.getDeviceIdFromToken(token);

        // Then
        assertEquals(deviceId, extractedDeviceId);
    }

    @Test
    @DisplayName("获取Token过期时间 - 正确")
    void getExpirationDateFromToken() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
        // 应该在1小时内过期
        assertTrue(expiration.before(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY + 1000)));
    }

    @Test
    @DisplayName("判断Token是否即将过期 - 是")
    void isTokenExpiringSoon_True() {
        // Given - 创建即将过期的Token（4分钟后过期）
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
        String soonExpiringToken = Jwts.builder()
            .setSubject(userId)
            .claim("deviceId", deviceId)
            .setExpiration(new Date(System.currentTimeMillis() + 4 * 60 * 1000)) // 4分钟后过期
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        // When
        boolean expiringSoon = jwtTokenProvider.isTokenExpiringSoon(soonExpiringToken, 5);

        // Then - 距离过期不到5分钟
        assertTrue(expiringSoon);
    }

    @Test
    @DisplayName("判断Token是否即将过期 - 否")
    void isTokenExpiringSoon_False() {
        // Given - 刚生成的Token（1小时后过期）
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        boolean expiringSoon = jwtTokenProvider.isTokenExpiringSoon(token, 5);

        // Then - 距离过期还很久
        assertFalse(expiringSoon);
    }

    @Test
    @DisplayName("获取Token剩余时间 - 正确")
    void getTokenRemainingTime() {
        // Given
        String token = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        long remainingTime = jwtTokenProvider.getTokenRemainingTime(token);

        // Then
        assertTrue(remainingTime > 0);
        assertTrue(remainingTime <= ACCESS_TOKEN_EXPIRY);
    }

    @Test
    @DisplayName("验证Token类型 - Access Token")
    void getTokenType_Access() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When
        String type = jwtTokenProvider.getTokenType(accessToken);

        // Then
        assertEquals("access", type);
    }

    @Test
    @DisplayName("验证Token类型 - Refresh Token")
    void getTokenType_Refresh() {
        // Given
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, deviceId);

        // When
        String type = jwtTokenProvider.getTokenType(refreshToken);

        // Then
        assertEquals("refresh", type);
    }

    @Test
    @DisplayName("刷新Access Token - 使用有效Refresh Token")
    void refreshAccessToken_Success() {
        // Given
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, deviceId);

        // When
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);

        // Then
        assertNotNull(newAccessToken);
        assertNotEquals(refreshToken, newAccessToken);
        
        Claims claims = jwtTokenProvider.getClaimsFromToken(newAccessToken);
        assertEquals(userId, claims.getSubject());
        assertEquals("access", claims.get("type"));
    }

    @Test
    @DisplayName("使用Access Token刷新 - 失败")
    void refreshAccessToken_WithAccessToken_Fail() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(userId, deviceId);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.refreshAccessToken(accessToken);
        });
    }

    @Test
    @DisplayName("生成包含额外Claims的Token - 成功")
    void generateTokenWithExtraClaims() {
        // Given
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        extraClaims.put("role", "admin");
        extraClaims.put("permissions", "read,write");

        // When
        String token = jwtTokenProvider.generateToken(userId, deviceId, extraClaims, ACCESS_TOKEN_EXPIRY);

        // Then
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        assertEquals("admin", claims.get("role"));
        assertEquals("read,write", claims.get("permissions"));
    }
}
