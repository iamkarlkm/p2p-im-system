package com.im.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;

/**
 * JWT令牌管理器
 * 功能 #3: 用户认证与授权模块 - JWT令牌管理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class JwtTokenManager {
    
    private static final String SECRET_KEY = "im-system-secret-key-for-jwt-signing-must-be-at-least-256-bits-long";
    private static final long ACCESS_TOKEN_EXPIRE = 24 * 60 * 60 * 1000; // 24小时
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000; // 7天
    
    /**
     * 生成访问令牌
     */
    public String generateAccessToken(String userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("username", username)
                .claim("type", "access")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 从令牌获取用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    /**
     * 获取令牌过期时间
     */
    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
}
