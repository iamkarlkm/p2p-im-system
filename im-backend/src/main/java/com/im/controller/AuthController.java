package com.im.controller;

import com.im.dto.LoginRequest;
import com.im.dto.LoginResponse;
import com.im.dto.RegisterRequest;
import com.im.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 功能 #3: 用户认证与授权模块 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    @Autowired
    private IAuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            var user = authService.register(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", user.getUserId(),
                "message", "Registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", response.getAccessToken(),
                "refreshToken", response.getRefreshToken(),
                "expiresIn", response.getExpiresIn()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        var response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "token", response.getAccessToken()
        ));
    }
}
