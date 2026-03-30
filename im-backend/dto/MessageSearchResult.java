package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息搜索结果DTO
 */
public class MessageSearchResult {

    private Long messageId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String conversationType;
    private Long conversationId;
    private String conversationName;
    private String content;
    private String highlightedContent; // 高亮后的内容
    private String contentType;
    private LocalDateTime createdAt;
    private List<Integer> highlightPositions; // 高亮位置索引

    // Constructors
    public MessageSearchResult() {}

    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

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

    public String getConversationName() { return conversationName; }
    public void setConversationName(String conversationName) { this.conversationName = conversationName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHighlightedContent() { return highlightedContent; }
    public void setHighlightedContent(String highlightedContent) { this.highlightedContent = highlightedContent; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Integer> getHighlightPositions() { return highlightPositions; }
    public void setHighlightPositions(List<Integer> highlightPositions) { this.highlightPositions = highlightPositions; }
}
