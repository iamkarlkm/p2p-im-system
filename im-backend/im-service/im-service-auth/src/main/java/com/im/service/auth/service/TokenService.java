package com.im.service.auth.service;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 简化的Token服务 - 不使用外部JWT库
 */
@Service
public class TokenService {

    private static final long ACCESS_TOKEN_EXPIRE = 1000 * 60 * 60 * 2; // 2小时
    private static final long REFRESH_TOKEN_EXPIRE = 1000 * 60 * 60 * 24 * 7; // 7天

    public String generateAccessToken(String userId, String username) {
        // 简化实现：生成Base64编码的token
        Map<String, String> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("exp", String.valueOf(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE));
        return encodeToken(claims);
    }

    public String generateRefreshToken(String userId) {
        Map<String, String> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("userId", userId);
        claims.put("exp", String.valueOf(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE));
        return encodeToken(claims);
    }

    public Map<String, String> parseToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            Map<String, String> claims = new HashMap<>();
            for (String pair : decoded.split(";")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    claims.put(kv[0], kv[1]);
                }
            }
            return claims;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        Map<String, String> claims = parseToken(token);
        if (claims == null) return false;
        
        String exp = claims.get("exp");
        if (exp == null) return false;
        
        try {
            long expireTime = Long.parseLong(exp);
            return System.currentTimeMillis() < expireTime;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Map<String, String> claims = parseToken(token);
        return claims != null ? claims.get("userId") : null;
    }

    private String encodeToken(Map<String, String> claims) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : claims.entrySet()) {
            if (sb.length() > 0) sb.append(";");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return Base64.getEncoder().encodeToString(sb.toString().getBytes());
    }
}
