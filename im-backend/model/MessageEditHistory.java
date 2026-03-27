package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息编辑历史实体
 * 记录消息编辑的历史版本
 */
@Entity
@Table(name = "message_edit_history", indexes = {
    @Index(name = "idx_edit_msg_id", columnList = "messageId"),
    @Index(name = "idx_edit_conversation", columnList = "conversationId"),
    @Index(name = "idx_edit_by", columnList = "editedBy"),
    @Index(name = "idx_edit_time", columnList = "editTime"),
    @Index(name = "idx_edit_version", columnList = "messageId, editVersion")
})
public class MessageEditHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 被编辑的消息ID */
    @Column(nullable = false)
    private Long messageId;

    /** 会话ID */
    @Column(nullable = false)
    private Long conversationId;

    /** 原消息发送者ID */
    @Column(nullable = false)
    private Long senderId;

    /** 执行编辑的用户ID */
    @Column(nullable = false)
    private Long editedBy;

    /** 编辑时间 */
    @Column(nullable = false)
    private LocalDateTime editTime;

    /** 编辑前的内容 */
    @Column(name = "old_content", columnDefinition = "TEXT")
    private String oldContent;

    /** 编辑后的内容 */
    @Column(name = "new_content", columnDefinition = "TEXT", nullable = false)
    private String newContent;

    /** 编辑版本号（从1开始递增） */
    @Column(nullable = false)
    private int editVersion;

    /** 编辑原因 */
    @Column(length = 500)
    private String editReason;

    /** 消息类型 */
    @Column(length = 50)
    private String messageType;

    /** IP地址 */
    @Column(length = 50)
    private String ipAddress;

    /** 用户代理 */
    @Column(length = 500)
    private String userAgent;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========== 生命周期回调 ==========

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (editTime == null) {
            editTime = LocalDateTime.now();
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

    public Long getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Long editedBy) {
        this.editedBy = editedBy;
    }

    public LocalDateTime getEditTime() {
        return editTime;
    }

    public void setEditTime(LocalDateTime editTime) {
        this.editTime = editTime;
    }

    public String getOldContent() {
        return oldContent;
    }

    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public int getEditVersion() {
        return editVersion;
    }

    public void setEditVersion(int editVersion) {
        this.editVersion = editVersion;
    }

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ========== 便捷方法 ==========

    /**
     * 获取内容变更摘要
     */
    public String getChangeSummary() {
        int oldLen = oldContent != null ? oldContent.length() : 0;
        int newLen = newContent != null ? newContent.length() : 0;

        if (oldLen == 0) {
            return "新增内容 " + newLen + " 字符";
        }

        int diff = newLen - oldLen;
        if (diff > 0) {
            return "增加 " + diff + " 字符";
        } else if (diff < 0) {
            return "减少 " + Math.abs(diff) + " 字符";
        } else {
            return "内容长度相同";
        }
    }

    @Override
    public String toString() {
        return "MessageEditHistory{" +
            "id=" + id +
            ", messageId=" + messageId +
            ", editVersion=" + editVersion +
            ", editTime=" + editTime +
            '}';
    }
}
