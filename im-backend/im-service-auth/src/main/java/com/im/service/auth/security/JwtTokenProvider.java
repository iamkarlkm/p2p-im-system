package com.im.service.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT Token 生成与验证工具类
 * 
 * 功能特性：
 * 1. Access Token 生成与验证（30分钟有效期）
 * 2. Refresh Token 生成与验证（7天有效期）
 * 3. Token 解析与信息提取
 * 4. Token 过期检查
 * 5. Token 黑名单校验支持
 * 
 * 加密算法：HS512 (HMAC-SHA512)
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /**
     * JWT 密钥，从配置文件读取
     */
    @Value("${jwt.secret:im-modular-jwt-secret-key-for-hs512-algorithm-must-be-at-least-512-bits}")
    private String jwtSecret;

    /**
     * Access Token 有效期（毫秒），默认30分钟
     */
    @Value("${jwt.access-token-expiration:1800000}")
    private long accessTokenExpiration;

    /**
     * Refresh Token 有效期（毫秒），默认7天
     */
    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration;

    /**
     * 签发者
     */
    @Value("${jwt.issuer:im-modular-auth}")
    private String issuer;

    /**
     * JWT 签名密钥
     */
    private SecretKey secretKey;

    /**
     * Token 类型声明
     */
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_USER_ID = "user_id";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_DEVICE_ID = "device_id";

    /**
     * 初始化签名密钥
     */
    @PostConstruct
    public void init() {
        // 确保密钥长度至少为512位（64字节）以支持HS512
        String keyString = jwtSecret;
        if (keyString.length() < 64) {
            keyString = String.format("%-64s", keyString).replace(' ', 'X');
            log.warn("JWT secret key is too short for HS512, padded to 64 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Token Provider initialized with HS512 algorithm");
    }

    // ==================== Access Token 操作 ====================

    /**
     * 生成 Access Token
     *
     * @param authentication Spring Security 认证对象
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return Access Token 字符串
     */
    public String generateAccessToken(Authentication authentication, Long userId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_DEVICE_ID, deviceId);
        
        // 提取用户权限
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(CLAIM_AUTHORITIES, authorities);

        return buildToken(claims, authentication.getName(), accessTokenExpiration);
    }

    /**
     * 生成 Access Token（使用UserDetails）
     *
     * @param userDetails 用户详情
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return Access Token 字符串
     */
    public String generateAccessToken(UserDetails userDetails, Long userId, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_DEVICE_ID, deviceId);
        
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(CLAIM_AUTHORITIES, authorities);

        return buildToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    /**
     * 验证 Access Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            
            if (!TOKEN_TYPE_ACCESS.equals(tokenType)) {
                log.warn("Token type mismatch: expected 'access', got '{}'", tokenType);
                return false;
            }
            
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("Access token has expired: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid access token: {}", e.getMessage());
            return false;
        }
    }

    // ==================== Refresh Token 操作 ====================

    /**
     * 生成 Refresh Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param deviceId 设备ID
     * @return Refresh Token 字符串
     */
    public String generateRefreshToken(Long userId, String username, String deviceId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_DEVICE_ID, deviceId);
        
        // Refresh Token 使用唯一的JTI用于黑名单管理
        claims.put(Claims.ID, UUID.randomUUID().toString());

        return buildToken(claims, username, refreshTokenExpiration);
    }

    /**
     * 验证 Refresh Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            
            if (!TOKEN_TYPE_REFRESH.equals(tokenType)) {
                log.warn("Token type mismatch: expected 'refresh', got '{}'", tokenType);
                return false;
            }
            
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token has expired: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Refresh Token 中提取 JTI（唯一标识）
     *
     * @param token Refresh Token
     * @return JTI 字符串
     */
    public String extractTokenId(String token) {
        return extractClaim(token, Claims::getId);
    }

    // ==================== Token 通用操作 ====================

    /**
     * 构建 JWT Token
     *
     * @param claims 自定义声明
     * @param subject 主题（用户名）
     * @param expiration 过期时间（毫秒）
     * @return JWT Token 字符串
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从 Token 中提取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_USER_ID, Long.class);
    }

    /**
     * 从 Token 中提取设备ID
     *
     * @param token JWT Token
     * @return 设备ID
     */
    public String extractDeviceId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_DEVICE_ID, String.class);
    }

    /**
     * 从 Token 中提取权限列表
     *
     * @param token JWT Token
     * @return 权限字符串（逗号分隔）
     */
    public String extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get(CLAIM_AUTHORITIES, String.class);
    }

    /**
     * 从 Token 中提取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从 Token 中提取签发时间
     *
     * @param token JWT Token
     * @return 签发时间
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * 提取指定声明
     *
     * @param token JWT Token
     * @param claimsResolver 声明解析器
     * @param <T> 返回类型
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取所有声明
     *
     * @param token JWT Token
     * @return 所有声明
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token JWT Token
     * @return 是否已过期
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     *
     * @param token JWT Token
     * @return 剩余有效时间（毫秒）
     */
    public long getExpirationTime(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * 验证 Token 是否有效（用户名匹配且未过期）
     *
     * @param token JWT Token
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ==================== Token 头部处理 ====================

    /**
     * 从请求头中提取 Token
     *
     * @param bearerToken Authorization 头值（Bearer xxx）
     * @return Token 字符串，如果不存在则返回 null
     */
    public String extractTokenFromHeader(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 生成用于响应的 Authorization 头值
     *
     * @param token JWT Token
     * @return Bearer token 字符串
     */
    public String generateBearerToken(String token) {
        return "Bearer " + token;
    }

    // ==================== 配置获取 ====================

    /**
     * 获取 Access Token 有效期
     *
     * @return 有效期（毫秒）
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * 获取 Refresh Token 有效期
     *
     * @return 有效期（毫秒）
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
