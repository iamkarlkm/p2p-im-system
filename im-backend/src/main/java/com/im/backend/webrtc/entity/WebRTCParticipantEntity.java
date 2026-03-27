package com.im.backend.webrtc;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WebRTC参与者实体
 * 存储音视频通话参与者信息
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webrtc_participants", indexes = {
    @Index(name = "idx_session_id", columnList = "sessionId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_connection_id", columnList = "connectionId"),
    @Index(name = "idx_joined_at", columnList = "joinedAt")
})
public class WebRTCParticipantEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String sessionId;

    @Column(nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 64)
    private String connectionId;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ParticipantStatus status;

    @Column(nullable = false)
    private Boolean isAudioEnabled;

    @Column(nullable = false)
    private Boolean isVideoEnabled;

    @Column(nullable = false)
    private Boolean isScreenSharing;

    @Column(nullable = false)
    private Boolean isHandRaised;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    @Column
    private LocalDateTime leftAt;

    @Column
    private Long duration;

    @Column(length = 100)
    private String ipAddress;

    @Column(length = 200)
    private String userAgent;

    @Column(length = 50)
    private String deviceType;

    @Column(length = 500)
    private String sdpOffer;

    @Column(length = 500)
    private String sdpAnswer;

    @Column(length = 1000)
    private String iceCandidates;

    @Column(nullable = false)
    private Integer audioBitrate;

    @Column(nullable = false)
    private Integer videoBitrate;

    @Column(nullable = false)
    private Integer packetLoss;

    @Column(nullable = false)
    private Integer jitter;

    @Column(nullable = false)
    private Integer roundTripTime;

    @Column(nullable = false)
    private Integer audioLevel;

    @Column(length = 50)
    private String videoResolution;

    @Column(nullable = false)
    private Integer frameRate;

    @Column(length = 1000)
    private String statistics;

    @Column(nullable = false)
    private LocalDateTime lastActiveAt;

    @Column(length = 100)
    private String sfuTrackId;

    @Column(nullable = false)
    private Boolean isHost;

    @Column(length = 36)
    private String invitedBy;

    @Column
    private LocalDateTime invitedAt;

    @Column(length = 500)
    private String metadata;

    /**
     * 参与者角色枚举
     */
    public enum ParticipantRole {
        HOST,            // 主持人
        CO_HOST,         // 联合主持人
        SPEAKER,         // 发言人
        PARTICIPANT,     // 普通参与者
        VIEWER,          // 观看者
        WAITING          // 等待中
    }

    /**
     * 参与者状态枚举
     */
    public enum ParticipantStatus {
        JOINING,         // 加入中
        CONNECTED,       // 已连接
        RECONNECTING,    // 重连中
        MUTED,           // 已静音
        SCREEN_SHARING,  // 屏幕共享中
        DISCONNECTED,    // 已断开
        KICKED,          // 被踢出
        BANNED,          // 被禁止
        LEFT             // 已离开
    }

    /**
     * 持久化前处理
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
        if (this.lastActiveAt == null) {
            this.lastActiveAt = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = ParticipantRole.PARTICIPANT;
        }
        if (this.status == null) {
            this.status = ParticipantStatus.JOINING;
        }
        if (this.isAudioEnabled == null) {
            this.isAudioEnabled = true;
        }
        if (this.isVideoEnabled == null) {
            this.isVideoEnabled = true;
        }
        if (this.isScreenSharing == null) {
            this.isScreenSharing = false;
        }
        if (this.isHandRaised == null) {
            this.isHandRaised = false;
        }
        if (this.isHost == null) {
            this.isHost = false;
        }
        if (this.audioBitrate == null) {
            this.audioBitrate = 128000;
        }
        if (this.videoBitrate == null) {
            this.videoBitrate = 2500000;
        }
        if (this.packetLoss == null) {
            this.packetLoss = 0;
        }
        if (this.jitter == null) {
            this.jitter = 0;
        }
        if (this.roundTripTime == null) {
            this.roundTripTime = 0;
        }
        if (this.audioLevel == null) {
            this.audioLevel = 0;
        }
        if (this.frameRate == null) {
            this.frameRate = 30;
        }
    }

    /**
     * 更新状态
     */
    public void updateStatus(ParticipantStatus newStatus) {
        this.status = newStatus;
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 更新媒体状态
     */
    public void updateMediaState(Boolean audio, Boolean video) {
        if (audio != null) {
            this.isAudioEnabled = audio;
        }
        if (video != null) {
            this.isVideoEnabled = video;
        }
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 开始屏幕共享
     */
    public void startScreenSharing() {
        this.isScreenSharing = true;
        this.status = ParticipantStatus.SCREEN_SHARING;
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 停止屏幕共享
     */
    public void stopScreenSharing() {
        this.isScreenSharing = false;
        if (this.status == ParticipantStatus.SCREEN_SHARING) {
            this.status = ParticipantStatus.CONNECTED;
        }
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 举手
     */
    public void raiseHand() {
        this.isHandRaised = true;
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 放下手
     */
    public void lowerHand() {
        this.isHandRaised = false;
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 离开会话
     */
    public void leave() {
        this.leftAt = LocalDateTime.now();
        this.status = ParticipantStatus.LEFT;
        if (this.joinedAt != null) {
            this.duration = java.time.Duration.between(
                this.joinedAt, this.leftAt).getSeconds();
        }
    }

    /**
     * 被踢出
     */
    public void kick() {
        this.leftAt = LocalDateTime.now();
        this.status = ParticipantStatus.KICKED;
        if (this.joinedAt != null) {
            this.duration = java.time.Duration.between(
                this.joinedAt, this.leftAt).getSeconds();
        }
    }

    /**
     * 更新网络统计
     */
    public void updateStats(Integer packetLoss, Integer jitter, 
                           Integer rtt, Integer audioLevel) {
        if (packetLoss != null) {
            this.packetLoss = packetLoss;
        }
        if (jitter != null) {
            this.jitter = jitter;
        }
        if (rtt != null) {
            this.roundTripTime = rtt;
        }
        if (audioLevel != null) {
            this.audioLevel = audioLevel;
        }
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 更新视频信息
     */
    public void updateVideoInfo(String resolution, Integer frameRate, 
                                 Integer bitrate) {
        if (resolution != null) {
            this.videoResolution = resolution;
        }
        if (frameRate != null) {
            this.frameRate = frameRate;
        }
        if (bitrate != null) {
            this.videoBitrate = bitrate;
        }
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 设置ICE候选
     */
    public void addIceCandidate(String candidate) {
        if (this.iceCandidates == null || this.iceCandidates.isEmpty()) {
            this.iceCandidates = candidate;
        } else {
            this.iceCandidates += ";" + candidate;
        }
    }

    /**
     * 检查是否在线
     */
    public boolean isOnline() {
        return this.status == ParticipantStatus.CONNECTED ||
               this.status == ParticipantStatus.MUTED ||
               this.status == ParticipantStatus.SCREEN_SHARING;
    }

    /**
     * 检查是否可以发言
     */
    public boolean canSpeak() {
        return this.status == ParticipantStatus.CONNECTED ||
               this.status == ParticipantStatus.MUTED ||
               this.status == ParticipantStatus.SCREEN_SHARING;
    }

    /**
     * 设置为主持人
     */
    public void setAsHost() {
        this.isHost = true;
        this.role = ParticipantRole.HOST;
    }

    /**
     * 降级为普通参与者
     */
    public void demoteToParticipant() {
        this.isHost = false;
        if (this.role == ParticipantRole.HOST) {
            this.role = ParticipantRole.PARTICIPANT;
        }
    }
}
