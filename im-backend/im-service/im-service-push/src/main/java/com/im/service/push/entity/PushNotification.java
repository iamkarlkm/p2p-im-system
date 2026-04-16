package com.im.service.push.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推送通知实体 - 离线消息推送
 * 支持 APNs/FCM/HMS 推送、消息同步、推送静默
 */
@Data
@Entity
@Table(name = "im_push_notification",
       indexes = {
           @Index(name = "idx_push_user", columnList = "userId"),
           @Index(name = "idx_push_device", columnList = "deviceId"),
           @Index(name = "idx_push_status", columnList = "status"),
           @Index(name = "idx_push_scheduled", columnList = "scheduledAt"),
           @Index(name = "idx_push_created", columnList = "createdAt"),
           @Index(name = "idx_push_batch", columnList = "batchId")
       })
public class PushNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 推送记录ID */
    @Column(nullable = false, unique = true, length = 36)
    private String notificationId;

    /** 目标用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 目标设备ID */
    @Column(length = 36)
    private String deviceId;

    /** 设备令牌 (APNs/FCM token) */
    @Column(length = 256)
    private String deviceToken;

    /** 推送类型: APNS, FCM, HMS */
    @Column(length = 20)
    private String pushType;

    /** 通知类型: MESSAGE, CALL, MENTION, SYSTEM, CUSTOM */
    @Column(nullable = false, length = 20)
    private String notificationType;

    /** 推送状态: PENDING, SENT, DELIVERED, FAILED, EXPIRED, SILENCED */
    @Column(nullable = false, length = 20)
    private String status;

    /** 推送优先级: HIGH, NORMAL, LOW */
    @Column(nullable = false, length = 10)
    private String priority;

    /** 推送标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 推送内容 */
    @Column(nullable = false, length = 500)
    private String body;

    /** 消息ID (关联的消息) */
    @Column(length = 36)
    private String messageId;

    /** 会话ID */
    @Column(length = 36)
    private String conversationId;

    /** 发送者用户ID */
    @Column(length = 36)
    private String senderId;

    /** 发送者名称 */
    @Column(length = 100)
    private String senderName;

    /** 发送者头像 */
    @Column(length = 500)
    private String senderAvatar;

    /** 静默推送 (无横幅) */
    @Column(nullable = false)
    private Boolean isSilent;

    /** 静默类型: VOIP, DATA, NONE */
    @Column(length = 20)
    private String silentType;

    /** 角标/未读数 */
    private Integer badge;

    /** 自定义数据 (JSON) */
    @Column(columnDefinition = "TEXT")
    private String customData;

    /** 过期时间 (TTL) */
    private LocalDateTime expiresAt;

    /** 定时发送时间 */
    private LocalDateTime scheduledAt;

    /** 实际发送时间 */
    private LocalDateTime sentAt;

    /** 送达时间 */
    private LocalDateTime deliveredAt;

    /** 失败原因 */
    @Column(length = 500)
    private String failureReason;

    /** 重试次数 */
    @Column(nullable = false)
    private Integer retryCount;

    /** 最大重试次数 */
    @Column(nullable = false)
    private Integer maxRetries;

    /** 批量ID (批量推送) */
    @Column(length = 36)
    private String batchId;

    /** 发送来源: SERVER, BOT, SYSTEM */
    @Column(length = 20)
    private String source;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** APNs 请求ID */
    @Column(length = 64)
    private String apnsId;

    /** FCM 消息ID */
    @Column(length = 64)
    private String fcmMessageId;

    /** 折叠键 (APNs collapse_key) */
    @Column(length = 64)
    private String collapseKey;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
