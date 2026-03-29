package com.im.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 公告阅读记录实体
 * 记录用户阅读公告的状态
 */
@Entity
@Table(name = "announcement_read_records", indexes = {
    @Index(name = "idx_announcement_user", columnList = "announcementId, userId", unique = true),
    @Index(name = "idx_announcement_id", columnList = "announcementId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_read_at", columnList = "readAt")
})
public class AnnouncementReadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 公告ID
     */
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 阅读时间
     */
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    /**
     * 是否已确认
     */
    @Column(name = "confirmed")
    private Boolean confirmed = false;

    /**
     * 确认时间
     */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 设备信息
     */
    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 构造函数
    public AnnouncementReadRecord() {}

    public AnnouncementReadRecord(Long announcementId, Long userId) {
        this.announcementId = announcementId;
        this.userId = userId;
        this.readAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AnnouncementReadRecord{" +
                "id=" + id +
                ", announcementId=" + announcementId +
                ", userId=" + userId +
                ", readAt=" + readAt +
                ", confirmed=" + confirmed +
                '}';
    }
}
