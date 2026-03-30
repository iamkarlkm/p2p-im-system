package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 回复消息DTO
 */
public class ReplyMessageDTO {

    private Long id;
    private Long originalMessageId;
    private Long replyMessageId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String conversationType;
    private Long conversationId;
    private String replyContent;
    private String originalContentPreview;
    private String originalSenderName;
    private Long parentReplyId;
    private Integer replyLevel;
    private LocalDateTime createdAt;
    private List<ReplyMessageDTO> nestedReplies; // 嵌套回复

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(Long originalMessageId) { this.originalMessageId = originalMessageId; }

    public Long getReplyMessageId() { return replyMessageId; }
    public void setReplyMessageId(Long replyMessageId) { this.replyMessageId = replyMessageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public String getOriginalContentPreview() { return originalContentPreview; }
    public void setOriginalContentPreview(String originalContentPreview) { this.originalContentPreview = originalContentPreview; }

    public String getOriginalSenderName() { return originalSenderName; }
    public void setOriginalSenderName(String originalSenderName) { this.originalSenderName = originalSenderName; }

    public Long getParentReplyId() { return parentReplyId; }
    public void setParentReplyId(Long parentReplyId) { this.parentReplyId = parentReplyId; }

    public Integer getReplyLevel() { return replyLevel; }
    public void setReplyLevel(Integer replyLevel) { this.replyLevel = replyLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ReplyMessageDTO> getNestedReplies() { return nestedReplies; }
    public void setNestedReplies(List<ReplyMessageDTO> nestedReplies) { this.nestedReplies = nestedReplies; }
}
