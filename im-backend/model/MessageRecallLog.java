package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息撤回日志实体
 * 记录所有消息撤回操作的审计日志
 */
@Entity
@Table(name = "message_recall_logs", indexes = {
    @Index(name = "idx_recall_msg_id", columnList = "messageId"),
    @Index(name = "idx_recall_conversation", columnList = "conversationId"),
    @Index(name = "idx_recall_by", columnList = "recalledBy"),
    @Index(name = "idx_recall_time", columnList = "recallTime")
})
public class MessageRecallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 被撤回的消息ID */
    @Column(nullable = false)
    private Long messageId;

    /** 会话ID */
    @Column(nullable = false)
    private Long conversationId;

    /** 原消息发送者ID */
    @Column(nullable = false)
    private Long senderId;

    /** 执行撤回操作的用户ID */
    @Column(nullable = false)
    private Long recalledBy;

    /** 撤回时间 */
    @Column(nullable = false)
    private LocalDateTime recallTime;

    /** 撤回类型: USER-用户撤回, ADMIN-管理员撤回 */
    @Column(length = 20, nullable = false)
    private String recallType;

    /** 原消息内容（加密存储） */
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;

    /** 消息类型 */
    @Column(length = 50)
    private String messageType;

    /** 撤回原因（可选） */
    @Column(length = 500)
    private String reason;

    /** 用户代理信息 */
    @Column(length = 500)
    private String userAgent;

    /** IP地址 */
    @Column(length = 50)
    private String ipAddress;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== 生命周期回调 ==========

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recallTime == null) {
            recallTime = LocalDateTime.now();
        }
    }

    // ========== Getters & Setters ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecalledBy() {
        return recalledBy;
    }

    public void setRecalledBy(Long recalledBy) {
        this.recalledBy = recalledBy;
    }

    public LocalDateTime getRecallTime() {
        return recallTime;
    }

    public void setRecallTime(LocalDateTime recallTime) {
        this.recallTime = recallTime;
    }

    public String getRecallType() {
        return recallType;
    }

    public void setRecallType(String recallType) {
        this.recallType = recallType;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ========== 便捷方法 ==========

    /**
     * 判断是否为管理员撤回
     */
    public boolean isAdminRecall() {
        return "ADMIN".equals(recallType);
    }

    /**
     * 判断是否为发送者自己撤回
     */
    public boolean isSelfRecall() {
        return senderId.equals(recalledBy);
    }

    @Override
    public String toString() {
        return "MessageRecallLog{" +
            "id=" + id +
            ", messageId=" + messageId +
            ", conversationId=" + conversationId +
            ", senderId=" + senderId +
            ", recalledBy=" + recalledBy +
            ", recallTime=" + recallTime +
            ", recallType='" + recallType + '\'' +
            '}';
    }
}
