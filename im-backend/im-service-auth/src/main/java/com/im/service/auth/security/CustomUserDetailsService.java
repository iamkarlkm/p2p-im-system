package com.im.service.auth.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 用户详情服务
 * 
 * 功能特性：
 * 1. 从用户服务加载用户详情
 * 2. 检查用户账号状态（锁定、启用等）
 * 3. 加载用户权限和角色
 * 4. 提供缓存支持接口
 * 
 * 注意：实际项目中需要通过 Feign Client 调用用户服务
 * 
 * @author IM Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * 模拟用户数据存储 - 实际项目中应该调用用户服务
     * 这里使用 Map 模拟数据库查询，实际应该通过 Feign 调用 im-service-user
     */
    private final UserClient userClient;

    /**
     * 根据用户名加载用户详情
     *
     * @param username 用户名
     * @return UserDetails 对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        // 调用用户服务获取用户信息
        UserInfo userInfo = userClient.getUserByUsername(username);
        
        if (userInfo == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // 检查用户状态
        validateUserStatus(userInfo);

        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = buildAuthorities(userInfo);

        log.debug("User loaded successfully: {}, authorities: {}", username, authorities);

        // 构建 Spring Security User 对象
        return User.builder()
                .username(userInfo.getUsername())
                .password(userInfo.getPassword())
                .authorities(authorities)
                .accountExpired(!userInfo.isAccountNonExpired())
                .accountLocked(!userInfo.isAccountNonLocked())
                .credentialsExpired(!userInfo.isCredentialsNonExpired())
                .disabled(!userInfo.isEnabled())
                .build();
    }

    /**
     * 根据用户ID加载用户详情
     *
     * @param userId 用户ID
     * @return UserDetails 对象
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        UserInfo userInfo = userClient.getUserById(userId);
        
        if (userInfo == null) {
            log.warn("User not found with ID: {}", userId);
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        validateUserStatus(userInfo);

        List<SimpleGrantedAuthority> authorities = buildAuthorities(userInfo);

        return User.builder()
                .username(userInfo.getUsername())
                .password(userInfo.getPassword())
                .authorities(authorities)
                .accountExpired(!userInfo.isAccountNonExpired())
                .accountLocked(!userInfo.isAccountNonLocked())
                .credentialsExpired(!userInfo.isCredentialsNonExpired())
                .disabled(!userInfo.isEnabled())
                .build();
    }

    /**
     * 验证用户状态
     *
     * @param userInfo 用户信息
     * @throws UsernameNotFoundException 用户状态异常时抛出
     */
    private void validateUserStatus(UserInfo userInfo) {
        if (!userInfo.isEnabled()) {
            log.warn("User account is disabled: {}", userInfo.getUsername());
            throw new UsernameNotFoundException("User account is disabled");
        }

        if (!userInfo.isAccountNonLocked()) {
            log.warn("User account is locked: {}", userInfo.getUsername());
            throw new UsernameNotFoundException("User account is locked");
        }

        if (!userInfo.isAccountNonExpired()) {
            log.warn("User account has expired: {}", userInfo.getUsername());
            throw new UsernameNotFoundException("User account has expired");
        }
    }

    /**
     * 构建用户权限列表
     *
     * @param userInfo 用户信息
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> buildAuthorities(UserInfo userInfo) {
        List<String> roles = userInfo.getRoles();
        if (roles == null || roles.isEmpty()) {
            // 默认赋予普通用户角色
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return roles.stream()
                .map(role -> {
                    // 确保角色以 "ROLE_" 开头
                    if (!role.startsWith("ROLE_")) {
                        role = "ROLE_" + role;
                    }
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());
    }

    // ==================== 用户信息接口 ====================

    /**
     * 用户信息接口
     * 实际项目中应该调用用户服务
     */
    public interface UserClient {
        UserInfo getUserByUsername(String username);
        UserInfo getUserById(Long userId);
    }

    /**
     * 用户信息数据传输对象
     */
    public static class UserInfo {
        private Long id;
        private String username;
        private String password;
        private List<String> roles;
        private boolean enabled;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public boolean isAccountNonExpired() { return accountNonExpired; }
        public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }
        
        public boolean isAccountNonLocked() { return accountNonLocked; }
        public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
        
        public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
        public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }
    }
}
