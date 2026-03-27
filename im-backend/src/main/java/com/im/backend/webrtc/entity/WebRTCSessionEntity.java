package com.im.backend.webrtc;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WebRTC会话实体
 * 存储音视频通话会话信息
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webrtc_sessions", indexes = {
    @Index(name = "idx_room_id", columnList = "roomId"),
    @Index(name = "idx_host_id", columnList = "hostId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class WebRTCSessionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    private String roomId;

    @Column(nullable = false, length = 36)
    private String hostId;

    @Column(length = 36)
    private String hostConnectionId;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionType sessionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType mediaType;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Column(nullable = false)
    private Boolean isRecording;

    @Column(nullable = false)
    private Boolean isScreenSharing;

    @Column(length = 255)
    private String recordingUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Column
    private Long duration;

    @Column(nullable = false)
    private String sfuNodeId;

    @Column(length = 500)
    private String stunServers;

    @Column(length = 500)
    private String turnServers;

    @Column(nullable = false)
    private Boolean enableSimulcast;

    @Column(nullable = false)
    private Boolean enableTcc;

    @Column(nullable = false)
    private Boolean enableRemb;

    @Column(nullable = false)
    private Integer videoBitrate;

    @Column(nullable = false)
    private Integer audioBitrate;

    @Column(length = 100)
    private String videoCodec;

    @Column(length = 100)
    private String audioCodec;

    @Column(nullable = false)
    private Boolean requirePassword;

    @Column(length = 64)
    private String passwordHash;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(length = 36)
    private String scheduledMeetingId;

    @Column(length = 1000)
    private String metadata;

    /**
     * 会话类型枚举
     */
    public enum SessionType {
        ONE_TO_ONE,      // 一对一通话
        GROUP_CALL,      // 群组通话
        CONFERENCE,      // 会议模式
        BROADCAST,       // 直播/广播
        WEBINAR          // 网络研讨会
    }

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        PENDING,         // 等待中
        CONNECTING,      // 连接中
        ACTIVE,          // 活跃中
        PAUSED,          // 暂停
        RECONNECTING,    // 重连中
        ENDED,           // 已结束
        FAILED           // 失败
    }

    /**
     * 媒体类型枚举
     */
    public enum MediaType {
        AUDIO_ONLY,      // 仅音频
        VIDEO_ONLY,      // 仅视频
        AUDIO_VIDEO,     // 音视频
        SCREEN_SHARE     // 屏幕共享
    }

    /**
     * 生成新会话ID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.roomId == null) {
            this.roomId = generateRoomId();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
        if (this.currentParticipants == null) {
            this.currentParticipants = 0;
        }
        if (this.isRecording == null) {
            this.isRecording = false;
        }
        if (this.isScreenSharing == null) {
            this.isScreenSharing = false;
        }
        if (this.enableSimulcast == null) {
            this.enableSimulcast = true;
        }
        if (this.enableTcc == null) {
            this.enableTcc = true;
        }
        if (this.enableRemb == null) {
            this.enableRemb = true;
        }
        if (this.videoBitrate == null) {
            this.videoBitrate = 2500000;
        }
        if (this.audioBitrate == null) {
            this.audioBitrate = 128000;
        }
        if (this.requirePassword == null) {
            this.requirePassword = false;
        }
        if (this.isPublic == null) {
            this.isPublic = true;
        }
    }

    /**
     * 生成房间ID
     */
    private String generateRoomId() {
        return "room-" + System.currentTimeMillis() + "-" + 
               (int)(Math.random() * 10000);
    }

    /**
     * 更新统计信息
     */
    public void updateParticipantCount(int delta) {
        this.currentParticipants = Math.max(0, this.currentParticipants + delta);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 开始会话
     */
    public void start() {
        this.status = SessionStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 结束会话
     */
    public void end() {
        this.status = SessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.duration = java.time.Duration.between(
                this.startedAt, this.endedAt).getSeconds();
        }
    }

    /**
     * 检查是否可以加入
     */
    public boolean canJoin() {
        return this.status == SessionStatus.ACTIVE || 
               this.status == SessionStatus.PENDING ||
               this.status == SessionStatus.CONNECTING;
    }

    /**
     * 检查是否已满
     */
    public boolean isFull() {
        return this.currentParticipants >= this.maxParticipants;
    }

    /**
     * 开始录制
     */
    public void startRecording(String url) {
        this.isRecording = true;
        this.recordingUrl = url;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        this.isRecording = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 开始屏幕共享
     */
    public void startScreenSharing() {
        this.isScreenSharing = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停止屏幕共享
     */
    public void stopScreenSharing() {
        this.isScreenSharing = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String password) {
        if (!this.requirePassword) {
            return true;
        }
        if (password == null || this.passwordHash == null) {
            return false;
        }
        // 使用SHA-256验证密码
        try {
            java.security.MessageDigest digest = 
                java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            String hashedInput = bytesToHex(hash);
            return hashedInput.equals(this.passwordHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 设置密码
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            this.requirePassword = false;
            this.passwordHash = null;
            return;
        }
        try {
            java.security.MessageDigest digest = 
                java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            this.passwordHash = bytesToHex(hash);
            this.requirePassword = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
