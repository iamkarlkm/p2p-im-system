package com.im.service.auth.controller;

import com.im.service.auth.dto.*;
import com.im.service.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器 - REST API 端点
 * 
 * 功能特性：
 * 1. 用户登录 - /api/auth/login
 * 2. 用户注册 - /api/auth/register
 * 3. Token 刷新 - /api/auth/refresh
 * 4. 用户登出 - /api/auth/logout
 * 5. 全设备登出 - /api/auth/logout-all
 * 6. 密码重置 - /api/auth/forgot-password, /api/auth/reset-password
 * 7. Token 验证 - /api/auth/verify
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    // ==================== 认证端点 ====================

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token）
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsername());
        
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch (Exception e) {
            log.error("Login failed for user: {} - {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for username: {}", request.getUsername());
        
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Registration successful"));
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 刷新 Token
     *
     * @param request 刷新请求
     * @return 新的 Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        log.info("Token refresh request received");
        
        try {
            TokenRefreshResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== 登出端点 ====================

    /**
     * 用户登出（当前设备）
     *
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        log.info("Logout request received");
        
        boolean success = authService.logout(request);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed"));
        }
    }

    /**
     * 全设备登出
     *
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices(HttpServletRequest request) {
        log.info("Logout all devices request received");
        
        boolean success = authService.logoutAllDevices(request);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.success(null, "All devices logged out successfully"));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed"));
        }
    }

    // ==================== 密码管理端点 ====================

    /**
     * 忘记密码 - 发送重置邮件
     *
     * @param request 忘记密码请求
     * @return 响应
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        
        // TODO: 实现发送密码重置邮件逻辑
        // 这里简化处理，仅返回成功响应
        
        return ResponseEntity.ok(ApiResponse.success(null, 
                "If the email exists, a password reset link has been sent"));
    }

    /**
     * 重置密码
     *
     * @param request 重置密码请求
     * @return 响应
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request");
        
        // TODO: 实现密码重置逻辑
        // 验证重置令牌，更新密码
        
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
    }

    // ==================== Token 验证端点 ====================

    /**
     * 验证 Token 有效性
     *
     * @param request 验证请求
     * @return 验证结果
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<TokenVerifyResponse>> verifyToken(
            @Valid @RequestBody TokenVerifyRequest request) {
        log.info("Token verification request");
        
        // TODO: 调用 Token 验证服务
        // 这里简化处理
        
        TokenVerifyResponse response = TokenVerifyResponse.builder()
                .valid(true)
                .username("user")
                .userId(1L)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Token is valid"));
    }

    // ==================== 健康检查端点 ====================

    /**
     * 认证服务健康检查
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "auth-service");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }
}
