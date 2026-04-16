package com.im.service.auth.service;

import com.im.service.auth.dto.*;
import com.im.service.auth.entity.RefreshToken;
import com.im.service.auth.repository.RefreshTokenRepository;
import com.im.service.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务 - 核心业务逻辑实现
 * 
 * 功能特性：
 * 1. 用户登录认证 - 用户名密码验证
 * 2. Token 刷新机制 - Access Token 过期后刷新
 * 3. 用户登出处理 - Token 加入黑名单
 * 4. 登录失败限制 - 防止暴力破解
 * 5. 设备标识管理 - 多设备登录支持
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 键前缀
     */
    private static final String LOGIN_ATTEMPT_KEY_PREFIX = "login_attempt:";
    private static final String USER_LOCK_KEY_PREFIX = "user_lock:";

    /**
     * 登录失败次数限制
     */
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    
    /**
     * 登录失败锁定时间（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 30;
    
    /**
     * 登录失败计数过期时间（分钟）
     */
    private static final int LOGIN_ATTEMPT_EXPIRY_MINUTES = 30;

    // ==================== 登录认证 ====================

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String deviceId = request.getDeviceId();
        
        log.info("Login attempt for user: {}, device: {}", username, deviceId);

        // 1. 检查用户是否被锁定
        if (isUserLocked(username)) {
            log.warn("User is locked: {}", username);
            throw new LockedException("Account is temporarily locked due to multiple failed login attempts. " +
                    "Please try again after " + LOCK_DURATION_MINUTES + " minutes.");
        }

        try {
            // 2. 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            request.getPassword()
                    )
            );

            // 3. 认证成功，清除失败计数
            clearLoginAttempts(username);

            // 4. 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 5. 获取用户ID（实际应该从 userDetails 或数据库获取）
            Long userId = extractUserIdFromUserDetails(userDetails);

            // 6. 生成 Token
            String accessToken = jwtTokenProvider.generateAccessToken(authentication, userId, deviceId);
            String refreshToken = generateAndSaveRefreshToken(userId, username, deviceId);

            log.info("Login successful for user: {}, userId: {}", username, userId);

            // 7. 返回响应
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                    .userId(userId)
                    .username(username)
                    .deviceId(deviceId)
                    .build();

        } catch (BadCredentialsException e) {
            // 认证失败，增加失败计数
            handleFailedLogin(username);
            log.warn("Login failed for user: {} - Bad credentials", username);
            throw new BadCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            log.error("Authentication error for user: {} - {}", username, e.getMessage());
            throw e;
        }
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());

        // 实际项目中应该调用用户服务进行注册
        // 这里简化处理，仅返回成功响应
        
        // TODO: 调用用户服务创建用户
        // User newUser = userService.createUser(request);

        log.info("Registration successful for username: {}", request.getUsername());

        return RegisterResponse.builder()
                .success(true)
                .message("Registration successful. Please verify your email.")
                .build();
    }

    // ==================== Token 刷新 ====================

    /**
     * 刷新 Access Token
     *
     * @param request 刷新请求
     * @return 新的 Token 响应
     */
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String deviceId = request.getDeviceId();

        log.info("Token refresh attempt, device: {}", deviceId);

        // 1. 验证 Refresh Token
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new BadCredentialsException("Invalid refresh token");
        }

        // 2. 检查 Token 是否在黑名单中
        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            log.warn("Refresh token is blacklisted");
            throw new BadCredentialsException("Token has been revoked");
        }

        // 3. 从 Token 中提取信息
        String username = jwtTokenProvider.extractUsername(refreshToken);
        Long userId = jwtTokenProvider.extractUserId(refreshToken);
        String tokenDeviceId = jwtTokenProvider.extractDeviceId(refreshToken);

        // 4. 验证设备ID
        if (!deviceId.equals(tokenDeviceId)) {
            log.warn("Device ID mismatch during token refresh");
            throw new BadCredentialsException("Invalid device");
        }

        // 5. 从数据库查询 Refresh Token
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));

        // 6. 检查是否已使用或已过期
        if (storedToken.isUsed()) {
            log.warn("Refresh token has already been used: {}", storedToken.getId());
            // 标记为泄露，撤销该用户的所有 Token
            revokeAllUserTokens(userId);
            throw new BadCredentialsException("Token reuse detected. All tokens have been revoked for security.");
        }

        if (storedToken.isExpired()) {
            log.warn("Refresh token has expired: {}", storedToken.getId());
            throw new BadCredentialsException("Refresh token has expired");
        }

        // 7. 标记当前 Token 为已使用
        storedToken.setUsed(true);
        refreshTokenRepository.save(storedToken);

        // 8. 获取用户详情
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 9. 生成新的 Token 对
        String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails, userId, deviceId);
        String newRefreshToken = generateAndSaveRefreshToken(userId, username, deviceId);

        log.info("Token refresh successful for user: {}", username);

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();
    }

    // ==================== 登出处理 ====================

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     * @return 是否成功
     */
    @Transactional
    public boolean logout(HttpServletRequest request) {
        // 1. 提取 Access Token
        String accessToken = extractTokenFromRequest(request);
        
        if (accessToken == null) {
            log.warn("No token found for logout");
            return false;
        }

        // 2. 验证 Token 并获取信息
        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            String username = jwtTokenProvider.extractUsername(accessToken);
            Long userId = jwtTokenProvider.extractUserId(accessToken);
            String deviceId = jwtTokenProvider.extractDeviceId(accessToken);

            log.info("Logout for user: {}, device: {}", username, deviceId);

            // 3. 将 Access Token 加入黑名单
            long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);
            tokenBlacklistService.addToBlacklist(accessToken, expirationTime);

            // 4. 撤销该设备的 Refresh Token
            refreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId);

            log.info("Logout successful for user: {}", username);
            return true;
        }

        log.warn("Invalid token for logout");
        return false;
    }

    /**
     * 全设备登出（撤销所有 Token）
     *
     * @param request HTTP 请求
     * @return 是否成功
     */
    @Transactional
    public boolean logoutAllDevices(HttpServletRequest request) {
        String accessToken = extractTokenFromRequest(request);
        
        if (accessToken == null) {
            log.warn("No token found for logout all devices");
            return false;
        }

        if (jwtTokenProvider.validateAccessToken(accessToken)) {
            Long userId = jwtTokenProvider.extractUserId(accessToken);
            String username = jwtTokenProvider.extractUsername(accessToken);

            log.info("Logout all devices for user: {}", username);

            // 将当前 Access Token 加入黑名单
            long expirationTime = jwtTokenProvider.getExpirationTime(accessToken);
            tokenBlacklistService.addToBlacklist(accessToken, expirationTime);

            // 撤销该用户的所有 Refresh Token
            revokeAllUserTokens(userId);

            log.info("All devices logged out for user: {}", username);
            return true;
        }

        return false;
    }

    // ==================== 辅助方法 ====================

    /**
     * 生成并保存 Refresh Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param deviceId 设备ID
     * @return Refresh Token 字符串
     */
    private String generateAndSaveRefreshToken(Long userId, String username, String deviceId) {
        // 1. 删除该设备的旧 Refresh Token
        refreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId);

        // 2. 生成新的 Refresh Token
        String token = jwtTokenProvider.generateRefreshToken(userId, username, deviceId);
        String tokenId = jwtTokenProvider.extractTokenId(token);
        Instant expiryDate = Instant.ofEpochMilli(
                System.currentTimeMillis() + jwtTokenProvider.getRefreshTokenExpiration()
        );

        // 3. 保存到数据库
        RefreshToken refreshToken = RefreshToken.builder()
                .id(tokenId)
                .userId(userId)
                .username(username)
                .token(token)
                .deviceId(deviceId)
                .expiryDate(expiryDate)
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    /**
     * 撤销用户的所有 Token
     *
     * @param userId 用户ID
     */
    private void revokeAllUserTokens(Long userId) {
        log.warn("Revoking all tokens for user: {}", userId);
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    /**
     * 从请求中提取 Token
     *
     * @param request HTTP 请求
     * @return Token 字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 从 UserDetails 提取用户ID
     * 实际项目中应该从用户信息中获取
     *
     * @param userDetails 用户详情
     * @return 用户ID
     */
    private Long extractUserIdFromUserDetails(UserDetails userDetails) {
        // 实际项目中，UserDetails 应该包含用户ID
        // 这里简化处理，使用 username 的 hashCode 作为示例
        // TODO: 从实际的用户对象中获取 userId
        return (long) userDetails.getUsername().hashCode();
    }

    // ==================== 登录失败限制 ====================

    /**
     * 处理登录失败
     *
     * @param username 用户名
     */
    private void handleFailedLogin(String username) {
        String key = LOGIN_ATTEMPT_KEY_PREFIX + username;
        
        // 获取当前失败次数
        Integer attempts = (Integer) redisTemplate.opsForValue().get(key);
        if (attempts == null) {
            attempts = 0;
        }
        
        attempts++;
        
        // 保存新的失败次数
        redisTemplate.opsForValue().set(
                key, 
                attempts, 
                LOGIN_ATTEMPT_EXPIRY_MINUTES, 
                TimeUnit.MINUTES
        );
        
        // 如果达到最大失败次数，锁定用户
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            lockUser(username);
            log.warn("User locked due to too many failed login attempts: {}", username);
        }
    }

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     * @return 是否被锁定
     */
    private boolean isUserLocked(String username) {
        String key = USER_LOCK_KEY_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 锁定用户
     *
     * @param username 用户名
     */
    private void lockUser(String username) {
        String key = USER_LOCK_KEY_PREFIX + username;
        redisTemplate.opsForValue().set(
                key, 
                true, 
                LOCK_DURATION_MINUTES, 
                TimeUnit.MINUTES
        );
    }

    /**
     * 清除登录失败计数
     *
     * @param username 用户名
     */
    private void clearLoginAttempts(String username) {
        String key = LOGIN_ATTEMPT_KEY_PREFIX + username;
        redisTemplate.delete(key);
    }
}
