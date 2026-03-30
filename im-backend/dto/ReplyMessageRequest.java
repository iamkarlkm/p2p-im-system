package com.im.backend.dto;

/**
 * 回复消息请求DTO
 */
public class ReplyMessageRequest {

    private Long originalMessageId;
    private String replyContent;
    private String conversationType;
    private Long conversationId;
    private Long parentReplyId; // 嵌套引用时填写

    // Getters and Setters
    public Long getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(Long originalMessageId) { this.originalMessageId = originalMessageId; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getParentReplyId() { return parentReplyId; }
    public void setParentReplyId(Long parentReplyId) { this.parentReplyId = parentReplyId; }
}
