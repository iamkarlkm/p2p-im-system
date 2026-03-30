package com.im.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 认证服务
 * 功能 #3: 用户认证与授权模块 - 用户登录注册
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 模拟用户存储（实际项目使用数据库）
    private final Map<String, UserInfo> userStore = new HashMap<>();
    
    // 在线用户会话
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    
    /**
     * 用户注册
     */
    public AuthResult register(String username, String password, String email, String phone) {
        // 检查用户名是否已存在
        if (userStore.containsKey(username)) {
            return AuthResult.failure("Username already exists");
        }
        
        // 创建用户
        UserInfo user = new UserInfo();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhone(phone);
        user.setCreateTime(new Date());
        user.setRoles(Arrays.asList("USER"));
        
        userStore.put(username, user);
        
        logger.info("User registered: username={}", username);
        return AuthResult.success(user.getUserId(), "Registration successful");
    }
    
    /**
     * 用户登录
     */
    public AuthResult login(String username, String password, String deviceId, String deviceType) {
        UserInfo user = userStore.get(username);
        if (user == null) {
            return AuthResult.failure("Invalid username or password");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AuthResult.failure("Invalid username or password");
        }
        
        // 生成令牌
        String accessToken = jwtTokenProvider.generateAccessToken(
            user.getUserId(), user.getUsername(), user.getRoles());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        
        // 创建会话
        UserSession session = new UserSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(user.getUserId());
        session.setDeviceId(deviceId);
        session.setDeviceType(deviceType);
        session.setLoginTime(new Date());
        session.setAccessToken(accessToken);
        session.setRefreshToken(refreshToken);
        
        activeSessions.put(session.getSessionId(), session);
        
        // 更新用户最后登录
        user.setLastLoginTime(new Date());
        
        logger.info("User logged in: username={}, device={}", username, deviceType);
        
        return AuthResult.success(user.getUserId(), accessToken, refreshToken, user.getRoles());
    }
    
    /**
     * 用户登出
     */
    public boolean logout(String sessionId) {
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            logger.info("User logged out: sessionId={}", sessionId);
            return true;
        }
        return false;
    }
    
    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
    
    /**
     * 从令牌获取用户ID
     */
    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
    
    /**
     * 刷新令牌
     */
    public AuthResult refreshToken(String refreshToken) {
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            return AuthResult.success(userId, newAccessToken, refreshToken, null);
        }
        return AuthResult.failure("Invalid refresh token");
    }
    
    /**
     * 修改密码
     */
    public AuthResult changePassword(String userId, String oldPassword, String newPassword) {
        UserInfo user = findUserById(userId);
        if (user == null) {
            return AuthResult.failure("User not found");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return AuthResult.failure("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        
        // 使所有会话失效
        invalidateAllUserSessions(userId);
        
        logger.info("Password changed: userId={}", userId);
        return AuthResult.success(userId, "Password changed successfully");
    }
    
    /**
     * 获取用户会话
     */
    public List<UserSession> getUserSessions(String userId) {
        List<UserSession> sessions = new ArrayList<>();
        for (UserSession session : activeSessions.values()) {
            if (session.getUserId().equals(userId)) {
                sessions.add(session);
            }
        }
        return sessions;
    }
    
    /**
     * 获取在线用户数量
     */
    public int getOnlineUserCount() {
        return (int) activeSessions.values().stream()
            .map(UserSession::getUserId)
            .distinct()
            .count();
    }
    
    /**
     * 根据ID查找用户
     */
    private UserInfo findUserById(String userId) {
        return userStore.values().stream()
            .filter(u -> u.getUserId().equals(userId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 使用户所有会话失效
     */
    private void invalidateAllUserSessions(String userId) {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));
    }
    
    // ==================== 内部类 ====================
    
    public static class UserInfo {
        private String userId;
        private String username;
        private String password;
        private String email;
        private String phone;
        private Date createTime;
        private Date lastLoginTime;
        private List<String> roles;
        
        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Date getCreateTime() { return createTime; }
        public void setCreateTime(Date createTime) { this.createTime = createTime; }
        public Date getLastLoginTime() { return lastLoginTime; }
        public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
    
    public static class UserSession {
        private String sessionId;
        private String userId;
        private String deviceId;
        private String deviceType;
        private Date loginTime;
        private String accessToken;
        private String refreshToken;
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public Date getLoginTime() { return loginTime; }
        public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
    
    public static class AuthResult {
        private boolean success;
        private String message;
        private String userId;
        private String accessToken;
        private String refreshToken;
        private List<String> roles;
        
        public static AuthResult success(String userId, String message) {
            AuthResult result = new AuthResult();
            result.success = true;
            result.userId = userId;
            result.message = message;
            return result;
        }
        
        public static AuthResult success(String userId, String accessToken, String refreshToken, List<String> roles) {
            AuthResult result = new AuthResult();
            result.success = true;
            result.userId = userId;
            result.accessToken = accessToken;
            result.refreshToken = refreshToken;
            result.roles = roles;
            return result;
        }
        
        public static AuthResult failure(String message) {
            AuthResult result = new AuthResult();
            result.success = false;
            result.message = message;
            return result;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getUserId() { return userId; }
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public List<String> getRoles() { return roles; }
    }
}
