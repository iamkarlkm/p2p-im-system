package com.im.backend.dto;

import com.im.backend.model.MessageQuoteReply.QuoteType;
import com.im.backend.model.MessageQuoteReply.QuoteStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息引用回复 DTO
 */
public class MessageQuoteReplyDTO {

    private Long id;
    private Long messageId;
    private Long quotedMessageId;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String replyContent;
    private Integer quoteLevel;
    private Long rootQuoteId;
    private Long parentQuoteId;
    private List<Long> quoteChain;
    private QuoteType quoteType;
    private Boolean includeOriginal;
    private String highlightKeywords;
    private Boolean isBatchQuote;
    private List<Long> batchQuotedMessageIds;
    private QuoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 引用的原始消息信息
    private QuotedMessageInfo quotedMessageInfo;

    // 引用链详情
    private List<QuotedMessageInfo> quoteChainDetails;

    public static class QuotedMessageInfo {
        private Long messageId;
        private Long senderId;
        private String senderName;
        private String senderAvatar;
        private String content;
        private String messageType;
        private LocalDateTime sentAt;
        private List<String> mediaUrls;

        public Long getMessageId() { return messageId; }
        public void setMessageId(Long messageId) { this.messageId = messageId; }

        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }

        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }

        public String getSenderAvatar() { return senderAvatar; }
        public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }

        public LocalDateTime getSentAt() { return sentAt; }
        public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

        public List<String> getMediaUrls() { return mediaUrls; }
        public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getQuotedMessageId() { return quotedMessageId; }
    public void setQuotedMessageId(Long quotedMessageId) { this.quotedMessageId = quotedMessageId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public Integer getQuoteLevel() { return quoteLevel; }
    public void setQuoteLevel(Integer quoteLevel) { this.quoteLevel = quoteLevel; }

    public Long getRootQuoteId() { return rootQuoteId; }
    public void setRootQuoteId(Long rootQuoteId) { this.rootQuoteId = rootQuoteId; }

    public Long getParentQuoteId() { return parentQuoteId; }
    public void setParentQuoteId(Long parentQuoteId) { this.parentQuoteId = parentQuoteId; }

    public List<Long> getQuoteChain() { return quoteChain; }
    public void setQuoteChain(List<Long> quoteChain) { this.quoteChain = quoteChain; }

    public QuoteType getQuoteType() { return quoteType; }
    public void setQuoteType(QuoteType quoteType) { this.quoteType = quoteType; }

    public Boolean getIncludeOriginal() { return includeOriginal; }
    public void setIncludeOriginal(Boolean includeOriginal) { this.includeOriginal = includeOriginal; }

    public String getHighlightKeywords() { return highlightKeywords; }
    public void setHighlightKeywords(String highlightKeywords) { this.highlightKeywords = highlightKeywords; }

    public Boolean getIsBatchQuote() { return isBatchQuote; }
    public void setIsBatchQuote(Boolean isBatchQuote) { this.isBatchQuote = isBatchQuote; }

    public List<Long> getBatchQuotedMessageIds() { return batchQuotedMessageIds; }
    public void setBatchQuotedMessageIds(List<Long> batchQuotedMessageIds) { this.batchQuotedMessageIds = batchQuotedMessageIds; }

    public QuoteStatus getStatus() { return status; }
    public void setStatus(QuoteStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public QuotedMessageInfo getQuotedMessageInfo() { return quotedMessageInfo; }
    public void setQuotedMessageInfo(QuotedMessageInfo quotedMessageInfo) { this.quotedMessageInfo = quotedMessageInfo; }

    public List<QuotedMessageInfo> getQuoteChainDetails() { return quoteChainDetails; }
    public void setQuoteChainDetails(List<QuotedMessageInfo> quoteChainDetails) { this.quoteChainDetails = quoteChainDetails; }
}
