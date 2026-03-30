package com.im.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT令牌提供者
 * 功能 #3: 用户认证与授权模块 - JWT令牌管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnly}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // 24小时
    
    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration; // 7天
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String userId, String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roles", roles);
        claims.put("type", "access");
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * 从令牌获取用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }
    
    /**
     * 从令牌获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }
    
    /**
     * 从令牌获取角色
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
        }
        return Collections.emptyList();
    }
    
    /**
     * 解析令牌
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查令牌是否即将过期
     */
    public boolean isTokenExpiringSoon(String token, long thresholdMs) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Date expiration = claims.getExpiration();
            return expiration != null && 
                   expiration.getTime() - System.currentTimeMillis() < thresholdMs;
        }
        return false;
    }
    
    /**
     * 获取令牌剩余有效期
     */
    public long getTokenRemainingTime(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                long remaining = expiration.getTime() - System.currentTimeMillis();
                return Math.max(0, remaining);
            }
        }
        return 0;
    }
    
    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            return null;
        }
        
        Claims claims = parseToken(refreshToken);
        if (claims == null || !"refresh".equals(claims.get("type"))) {
            return null;
        }
        
        String userId = claims.get("userId", String.class);
        // 实际项目中从数据库获取用户信息和角色
        return generateAccessToken(userId, null, Collections.emptyList());
    }
}
