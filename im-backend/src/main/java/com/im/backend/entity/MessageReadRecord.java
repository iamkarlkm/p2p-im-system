package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息阅读记录（用于阅后即焚计时）
 */
@Entity
@Table(name = "message_read_records",
       indexes = {
           @Index(name = "idx_msg_user", columnList = "messageId, userId"),
           @Index(name = "idx_destroy_time", columnList = "scheduledDestroyTime")
       })
public class MessageReadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime readAt;

    /** 计划销毁时间 */
    @Column(nullable = false)
    private LocalDateTime scheduledDestroyTime;

    /** 是否已销毁 */
    @Column(nullable = false)
    private Boolean destroyed = false;

    @Column
    private LocalDateTime destroyedAt;

    /** 已发送过期前提醒 */
    @Column(nullable = false)
    private Boolean preExpireNoticeSent = false;

    @PrePersist
    protected void onCreate() {
        if (readAt == null) readAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getScheduledDestroyTime() { return scheduledDestroyTime; }
    public void setScheduledDestroyTime(LocalDateTime scheduledDestroyTime) { this.scheduledDestroyTime = scheduledDestroyTime; }

    public Boolean getDestroyed() { return destroyed; }
    public void setDestroyed(Boolean destroyed) { this.destroyed = destroyed; }

    public LocalDateTime getDestroyedAt() { return destroyedAt; }
    public void setDestroyedAt(LocalDateTime destroyedAt) { this.destroyedAt = destroyedAt; }

    public Boolean getPreExpireNoticeSent() { return preExpireNoticeSent; }
    public void setPreExpireNoticeSent(Boolean preExpireNoticeSent) { this.preExpireNoticeSent = preExpireNoticeSent; }
}
