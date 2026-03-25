package com.im.server.entity;

import java.time.LocalDateTime;

/**
 * Message Search Index Entity
 * 
 * 消息搜索索引实体，用于全文搜索和消息检索
 * 支持消息内容的分词索引、关键词提取、高亮标注
 */
public class MessageSearchIndex {

    private Long id;
    
    /** 消息ID */
    private Long messageId;
    
    /** 会话ID */
    private Long conversationId;
    
    /** 会话类型：1-私聊 2-群聊 */
    private Integer conversationType;
    
    /** 发送者用户ID */
    private Long senderId;
    
    /** 发送者昵称 */
    private String senderNickname;
    
    /** 消息类型 */
    private Integer messageType;
    
    /** 消息内容（索引内容） */
    private String content;
    
    /** 纯文本内容（用于搜索） */
    private String plainText;
    
    /** 文件名（如果是文件消息） */
    private String fileName;
    
    /** 关键词列表（逗号分隔） */
    private String keywords;
    
    /** 提及的用户ID列表（逗号分隔） */
    private String mentionedUsers;
    
    /** 消息时间戳 */
    private LocalDateTime messageTime;
    
    /** 索引创建时间 */
    private LocalDateTime indexedAt;
    
    /** 是否已删除 */
    private Boolean deleted;
    
    /** 删除时间 */
    private LocalDateTime deletedAt;

    public MessageSearchIndex() {
    }

    public MessageSearchIndex(Long messageId, Long conversationId, Integer conversationType,
                              Long senderId, String senderNickname, Integer messageType,
                              String content, String plainText, LocalDateTime messageTime) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.messageType = messageType;
        this.content = content;
        this.plainText = plainText;
        this.messageTime = messageTime;
        this.indexedAt = LocalDateTime.now();
        this.deleted = false;
    }

    // Getters and Setters
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

    public Integer getConversationType() {
        return conversationType;
    }

    public void setConversationType(Integer conversationType) {
        this.conversationType = conversationType;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getMentionedUsers() {
        return mentionedUsers;
    }

    public void setMentionedUsers(String mentionedUsers) {
        this.mentionedUsers = mentionedUsers;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
    }

    public LocalDateTime getIndexedAt() {
        return indexedAt;
    }

    public void setIndexedAt(LocalDateTime indexedAt) {
        this.indexedAt = indexedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
