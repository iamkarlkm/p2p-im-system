package com.im.backend.webrtc;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WebRTC信令消息实体
 * 存储SDP offer/answer和ICE candidate等信令数据
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webrtc_signaling", indexes = {
    @Index(name = "idx_session_id_sig", columnList = "sessionId"),
    @Index(name = "idx_from_user", columnList = "fromUserId"),
    @Index(name = "idx_to_user", columnList = "toUserId"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_created_at_sig", columnList = "createdAt")
})
public class WebRTCSignalingEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 36)
    private String sessionId;

    @Column(nullable = false, length = 36)
    private String fromUserId;

    @Column(length = 36)
    private String toUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SignalType type;

    @Column(nullable = false, length = 4000)
    private String payload;

    @Column(length = 100)
    private String sdpType;

    @Column(length = 4000)
    private String sdp;

    @Column(length = 500)
    private String iceCandidate;

    @Column(length = 50)
    private String iceSdpMid;

    @Column
    private Integer iceSdpMLineIndex;

    @Column(length = 200)
    private String iceUsernameFragment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SignalStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deliveredAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private Integer retryCount;

    @Column(length = 500)
    private String errorMessage;

    @Column(length = 100)
    private String connectionId;

    @Column(length = 50)
    private String trackId;

    @Column(length = 500)
    private String metadata;

    /**
     * 信令类型枚举
     */
    public enum SignalType {
        // SDP协商
        OFFER,                  // SDP offer
        ANSWER,                 // SDP answer
        PRANSWER,              // 渐进式answer
        ROLLBACK,              // 回滚

        // ICE候选
        ICE_CANDIDATE,          // ICE candidate
        ICE_CANDIDATE_REMOVE,   // 移除ICE candidate
        ICE_RESTART,           // ICE重启

        // 会话控制
        JOIN,                  // 加入会话
        LEAVE,                 // 离开会话
        INVITE,                // 邀请参与者
        KICK,                  // 踢出参与者
        REJECT,                // 拒绝邀请

        // 媒体控制
        MUTE_AUDIO,            // 静音音频
        UNMUTE_AUDIO,          // 取消静音
        MUTE_VIDEO,            // 关闭视频
        UNMUTE_VIDEO,          // 开启视频
        START_SCREEN_SHARE,    // 开始屏幕共享
        STOP_SCREEN_SHARE,     // 停止屏幕共享

        // 状态同步
        PARTICIPANT_JOINED,    // 参与者加入
        PARTICIPANT_LEFT,      // 参与者离开
        PARTICIPANT_UPDATED,   // 参与者信息更新
        ACTIVE_SPEAKER,        // 活跃发言人
        DOMINANT_SPEAKER,      // 主导发言人

        // 质量反馈
        QUALITY_STATS,         // 质量统计
        BANDWIDTH_ESTIMATE,    // 带宽估计
        LAYER_CHANGE,          // 层切换 (simulcast)

        // 错误处理
        ERROR,                 // 错误
        RECONNECT,             // 重连请求
        RECONNECTED,          // 重连成功
        PING,                  // 心跳
        PONG                   // 心跳响应
    }

    /**
     * 信令状态枚举
     */
    public enum SignalStatus {
        PENDING,               // 待发送
        SENDING,              // 发送中
        DELIVERED,            // 已送达
        PROCESSED,            // 已处理
        FAILED,               // 失败
        EXPIRED               // 过期
    }

    /**
     * 持久化前处理
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = SignalStatus.PENDING;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }

    /**
     * 标记为已送达
     */
    public void markDelivered() {
        this.status = SignalStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * 标记为已处理
     */
    public void markProcessed() {
        this.status = SignalStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * 标记为失败
     */
    public void markFailed(String error) {
        this.status = SignalStatus.FAILED;
        this.errorMessage = error;
        this.retryCount++;
    }

    /**
     * 标记为过期
     */
    public void markExpired() {
        this.status = SignalStatus.EXPIRED;
    }

    /**
     * 创建SDP offer消息
     */
    public static WebRTCSignalingEntity createOffer(String sessionId, 
                                                     String fromUserId,
                                                     String toUserId,
                                                     String sdp) {
        return WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .type(SignalType.OFFER)
            .sdpType("offer")
            .sdp(sdp)
            .payload(sdp)
            .sdpType("offer")
            .build();
    }

    /**
     * 创建SDP answer消息
     */
    public static WebRTCSignalingEntity createAnswer(String sessionId,
                                                      String fromUserId,
                                                      String toUserId,
                                                      String sdp) {
        return WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .type(SignalType.ANSWER)
            .sdpType("answer")
            .sdp(sdp)
            .payload(sdp)
            .build();
    }

    /**
     * 创建ICE candidate消息
     */
    public static WebRTCSignalingEntity createIceCandidate(String sessionId,
                                                            String fromUserId,
                                                            String toUserId,
                                                            String candidate,
                                                            String sdpMid,
                                                            Integer sdpMLineIndex) {
        return WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(fromUserId)
            .toUserId(toUserId)
            .type(SignalType.ICE_CANDIDATE)
            .iceCandidate(candidate)
            .iceSdpMid(sdpMid)
            .iceSdpMLineIndex(sdpMLineIndex)
            .payload(candidate)
            .build();
    }

    /**
     * 创建参与者加入消息
     */
    public static WebRTCSignalingEntity createParticipantJoined(String sessionId,
                                                                 String participantId,
                                                                 String metadata) {
        return WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(participantId)
            .type(SignalType.PARTICIPANT_JOINED)
            .payload(metadata)
            .build();
    }

    /**
     * 创建参与者离开消息
     */
    public static WebRTCSignalingEntity createParticipantLeft(String sessionId,
                                                               String participantId,
                                                               String reason) {
        return WebRTCSignalingEntity.builder()
            .sessionId(sessionId)
            .fromUserId(participantId)
            .type(SignalType.PARTICIPANT_LEFT)
            .payload(reason)
            .build();
    }

    /**
     * 是否需要重试
     */
    public boolean shouldRetry() {
        return this.status == SignalStatus.FAILED && 
               this.retryCount < 3;
    }

    /**
     * 是否已过期
     */
    public boolean isExpired(int timeoutSeconds) {
        if (this.status == SignalStatus.DELIVERED || 
            this.status == SignalStatus.PROCESSED) {
            return false;
        }
        LocalDateTime expiryTime = this.createdAt.plusSeconds(timeoutSeconds);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * 获取ICE candidate JSON
     */
    public String getIceCandidateJson() {
        if (this.iceCandidate == null) {
            return null;
        }
        return String.format(
            "{\"candidate\":\"%s\",\"sdpMid\":\"%s\",\"sdpMLineIndex\":%d}",
            this.iceCandidate,
            this.iceSdpMid != null ? this.iceSdpMid : "",
            this.iceSdpMLineIndex != null ? this.iceSdpMLineIndex : 0
        );
    }

    /**
     * 获取SDP JSON
     */
    public String getSdpJson() {
        if (this.sdp == null) {
            return null;
        }
        return String.format(
            "{\"type\":\"%s\",\"sdp\":\"%s\"}",
            this.sdpType,
            this.sdp.replace("\"", "\\\"")
        );
    }
}
