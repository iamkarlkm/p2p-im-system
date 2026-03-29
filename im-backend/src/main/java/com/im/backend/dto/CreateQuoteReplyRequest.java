package com.im.backend.dto;

import java.util.List;

/**
 * 创建消息引用回复请求
 */
public class CreateQuoteReplyRequest {

    private Long quotedMessageId;
    private Long conversationId;
    private String replyContent;
    private Long parentQuoteId;
    private List<Long> batchQuotedMessageIds;
    private Boolean includeOriginal = true;
    private String highlightKeywords;

    public Long getQuotedMessageId() { return quotedMessageId; }
    public void setQuotedMessageId(Long quotedMessageId) { this.quotedMessageId = quotedMessageId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Long getParentQuoteId() { return parentQuoteId; }
    public void setParentQuoteId(Long parentQuoteId) { this.parentQuoteId = parentQuoteId; }

    public List<Long> getBatchQuotedMessageIds() { return batchQuotedMessageIds; }
    public void setBatchQuotedMessageIds(List<Long> batchQuotedMessageIds) { this.batchQuotedMessageIds = batchQuotedMessageIds; }

    public Boolean getIncludeOriginal() { return includeOriginal; }
    public void setIncludeOriginal(Boolean includeOriginal) { this.includeOriginal = includeOriginal; }

    public String getHighlightKeywords() { return highlightKeywords; }
    public void setHighlightKeywords(String highlightKeywords) { this.highlightKeywords = highlightKeywords; }
}
