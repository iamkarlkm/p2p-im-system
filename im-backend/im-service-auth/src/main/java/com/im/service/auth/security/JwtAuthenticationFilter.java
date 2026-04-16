package com.im.service.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 * 
 * 功能特性：
 * 1. 拦截所有请求，提取并验证 JWT Token
 * 2. 设置 Spring Security 上下文认证信息
 * 3. 支持白名单路径跳过认证
 * 4. Token 过期和无效异常处理
 * 5. 支持 Token 黑名单校验
 * 
 * 执行顺序：在 Spring Security 过滤器链中优先执行
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 白名单路径 - 不需要认证
     */
    private static final List<String> WHITE_LIST_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/health",
            "/actuator/info",
            "/ws/**",
            "/ws/sockjs/**",
            "/error"
    );

    /**
     * 路径匹配器
     */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 执行过滤器逻辑
     *
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 检查是否是白名单路径
            if (isWhiteListedPath(request)) {
                log.debug("White listed path accessed: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 从请求中提取 Token
            String jwt = extractJwtFromRequest(request);

            // 3. 如果没有 Token，继续过滤器链（可能是不需认证的请求）
            if (!StringUtils.hasText(jwt)) {
                log.debug("No JWT token found in request: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // 4. 检查 Token 是否在黑名单中
            if (tokenBlacklistService.isBlacklisted(jwt)) {
                log.warn("Token is blacklisted: {}", maskToken(jwt));
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Token has been revoked\"}");
                return;
            }

            // 5. 验证 Token 并设置认证上下文
            if (jwtTokenProvider.validateAccessToken(jwt)) {
                String username = jwtTokenProvider.extractUsername(jwt);
                
                // 检查是否已有认证信息（避免重复认证）
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 验证 Token 是否对应当前用户
                    if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        
                        // 设置安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // 将用户信息存入请求属性，方便后续使用
                        Long userId = jwtTokenProvider.extractUserId(jwt);
                        String deviceId = jwtTokenProvider.extractDeviceId(jwt);
                        request.setAttribute("userId", userId);
                        request.setAttribute("deviceId", deviceId);
                        request.setAttribute("username", username);
                        
                        log.debug("Authenticated user: {}, userId: {}, URI: {}", 
                                username, userId, request.getRequestURI());
                    } else {
                        log.warn("Token is not valid for user: {}", username);
                    }
                }
            } else {
                log.warn("Invalid JWT token for request: {}", request.getRequestURI());
            }

        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Token has expired");
            return;
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            handleAuthenticationError(response, HttpServletResponse.SC_UNAUTHORIZED, 
                    "Invalid token");
            return;
        } catch (Exception e) {
            log.error("Could not set user authentication in security context: {}", e.getMessage());
            handleAuthenticationError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Authentication error");
            return;
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取 JWT Token
     *
     * @param request HTTP 请求
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 也支持从查询参数中获取 Token（用于 WebSocket 等场景）
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * 检查请求路径是否在白名单中
     *
     * @param request HTTP 请求
     * @return 是否在白名单中
     */
    private boolean isWhiteListedPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        // 允许所有 OPTIONS 请求（预检请求）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        return WHITE_LIST_PATHS.stream()
                .anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    /**
     * 处理认证错误
     *
     * @param response HTTP 响应
     * @param status HTTP 状态码
     * @param message 错误消息
     * @throws IOException IO 异常
     */
    private void handleAuthenticationError(HttpServletResponse response, int status, 
                                           String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"code\":%d,\"message\":\"%s\",\"timestamp\":%d}",
                status, message, System.currentTimeMillis()
        ));
    }

    /**
     * 遮罩 Token 用于日志（只显示前10位）
     *
     * @param token JWT Token
     * @return 遮罩后的 Token
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...";
    }

    // ==================== 白名单配置 ====================

    /**
     * 添加白名单路径
     *
     * @param path 路径模式
     */
    public void addWhiteListPath(String path) {
        WHITE_LIST_PATHS.add(path);
        log.info("Added white list path: {}", path);
    }

    /**
     * 移除白名单路径
     *
     * @param path 路径模式
     */
    public void removeWhiteListPath(String path) {
        WHITE_LIST_PATHS.remove(path);
        log.info("Removed white list path: {}", path);
    }
}
