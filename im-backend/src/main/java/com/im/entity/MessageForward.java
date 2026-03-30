package com.im.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息转发记录实体
 * 功能#22: 消息转发
 */
@Entity
@Table(name = "message_forward")
public class MessageForward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_message_id", nullable = false)
    private Long originalMessageId;

    @Column(name = "forwarder_id", nullable = false)
    private Long forwarderId;

    @Column(name = "target_conversation_id")
    private Long targetConversationId;

    @Column(name = "target_conversation_type")
    @Enumerated(EnumType.STRING)
    private ConversationType targetConversationType;

    @Column(name = "new_message_id")
    private Long newMessageId;

    @Column(name = "forward_time", nullable = false)
    private LocalDateTime forwardTime;

    @Column(name = "forward_count", nullable = false)
    private Integer forwardCount = 1;

    @Column(name = "forward_comment", length = 500)
    private String forwardComment;

    @Column(name = "is_multi_forward")
    private Boolean isMultiForward = false;

    @Column(name = "target_user_ids", length = 1000)
    private String targetUserIds;

    @PrePersist
    protected void onCreate() {
        forwardTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(Long originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public Long getForwarderId() {
        return forwarderId;
    }

    public void setForwarderId(Long forwarderId) {
        this.forwarderId = forwarderId;
    }

    public Long getTargetConversationId() {
        return targetConversationId;
    }

    public void setTargetConversationId(Long targetConversationId) {
        this.targetConversationId = targetConversationId;
    }

    public ConversationType getTargetConversationType() {
        return targetConversationType;
    }

    public void setTargetConversationType(ConversationType targetConversationType) {
        this.targetConversationType = targetConversationType;
    }

    public Long getNewMessageId() {
        return newMessageId;
    }

    public void setNewMessageId(Long newMessageId) {
        this.newMessageId = newMessageId;
    }

    public LocalDateTime getForwardTime() {
        return forwardTime;
    }

    public void setForwardTime(LocalDateTime forwardTime) {
        this.forwardTime = forwardTime;
    }

    public Integer getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(Integer forwardCount) {
        this.forwardCount = forwardCount;
    }

    public String getForwardComment() {
        return forwardComment;
    }

    public void setForwardComment(String forwardComment) {
        this.forwardComment = forwardComment;
    }

    public Boolean getIsMultiForward() {
        return isMultiForward;
    }

    public void setIsMultiForward(Boolean isMultiForward) {
        this.isMultiForward = isMultiForward;
    }

    public String getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(String targetUserIds) {
        this.targetUserIds = targetUserIds;
    }
}
