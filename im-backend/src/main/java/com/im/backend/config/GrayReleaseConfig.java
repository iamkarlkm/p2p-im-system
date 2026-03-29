package com.im.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 灰度发布配置
 * 支持基于 Header 的流量染色和路由
 */
@Slf4j
@Configuration
@Profile({"kubernetes", "gray-release"})
public class GrayReleaseConfig {

    @Value("${im.grayrelease.enabled:false}")
    private boolean grayReleaseEnabled;

    @Value("${im.grayrelease.header.name:X-Gray-Release}")
    private String grayReleaseHeaderName;

    @Value("${im.grayrelease.header.value:gray}")
    private String grayReleaseHeaderValue;

    @Value("${im.grayrelease.percentage:0}")
    private int grayReleasePercentage;

    @Value("${im.grayrelease.whitelist-users:}")
    private List<String> whitelistUsers;

    @Value("${im.apollo.enabled:false}")
    private boolean apolloEnabled;

    @Value("${im.apollo.app.id:im-backend}")
    private String apolloAppId;

    @Value("${im.apollo.meta.server:http://apollo-meta-server:8080}")
    private String apolloMetaServer;

    @Value("${im.apollo.namespace:application}")
    private String apolloNamespace;

    /**
     * 灰度发布过滤器
     * 根据 Header、用户 ID、百分比等规则进行流量染色
     */
    @Bean
    public OncePerRequestFilter grayReleaseFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain) throws ServletException, IOException {
                
                if (!grayReleaseEnabled) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String grayValue = determineGrayValue(request);
                
                if (grayValue != null) {
                    // 设置灰度标记到请求属性
                    request.setAttribute("grayRelease", true);
                    request.setAttribute("grayValue", grayValue);
                    
                    // 在响应头中返回灰度信息
                    response.setHeader("X-Gray-Release", grayValue);
                    
                    log.debug("Gray release detected for request: {}, grayValue: {}", 
                            request.getRequestURI(), grayValue);
                }

                filterChain.doFilter(request, response);
            }

            private String determineGrayValue(HttpServletRequest request) {
                // 1. 检查 Header
                String headerValue = request.getHeader(grayReleaseHeaderName);
                if (headerValue != null && headerValue.equals(grayReleaseHeaderValue)) {
                    return "gray";
                }

                // 2. 检查白名单用户
                String userId = request.getHeader("X-User-ID");
                if (userId != null && whitelistUsers.contains(userId)) {
                    return "gray";
                }

                // 3. 基于百分比的灰度
                if (grayReleasePercentage > 0) {
                    int hashCode = Math.abs(request.getRemoteAddr().hashCode() % 100);
                    if (hashCode < grayReleasePercentage) {
                        return "gray";
                    }
                }

                return null;
            }
        };
    }

    /**
     * 灰度发布规则管理器
     */
    @Bean
    public GrayReleaseRuleManager grayReleaseRuleManager() {
        return new GrayReleaseRuleManager(
                grayReleaseEnabled,
                grayReleasePercentage,
                whitelistUsers
        );
    }

    /**
     * Apollo 配置中心集成
     */
    @Bean
    @Profile("apollo")
    public ApolloConfigListener apolloConfigListener() {
        return new ApolloConfigListener(apolloAppId, apolloMetaServer, apolloNamespace);
    }
}
