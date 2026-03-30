package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户实体类
 * 功能 #3: 用户认证与授权模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 用户状态 ====================
    public enum UserStatus {
        ACTIVE("活跃"),
        INACTIVE("未激活"),
        LOCKED("已锁定"),
        DELETED("已删除");
        
        private final String description;
        
        UserStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private UserStatus status;
    private Set<String> roles;
    private Set<String> permissions;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer loginFailCount;
    private LocalDateTime lockUntil;
    private String passwordSalt;
    
    // ==================== 构造函数 ====================
    public User() {
        this.status = UserStatus.INACTIVE;
        this.createTime = LocalDateTime.now();
        this.loginFailCount = 0;
    }
    
    // ==================== 业务方法 ====================
    
    public boolean isLocked() {
        if (lockUntil != null && lockUntil.isAfter(LocalDateTime.now())) {
            return true;
        }
        return status == UserStatus.LOCKED;
    }
    
    public void incrementLoginFail() {
        this.loginFailCount++;
        if (this.loginFailCount >= 5) {
            this.lockUntil = LocalDateTime.now().plusMinutes(30);
            this.status = UserStatus.LOCKED;
        }
    }
    
    public void resetLoginFail() {
        this.loginFailCount = 0;
        this.lockUntil = null;
        if (this.status == UserStatus.LOCKED) {
            this.status = UserStatus.ACTIVE;
        }
    }
    
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
    
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
    
    // ==================== Getter & Setter ====================
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
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    
    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    
    public Integer getLoginFailCount() { return loginFailCount; }
    public void setLoginFailCount(Integer loginFailCount) { this.loginFailCount = loginFailCount; }
    
    public LocalDateTime getLockUntil() { return lockUntil; }
    public void setLockUntil(LocalDateTime lockUntil) { this.lockUntil = lockUntil; }
    
    public String getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt; }
}
