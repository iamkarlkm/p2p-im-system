package com.im.service.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtTokenProvider 单元测试
 * 
 * 测试覆盖:
 * - Access Token 生成与验证
 * - Refresh Token 生成与验证
 * - Token 过期检查
 * - 从Token提取用户信息
 * 
 * @author IM Team
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Token提供者单元测试")
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_SECRET = "im-modular-jwt-secret-key-for-hs512-algorithm-must-be-at-least-512-bits-long-enough";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_DEVICE_ID = "device_001";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", 1800000L); // 30分钟
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpiration", 604800000L); // 7天
        ReflectionTestUtils.setField(jwtTokenProvider, "issuer", "im-modular-auth");
        jwtTokenProvider.init();
    }

    // ========== Access Token 测试 ==========

    @Test
    @DisplayName("生成Access Token成功")
    void generateAccessToken() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("message:send")
        );
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);

        // Act
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        // 验证token可以解析
        String extractedUsername = jwtTokenProvider.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(TEST_USERNAME);
        
        Long extractedUserId = jwtTokenProvider.extractUserId(token);
        assertThat(extractedUserId).isEqualTo(TEST_USER_ID);
        
        String extractedDeviceId = jwtTokenProvider.extractDeviceId(token);
        assertThat(extractedDeviceId).isEqualTo(TEST_DEVICE_ID);
    }

    @Test
    @DisplayName("验证Access Token有效")
    void validateToken_Valid() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        boolean isValid = jwtTokenProvider.validateAccessToken(token);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("验证Token已过期")
    void validateToken_Expired() {
        // Prepare - 创建已过期的token
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpiration", -1000L); // 已过期
        jwtTokenProvider.init();
        
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String expiredToken = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        boolean isValid = jwtTokenProvider.validateAccessToken(expiredToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    // ========== Refresh Token 测试 ==========

    @Test
    @DisplayName("生成Refresh Token成功")
    void generateRefreshToken() {
        // Act
        String refreshToken = jwtTokenProvider.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_DEVICE_ID);

        // Assert
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        
        // 验证token类型
        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("从Token中提取用户ID")
    void getUserIdFromToken() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        Long userId = jwtTokenProvider.extractUserId(token);

        // Assert
        assertThat(userId).isEqualTo(TEST_USER_ID);
    }

    @Test
    @DisplayName("从Token中提取用户名")
    void extractUsername() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        String username = jwtTokenProvider.extractUsername(token);

        // Assert
        assertThat(username).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("从Token中提取权限")
    void extractAuthorities() {
        // Prepare
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("message:read"),
                new SimpleGrantedAuthority("message:send")
        );
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        String authString = jwtTokenProvider.extractAuthorities(token);

        // Assert
        assertThat(authString).contains("ROLE_USER");
        assertThat(authString).contains("message:read");
        assertThat(authString).contains("message:send");
    }

    @Test
    @DisplayName("检查Token是否过期-未过期")
    void isTokenExpired_False() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        boolean isExpired = jwtTokenProvider.isTokenExpired(token);

        // Assert
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("验证Token与用户详情匹配")
    void isTokenValid() {
        // Prepare
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User(TEST_USERNAME, "password", authorities);
        String token = jwtTokenProvider.generateAccessToken(userDetails, TEST_USER_ID, TEST_DEVICE_ID);

        // Act
        boolean isValid = jwtTokenProvider.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("从Authorization头提取Token")
    void extractTokenFromHeader() {
        // Prepare
        String bearerToken = "Bearer abcdef123456";

        // Act
        String token = jwtTokenProvider.extractTokenFromHeader(bearerToken);

        // Assert
        assertThat(token).isEqualTo("abcdef123456");
    }

    @Test
    @DisplayName("从Authorization头提取Token-无Bearer前缀")
    void extractTokenFromHeader_NoBearer() {
        // Prepare
        String bearerToken = "abcdef123456";

        // Act
        String token = jwtTokenProvider.extractTokenFromHeader(bearerToken);

        // Assert
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("生成Bearer Token")
    void generateBearerToken() {
        // Prepare
        String token = "test_token_123";

        // Act
        String bearerToken = jwtTokenProvider.generateBearerToken(token);

        // Assert
        assertThat(bearerToken).isEqualTo("Bearer test_token_123");
    }

    @Test
    @DisplayName("从Refresh Token提取JTI")
    void extractTokenId() {
        // Prepare
        String refreshToken = jwtTokenProvider.generateRefreshToken(TEST_USER_ID, TEST_USERNAME, TEST_DEVICE_ID);

        // Act
        String jti = jwtTokenProvider.extractTokenId(refreshToken);

        // Assert
        assertThat(jti).isNotNull();
        assertThat(jti).isNotEmpty();
    }
}
