package com.im.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息引用回复实体
 * 支持多级引用、引用链溯源
 */
@Entity
@Table(name = "message_quote_reply")
public class MessageQuoteReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "quoted_message_id", nullable = false)
    private Long quotedMessageId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "reply_content", length = 4000)
    private String replyContent;

    @Column(name = "quote_level")
    private Integer quoteLevel = 1;

    @Column(name = "root_quote_id")
    private Long rootQuoteId;

    @Column(name = "parent_quote_id")
    private Long parentQuoteId;

    @ElementCollection
    @CollectionTable(name = "quote_chain", joinColumns = @JoinColumn(name = "quote_reply_id"))
    @Column(name = "quoted_message_id")
    @OrderColumn(name = "chain_order")
    private List<Long> quoteChain = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "quote_type", length = 20)
    private QuoteType quoteType = QuoteType.SINGLE;

    @Column(name = "include_original")
    private Boolean includeOriginal = true;

    @Column(name = "highlight_keywords")
    private String highlightKeywords;

    @Column(name = "is_batch_quote")
    private Boolean isBatchQuote = false;

    @ElementCollection
    @CollectionTable(name = "batch_quoted_messages", joinColumns = @JoinColumn(name = "quote_reply_id"))
    @Column(name = "message_id")
    private List<Long> batchQuotedMessageIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QuoteStatus status = QuoteStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

    public enum QuoteType {
        SINGLE,           // 单条引用
        MULTI,            // 多条引用
        NESTED,           // 嵌套引用
        FORWARD           // 转发引用
    }

    public enum QuoteStatus {
        ACTIVE,           // 活跃
        EDITED,           // 已编辑
        DELETED,          // 已删除
        RECALLED          // 已撤回
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
}
