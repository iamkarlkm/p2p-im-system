package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 推送通知实体 - 离线消息推送
 * 支持 APNs/FCM 推送、消息同步、推送静默
 */
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
public class PushNotificationEntity {

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
    @Column
    private Integer badge;

    /** 自定义数据 (JSON) */
    @Column(columnDefinition = "TEXT")
    private String customData;

    /** 过期时间 (TTL) */
    @Column
    private LocalDateTime expiresAt;

    /** 定时发送时间 */
    @Column
    private LocalDateTime scheduledAt;

    /** 实际发送时间 */
    @Column
    private LocalDateTime sentAt;

    /** 送达时间 */
    @Column
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
    @Column
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public String getPushType() { return pushType; }
    public void setPushType(String pushType) { this.pushType = pushType; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public Boolean getIsSilent() { return isSilent; }
    public void setIsSilent(Boolean isSilent) { this.isSilent = isSilent; }

    public String getSilentType() { return silentType; }
    public void setSilentType(String silentType) { this.silentType = silentType; }

    public Integer getBadge() { return badge; }
    public void setBadge(Integer badge) { this.badge = badge; }

    public String getCustomData() { return customData; }
    public void setCustomData(String customData) { this.customData = customData; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getApnsId() { return apnsId; }
    public void setApnsId(String apnsId) { this.apnsId = apnsId; }

    public String getFcmMessageId() { return fcmMessageId; }
    public void setFcmMessageId(String fcmMessageId) { this.fcmMessageId = fcmMessageId; }

    public String getCollapseKey() { return collapseKey; }
    public void setCollapseKey(String collapseKey) { this.collapseKey = collapseKey; }
}
