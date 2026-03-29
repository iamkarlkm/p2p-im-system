package com.im.message.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 阅后即焚消息实体
 * 支持设置自动销毁时间，阅读后倒计时删除
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "self_destruct_messages")
public class SelfDestructMessage {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "conversation_id", nullable = false, length = 36)
    private String conversationId;

    @Column(name = "sender_id", nullable = false, length = 36)
    private String senderId;

    @Column(name = "receiver_id", nullable = false, length = 36)
    private String receiverId;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    private String messageContent;

    @Column(name = "content_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ContentType contentType = ContentType.TEXT;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds = 10;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "scheduled_destroy_at")
    private LocalDateTime scheduledDestroyAt;

    @Column(name = "is_destroyed", nullable = false)
    private Boolean isDestroyed = false;

    @Column(name = "destroyed_at")
    private LocalDateTime destroyedAt;

    @Column(name = "screenshot_detected", nullable = false)
    private Boolean screenshotDetected = false;

    @Column(name = "screenshot_detected_at")
    private LocalDateTime screenshotDetectedAt;

    @Column(name = "screenshot_count", nullable = false)
    private Integer screenshotCount = 0;

    @Column(name = "allow_forward", nullable = false)
    private Boolean allowForward = false;

    @Column(name = "allow_screenshot", nullable = false)
    private Boolean allowScreenshot = false;

    @Column(name = "blur_preview", nullable = false)
    private Boolean blurPreview = true;

    @Column(name = "notification_message", length = 100)
    private String notificationMessage = "你收到了一条阅后即焚消息";

    @Column(name = "encryption_key", length = 256)
    private String encryptionKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ContentType {
        TEXT, IMAGE, VIDEO, AUDIO, FILE, LOCATION
    }

    public SelfDestructMessage() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public ContentType getContentType() { return contentType; }
    public void setContentType(ContentType contentType) { this.contentType = contentType; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { 
        if (durationSeconds < 3) durationSeconds = 3;
        if (durationSeconds > 60) durationSeconds = 60;
        this.durationSeconds = durationSeconds; 
    }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getScheduledDestroyAt() { return scheduledDestroyAt; }
    public void setScheduledDestroyAt(LocalDateTime scheduledDestroyAt) { this.scheduledDestroyAt = scheduledDestroyAt; }

    public Boolean getIsDestroyed() { return isDestroyed; }
    public void setIsDestroyed(Boolean isDestroyed) { this.isDestroyed = isDestroyed; }

    public LocalDateTime getDestroyedAt() { return destroyedAt; }
    public void setDestroyedAt(LocalDateTime destroyedAt) { this.destroyedAt = destroyedAt; }

    public Boolean getScreenshotDetected() { return screenshotDetected; }
    public void setScreenshotDetected(Boolean screenshotDetected) { this.screenshotDetected = screenshotDetected; }

    public LocalDateTime getScreenshotDetectedAt() { return screenshotDetectedAt; }
    public void setScreenshotDetectedAt(LocalDateTime screenshotDetectedAt) { this.screenshotDetectedAt = screenshotDetectedAt; }

    public Integer getScreenshotCount() { return screenshotCount; }
    public void setScreenshotCount(Integer screenshotCount) { this.screenshotCount = screenshotCount; }

    public Boolean getAllowForward() { return allowForward; }
    public void setAllowForward(Boolean allowForward) { this.allowForward = allowForward; }

    public Boolean getAllowScreenshot() { return allowScreenshot; }
    public void setAllowScreenshot(Boolean allowScreenshot) { this.allowScreenshot = allowScreenshot; }

    public Boolean getBlurPreview() { return blurPreview; }
    public void setBlurPreview(Boolean blurPreview) { this.blurPreview = blurPreview; }

    public String getNotificationMessage() { return notificationMessage; }
    public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }

    public String getEncryptionKey() { return encryptionKey; }
    public void setEncryptionKey(String encryptionKey) { this.encryptionKey = encryptionKey; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * 标记消息为已读
     */
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
            this.scheduledDestroyAt = this.readAt.plusSeconds(this.durationSeconds);
        }
    }

    /**
     * 标记消息为已销毁
     */
    public void markAsDestroyed() {
        this.isDestroyed = true;
        this.destroyedAt = LocalDateTime.now();
        this.messageContent = null; // 清除内容
    }

    /**
     * 记录截图检测
     */
    public void recordScreenshot() {
        this.screenshotDetected = true;
        this.screenshotCount++;
        this.screenshotDetectedAt = LocalDateTime.now();
    }

    /**
     * 检查消息是否已过期
     */
    public boolean isExpired() {
        if (!this.isRead || this.scheduledDestroyAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.scheduledDestroyAt);
    }

    /**
     * 获取剩余秒数
     */
    public int getRemainingSeconds() {
        if (!this.isRead || this.scheduledDestroyAt == null) {
            return this.durationSeconds;
        }
        long remaining = java.time.Duration.between(LocalDateTime.now(), this.scheduledDestroyAt).getSeconds();
        return Math.max(0, (int) remaining);
    }

    /**
     * 检查是否可以阅读
     */
    public boolean canRead() {
        return !this.isDestroyed;
    }

    @Override
    public String toString() {
        return "SelfDestructMessage{" +
                "id='" + id + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", contentType=" + contentType +
                ", durationSeconds=" + durationSeconds +
                ", isRead=" + isRead +
                ", isDestroyed=" + isDestroyed +
                ", screenshotCount=" + screenshotCount +
                '}';
    }
}
