package com.im.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息定时撤回实体
 * 支持设置消息在指定时间后自动撤回
 */
@Entity
@Table(name = "scheduled_message_recall", indexes = {
    @Index(name = "idx_scheduled_recall_user_id", columnList = "user_id"),
    @Index(name = "idx_scheduled_recall_message_id", columnList = "message_id"),
    @Index(name = "idx_scheduled_recall_status", columnList = "status"),
    @Index(name = "idx_scheduled_recall_time", columnList = "scheduled_recall_time")
})
public class ScheduledMessageRecall {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;
    
    @Column(name = "conversation_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ConversationType conversationType;
    
    @Column(name = "message_content", length = 2000)
    private String messageContent;
    
    @Column(name = "scheduled_recall_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledRecallTime;
    
    @Column(name = "scheduled_seconds", nullable = false)
    private Integer scheduledSeconds;
    
    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RecallStatus status;
    
    @Column(name = "recall_reason", length = 500)
    private String recallReason;
    
    @Column(name = "notify_receivers")
    private Boolean notifyReceivers;
    
    @Column(name = "custom_notify_message", length = 200)
    private String customNotifyMessage;
    
    @Column(name = "is_cancelable")
    private Boolean isCancelable;
    
    @Column(name = "cancel_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelDeadline;
    
    @Column(name = "executed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 枚举：会话类型
    public enum ConversationType {
        PRIVATE,      // 私聊
        GROUP,        // 群聊
        CHANNEL       // 频道
    }
    
    // 枚举：撤回状态
    public enum RecallStatus {
        PENDING,      // 待执行
        EXECUTED,     // 已执行
        CANCELLED,    // 已取消
        FAILED,       // 执行失败
        EXPIRED       // 已过期
    }
    
    // 构造函数
    public ScheduledMessageRecall() {
        this.status = RecallStatus.PENDING;
        this.notifyReceivers = true;
        this.isCancelable = true;
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
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public ConversationType getConversationType() {
        return conversationType;
    }
    
    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }
    
    public String getMessageContent() {
        return messageContent;
    }
    
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    
    public LocalDateTime getScheduledRecallTime() {
        return scheduledRecallTime;
    }
    
    public void setScheduledRecallTime(LocalDateTime scheduledRecallTime) {
        this.scheduledRecallTime = scheduledRecallTime;
    }
    
    public Integer getScheduledSeconds() {
        return scheduledSeconds;
    }
    
    public void setScheduledSeconds(Integer scheduledSeconds) {
        this.scheduledSeconds = scheduledSeconds;
    }
    
    public RecallStatus getStatus() {
        return status;
    }
    
    public void setStatus(RecallStatus status) {
        this.status = status;
    }
    
    public String getRecallReason() {
        return recallReason;
    }
    
    public void setRecallReason(String recallReason) {
        this.recallReason = recallReason;
    }
    
    public Boolean getNotifyReceivers() {
        return notifyReceivers;
    }
    
    public void setNotifyReceivers(Boolean notifyReceivers) {
        this.notifyReceivers = notifyReceivers;
    }
    
    public String getCustomNotifyMessage() {
        return customNotifyMessage;
    }
    
    public void setCustomNotifyMessage(String customNotifyMessage) {
        this.customNotifyMessage = customNotifyMessage;
    }
    
    public Boolean getIsCancelable() {
        return isCancelable;
    }
    
    public void setIsCancelable(Boolean isCancelable) {
        this.isCancelable = isCancelable;
    }
    
    public LocalDateTime getCancelDeadline() {
        return cancelDeadline;
    }
    
    public void setCancelDeadline(LocalDateTime cancelDeadline) {
        this.cancelDeadline = cancelDeadline;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 业务方法
    
    /**
     * 检查是否可以取消
     */
    public boolean canCancel() {
        if (status != RecallStatus.PENDING) {
            return false;
        }
        if (!isCancelable) {
            return false;
        }
        if (cancelDeadline != null && LocalDateTime.now().isAfter(cancelDeadline)) {
            return false;
        }
        if (scheduledRecallTime != null && LocalDateTime.now().isAfter(scheduledRecallTime)) {
            return false;
        }
        return true;
    }
    
    /**
     * 检查是否到达执行时间
     */
    public boolean isDue() {
        return status == RecallStatus.PENDING 
            && scheduledRecallTime != null 
            && !LocalDateTime.now().isBefore(scheduledRecallTime);
    }
    
    /**
     * 计算剩余秒数
     */
    public long getRemainingSeconds() {
        if (status != RecallStatus.PENDING || scheduledRecallTime == null) {
            return 0;
        }
        java.time.Duration duration = java.time.Duration.between(
            LocalDateTime.now(), scheduledRecallTime);
        return Math.max(0, duration.getSeconds());
    }
    
    /**
     * 标记为已执行
     */
    public void markExecuted() {
        this.status = RecallStatus.EXECUTED;
        this.executedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已取消
     */
    public void markCancelled() {
        this.status = RecallStatus.CANCELLED;
    }
    
    @Override
    public String toString() {
        return "ScheduledMessageRecall{" +
            "id=" + id +
            ", userId=" + userId +
            ", messageId=" + messageId +
            ", status=" + status +
            ", scheduledRecallTime=" + scheduledRecallTime +
            '}';
    }
}
