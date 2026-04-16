package com.im.service.auth.service;

import com.im.service.auth.dto.LoginRequest;
import com.im.service.auth.dto.LoginResponse;
import com.im.service.auth.dto.TokenRefreshRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final StringRedisTemplate redisTemplate;
    private static final long ACCESS_TOKEN_EXPIRE = 7200; // 2小时
    private static final long REFRESH_TOKEN_EXPIRE = 604800; // 7天

    public AuthService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public LoginResponse login(LoginRequest request) {
        // 简化实现：实际项目中需要验证用户名密码
        String userId = UUID.randomUUID().toString();
        String accessToken = generateToken();
        String refreshToken = generateToken();

        // 存储到Redis
        redisTemplate.opsForValue().set("token:" + accessToken, userId, ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("refresh:" + refreshToken, userId, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);

        LoginResponse response = new LoginResponse();
        response.setUserId(userId);
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(ACCESS_TOKEN_EXPIRE);
        response.setTokenType("Bearer");
        response.setPermissions(Arrays.asList("message:send", "message:read", "group:join"));
        return response;
    }

    public LoginResponse refreshToken(TokenRefreshRequest request) {
        String userId = redisTemplate.opsForValue().get("refresh:" + request.getRefreshToken());
        if (userId == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = generateToken();
        String newRefreshToken = generateToken();

        redisTemplate.opsForValue().set("token:" + newAccessToken, userId, ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("refresh:" + newRefreshToken, userId, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);

        LoginResponse response = new LoginResponse();
        response.setUserId(userId);
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(ACCESS_TOKEN_EXPIRE);
        response.setTokenType("Bearer");
        return response;
    }

    public boolean logout(String accessToken) {
        redisTemplate.delete("token:" + accessToken);
        return true;
    }

    public String validateToken(String accessToken) {
        return redisTemplate.opsForValue().get("token:" + accessToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
