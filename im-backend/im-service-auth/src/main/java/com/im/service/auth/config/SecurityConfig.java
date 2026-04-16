package com.im.service.auth.config;

import com.im.service.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 配置类
 * 
 * 功能特性：
 * 1. JWT Token 认证配置
 * 2. 密码加密配置（BCrypt）
 * 3. URL 访问控制规则
 * 4. CORS 跨域配置
 * 5. 方法级安全注解支持
 * 6. 无状态会话管理
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,      // 启用 @PreAuthorize 和 @PostAuthorize
        securedEnabled = true,      // 启用 @Secured
        jsr250Enabled = true        // 启用 @RolesAllowed
)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * 白名单路径 - 允许匿名访问
     */
    private static final String[] WHITE_LIST_URLS = {
            // 认证相关
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/verify-email",
            "/api/auth/resend-verification",
            
            // 公开接口
            "/api/public/**",
            
            // Swagger/OpenAPI 文档
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
            
            // 健康检查
            "/actuator/health",
            "/actuator/info",
            
            // WebSocket 端点
            "/ws/**",
            "/ws/sockjs/**",
            
            // 错误页面
            "/error",
            "/favicon.ico"
    };

    /**
     * 配置安全过滤器链
     *
     * @param http HttpSecurity 配置
     * @return SecurityFilterChain 过滤器链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
            .csrf().disable()
            
            // 配置 CORS
            .cors().configurationSource(corsConfigurationSource())
            .and()
            
            // 配置会话管理 - 无状态（不创建 Session）
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            
            // 配置认证提供者
            .authenticationProvider(authenticationProvider())
            
            // 添加 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 配置授权规则
            .authorizeRequests()
            
            // 白名单路径允许匿名访问
            .antMatchers(WHITE_LIST_URLS).permitAll()
            
            // OPTIONS 请求允许所有
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            
            // 静态资源允许访问
            .antMatchers("/", "/static/**", "/resources/**").permitAll()
            
            // 管理员接口需要 ADMIN 角色
            .antMatchers("/api/admin/**").hasRole("ADMIN")
            
            // 用户相关接口需要认证
            .antMatchers("/api/users/**", "/api/friends/**", "/api/messages/**").authenticated()
            
            // 群组相关接口需要认证
            .antMatchers("/api/groups/**").authenticated()
            
            // 文件上传下载需要认证
            .antMatchers("/api/files/**").authenticated()
            
            // 其他所有请求需要认证
            .anyRequest().authenticated();

        return http.build();
    }

    /**
     * 配置密码编码器
     * 使用 BCrypt 强哈希算法
     *
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证提供者
     * 使用 DAO 认证提供者，配置 UserDetailsService 和 PasswordEncoder
     *
     * @return AuthenticationProvider 实例
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 配置认证管理器
     *
     * @param config AuthenticationConfiguration 配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置 CORS 跨域
     *
     * @return CorsConfigurationSource 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（实际项目中应该配置具体的域名）
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",      // React 前端
                "http://localhost:8080",      // Vue 前端
                "http://localhost:4200",      // Angular 前端
                "http://127.0.0.1:3000",
                "http://127.0.0.1:8080",
                "https://*.im-modular.com"    // 生产环境域名
        ));
        
        // 允许的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Device-Id",
                "X-Client-Version"
        ));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "X-Auth-Token",
                "X-Refresh-Token"
        ));
        
        // 允许携带凭证（cookies）
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间（秒）
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
