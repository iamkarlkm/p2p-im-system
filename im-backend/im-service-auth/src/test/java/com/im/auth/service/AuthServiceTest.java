package com.im.auth.service;

import com.im.auth.dto.LoginRequest;
import com.im.auth.dto.LoginResponse;
import com.im.auth.dto.LogoutRequest;
import com.im.auth.dto.RefreshTokenRequest;
import com.im.auth.entity.RefreshToken;
import com.im.auth.exception.InvalidCredentialsException;
import com.im.auth.exception.TokenReuseException;
import com.im.auth.exception.TooManyLoginAttemptsException;
import com.im.auth.repository.RefreshTokenRepository;
import com.im.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 * 测试覆盖: 登录、登出、Token刷新等核心功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务单元测试")
class AuthServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest validLoginRequest;
    private com.im.user.entity.User validUser;
    private RefreshToken validRefreshToken;

    @BeforeEach
    void setUp() {
        // 准备登录请求
        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsername("testuser");
        validLoginRequest.setPassword("password123");
        validLoginRequest.setDeviceId("device_001");

        // 准备用户实体
        validUser = new com.im.user.entity.User();
        validUser.setUserId("user_001");
        validUser.setUsername("testuser");
        validUser.setPassword("encoded_password");
        validUser.setStatus(com.im.user.enums.UserStatus.ACTIVE);
        validUser.setLoginFailCount(0);
        validUser.setIsLocked(false);

        // 准备Refresh Token
        validRefreshToken = new RefreshToken();
        validRefreshToken.setTokenId("rt_001");
        validRefreshToken.setToken("refresh_token_string");
        validRefreshToken.setUserId("user_001");
        validRefreshToken.setDeviceId("device_001");
        validRefreshToken.setCreatedAt(LocalDateTime.now());
        validRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        validRefreshToken.setIsRevoked(false);
    }

    @Test
    @DisplayName("正常登录 - 成功")
    void login_Success() {
        // Given
        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.getUserByUsername("testuser")).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken("user_001", "device_001")).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken("user_001", "device_001")).thenReturn("refresh_token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(validRefreshToken);

        // When
        LoginResponse response = authService.login(validLoginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals("user_001", response.getUserId());
        
        verify(loginAttemptService).loginSucceeded("testuser");
    }

    @Test
    @DisplayName("登录 - 无效凭证")
    void login_InvalidCredentials() {
        // Given
        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.getUserByUsername("testuser")).thenReturn(Optional.of(validUser));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(validLoginRequest);
        });

        verify(loginAttemptService).loginFailed("testuser");
    }

    @Test
    @DisplayName("登录 - 账号被锁定")
    void login_AccountLocked() {
        // Given
        validUser.setIsLocked(true);
        when(loginAttemptService.isBlocked("testuser")).thenReturn(false);
        when(loginAttemptService.getUserByUsername("testuser")).thenReturn(Optional.of(validUser));

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(validLoginRequest);
        });
    }

    @Test
    @DisplayName("登录 - 尝试次数过多")
    void login_TooManyAttempts() {
        // Given
        when(loginAttemptService.isBlocked("testuser")).thenReturn(true);

        // When & Then
        assertThrows(TooManyLoginAttemptsException.class, () -> {
            authService.login(validLoginRequest);
        });
    }

    @Test
    @DisplayName("刷新Token - 成功")
    void refreshToken_Success() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid_refresh_token");

        when(refreshTokenRepository.findByToken("valid_refresh_token")).thenReturn(Optional.of(validRefreshToken));
        when(jwtTokenProvider.validateToken("valid_refresh_token")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("valid_refresh_token")).thenReturn("user_001");
        when(jwtTokenProvider.getDeviceIdFromToken("valid_refresh_token")).thenReturn("device_001");
        when(jwtTokenProvider.generateAccessToken("user_001", "device_001")).thenReturn("new_access_token");
        when(jwtTokenProvider.generateRefreshToken("user_001", "device_001")).thenReturn("new_refresh_token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(validRefreshToken);

        // When
        LoginResponse response = authService.refreshToken(request);

        // Then
        assertNotNull(response);
        assertEquals("new_access_token", response.getAccessToken());
        assertEquals("new_refresh_token", response.getRefreshToken());
        
        // 旧的Refresh Token应该被标记为已使用
        verify(refreshTokenRepository).delete(validRefreshToken);
    }

    @Test
    @DisplayName("刷新Token - Token重复使用")
    void refreshToken_ReuseDetected() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("used_refresh_token");

        validRefreshToken.setIsRevoked(true);
        when(refreshTokenRepository.findByToken("used_refresh_token")).thenReturn(Optional.of(validRefreshToken));
        when(jwtTokenProvider.validateToken("used_refresh_token")).thenReturn(true);

        // When & Then
        assertThrows(TokenReuseException.class, () -> {
            authService.refreshToken(request);
        });
    }

    @Test
    @DisplayName("登出 - 成功")
    void logout_Success() {
        // Given
        LogoutRequest request = new LogoutRequest();
        request.setAccessToken("access_token");
        request.setRefreshToken("refresh_token");
        request.setUserId("user_001");
        request.setDeviceId("device_001");

        when(jwtTokenProvider.getExpirationDateFromToken("access_token"))
            .thenReturn(new java.util.Date(System.currentTimeMillis() + 3600000));
        when(refreshTokenRepository.findByToken("refresh_token")).thenReturn(Optional.of(validRefreshToken));

        // When
        authService.logout(request);

        // Then
        verify(tokenBlacklistService).addToBlacklist("access_token", 3600L);
        verify(refreshTokenRepository).delete(validRefreshToken);
    }

    @Test
    @DisplayName("全设备登出 - 成功")
    void logoutAllDevices_Success() {
        // Given
        when(refreshTokenRepository.findByUserId("user_001"))
            .thenReturn(java.util.Arrays.asList(validRefreshToken));

        // When
        authService.logoutAllDevices("user_001", "device_001");

        // Then
        verify(refreshTokenRepository).deleteByUserId("user_001");
    }

    @Test
    @DisplayName("验证Token有效性 - 有效")
    void validateToken_Valid() {
        // Given
        when(jwtTokenProvider.validateToken("valid_token")).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted("valid_token")).thenReturn(false);

        // When
        boolean isValid = authService.validateToken("valid_token");

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("验证Token有效性 - 在黑名单中")
    void validateToken_Blacklisted() {
        // Given
        when(jwtTokenProvider.validateToken("blacklisted_token")).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted("blacklisted_token")).thenReturn(true);

        // When
        boolean isValid = authService.validateToken("blacklisted_token");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("验证Token有效性 - Token无效")
    void validateToken_Invalid() {
        // Given
        when(jwtTokenProvider.validateToken("invalid_token")).thenReturn(false);

        // When
        boolean isValid = authService.validateToken("invalid_token");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("从Token获取用户ID - 成功")
    void getUserIdFromToken_Success() {
        // Given
        when(jwtTokenProvider.getUserIdFromToken("token")).thenReturn("user_001");

        // When
        String userId = authService.getUserIdFromToken("token");

        // Then
        assertEquals("user_001", userId);
    }

    @Test
    @DisplayName("从Token获取设备ID - 成功")
    void getDeviceIdFromToken_Success() {
        // Given
        when(jwtTokenProvider.getDeviceIdFromToken("token")).thenReturn("device_001");

        // When
        String deviceId = authService.getDeviceIdFromToken("token");

        // Then
        assertEquals("device_001", deviceId);
    }
}
