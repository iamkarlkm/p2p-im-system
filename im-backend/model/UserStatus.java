package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户状态实体
 * 存储用户的在线状态、自定义状态等信息
 */
@Entity
@Table(name = "user_status")
public class UserStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "online_status", nullable = false, length = 50)
    private String onlineStatus = "OFFLINE";

    @Column(name = "custom_status", length = 100)
    private String customStatus;

    @Column(name = "status_message", length = 500)
    private String statusMessage;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "show_last_seen", nullable = false)
    private Boolean showLastSeen = true;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getCustomStatus() {
        return customStatus;
    }

    public void setCustomStatus(String customStatus) {
        this.customStatus = customStatus;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Boolean getShowLastSeen() {
        return showLastSeen;
    }

    public void setShowLastSeen(Boolean showLastSeen) {
        this.showLastSeen = showLastSeen;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
