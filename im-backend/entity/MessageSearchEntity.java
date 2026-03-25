package com.im.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息全文搜索索引实体
 * 用于 Elasticsearch/PostgreSQL 全文搜索
 */
@Document(indexName = "message_search_index")
@Setting(settingPath = "es-settings.json")
@Mapping(mappingPath = "es-mappings.json")
@Entity
@Table(name = "message_search_index", indexes = {
    @Index(name = "idx_message_search_session_id", columnList = "sessionId"),
    @Index(name = "idx_message_search_sender_id", columnList = "senderId"),
    @Index(name = "idx_message_search_created_at", columnList = "createdAt"),
    @Index(name = "idx_message_search_type", columnList = "messageType"),
    @Index(name = "idx_message_search_has_attachment", columnList = "hasAttachment")
})
public class MessageSearchEntity {
    
    @Id
    @org.springframework.data.annotation.Id
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Field(name = "message_id", type = FieldType.Keyword)
    @Column(name = "message_id", length = 64, unique = true, nullable = false)
    private String messageId;
    
    @Field(name = "session_id", type = FieldType.Keyword)
    @Column(name = "session_id", length = 64, nullable = false)
    private String sessionId;
    
    @Field(name = "sender_id", type = FieldType.Keyword)
    @Column(name = "sender_id", length = 64, nullable = false)
    private String senderId;
    
    @Field(name = "sender_name", type = FieldType.Text)
    @Column(name = "sender_name", length = 255)
    private String senderName;
    
    @Field(name = "message_type", type = FieldType.Keyword)
    @Column(name = "message_type", length = 50)
    private String messageType;
    
    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
        otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword)
        }
    )
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Field(name = "plain_content", type = FieldType.Text, analyzer = "standard")
    @Column(name = "plain_content", columnDefinition = "TEXT")
    private String plainContent;
    
    @Field(name = "has_attachment", type = FieldType.Boolean)
    @Column(name = "has_attachment")
    private Boolean hasAttachment = false;
    
    @Field(name = "attachment_count", type = FieldType.Integer)
    @Column(name = "attachment_count")
    private Integer attachmentCount = 0;
    
    @Field(name = "attachment_types", type = FieldType.Keyword)
    @Column(name = "attachment_types", length = 255)
    private String attachmentTypes;
    
    @Field(name = "is_encrypted", type = FieldType.Boolean)
    @Column(name = "is_encrypted")
    private Boolean isEncrypted = false;
    
    @Field(name = "is_deleted", type = FieldType.Boolean)
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Field(name = "is_edited", type = FieldType.Boolean)
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Field(name = "edit_count", type = FieldType.Integer)
    @Column(name = "edit_count")
    private Integer editCount = 0;
    
    @Field(name = "reaction_count", type = FieldType.Integer)
    @Column(name = "reaction_count")
    private Integer reactionCount = 0;
    
    @Field(name = "reply_count", type = FieldType.Integer)
    @Column(name = "reply_count")
    private Integer replyCount = 0;
    
    @Field(name = "read_count", type = FieldType.Integer)
    @Column(name = "read_count")
    private Integer readCount = 0;
    
    @Field(name = "priority", type = FieldType.Integer)
    @Column(name = "priority")
    private Integer priority = 0;
    
    @Field(name = "tags", type = FieldType.Keyword)
    @Column(name = "tags", length = 500)
    private String tags;
    
    @Field(name = "keywords", type = FieldType.Keyword)
    @Column(name = "keywords", length = 1000)
    private String keywords;
    
    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Field(name = "expires_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Field(name = "search_score", type = FieldType.Float)
    @Column(name = "search_score")
    private Float searchScore;
    
    @Field(name = "language", type = FieldType.Keyword)
    @Column(name = "language", length = 10)
    private String language;
    
    @Field(name = "sentiment_score", type = FieldType.Float)
    @Column(name = "sentiment_score")
    private Float sentimentScore;
    
    @Field(name = "metadata_json", type = FieldType.Text)
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;
    
    @Version
    @Column(name = "version")
    private Long version = 0L;
    
    // 构造函数
    public MessageSearchEntity() {}
    
    public MessageSearchEntity(String messageId, String sessionId, String senderId, String content) {
        this.messageId = messageId;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.content = content;
        this.plainContent = extractPlainText(content);
        this.keywords = extractKeywords(content);
    }
    
    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.plainContent = extractPlainText(content);
        this.keywords = extractKeywords(content);
    }
    
    public String getPlainContent() { return plainContent; }
    public void setPlainContent(String plainContent) { this.plainContent = plainContent; }
    
    public Boolean getHasAttachment() { return hasAttachment; }
    public void setHasAttachment(Boolean hasAttachment) { this.hasAttachment = hasAttachment; }
    
    public Integer getAttachmentCount() { return attachmentCount; }
    public void setAttachmentCount(Integer attachmentCount) { this.attachmentCount = attachmentCount; }
    
    public String getAttachmentTypes() { return attachmentTypes; }
    public void setAttachmentTypes(String attachmentTypes) { this.attachmentTypes = attachmentTypes; }
    
    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public Boolean getIsEdited() { return isEdited; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    
    public Integer getEditCount() { return editCount; }
    public void setEditCount(Integer editCount) { this.editCount = editCount; }
    
    public Integer getReactionCount() { return reactionCount; }
    public void setReactionCount(Integer reactionCount) { this.reactionCount = reactionCount; }
    
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    
    public Integer getReadCount() { return readCount; }
    public void setReadCount(Integer readCount) { this.readCount = readCount; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Float getSearchScore() { return searchScore; }
    public void setSearchScore(Float searchScore) { this.searchScore = searchScore; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public Float getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Float sentimentScore) { this.sentimentScore = sentimentScore; }
    
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    // 工具方法
    private String extractPlainText(String content) {
        if (content == null) return "";
        // 移除HTML标签、表情符号、URL等，提取纯文本
        return content.replaceAll("<[^>]+>", "")
                     .replaceAll("\\[.*?\\]", "")
                     .replaceAll("https?://\\S+", "")
                     .replaceAll("@\\w+", "")
                     .trim();
    }
    
    private String extractKeywords(String content) {
        if (content == null || content.isEmpty()) return "";
        // 简单关键词提取（实际应用中可以使用NLP库）
        String plain = extractPlainText(content);
        // 移除常见停用词
        String[] stopWords = {"的", "了", "在", "和", "是", "有", "不", "我", "他", "她", "它", "这", "那", "就", "也", "都", "而", "与", "或"};
        String result = plain;
        for (String stopWord : stopWords) {
            result = result.replaceAll("\\b" + stopWord + "\\b", "");
        }
        // 提取长度大于1的中文词汇或英文单词
        return result.replaceAll("\\b\\w{1}\\b", "")
                    .replaceAll("\\s+", ",")
                    .trim();
    }
    
    // 更新方法
    public void incrementEditCount() {
        this.editCount = (this.editCount == null ? 1 : this.editCount + 1);
        this.isEdited = true;
    }
    
    public void incrementReactionCount() {
        this.reactionCount = (this.reactionCount == null ? 1 : this.reactionCount + 1);
    }
    
    public void incrementReplyCount() {
        this.replyCount = (this.replyCount == null ? 1 : this.replyCount + 1);
    }
    
    public void incrementReadCount() {
        this.readCount = (this.readCount == null ? 1 : this.readCount + 1);
    }
    
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = tag;
        } else {
            this.tags = this.tags + "," + tag;
        }
    }
    
    public void addAttachmentType(String type) {
        if (this.attachmentTypes == null) {
            this.attachmentTypes = type;
        } else {
            this.attachmentTypes = this.attachmentTypes + "," + type;
        }
        this.hasAttachment = true;
        this.attachmentCount = (this.attachmentCount == null ? 1 : this.attachmentCount + 1);
    }
    
    @Override
    public String toString() {
        return "MessageSearchEntity{" +
               "id=" + id +
               ", messageId='" + messageId + '\'' +
               ", sessionId='" + sessionId + '\'' +
               ", senderId='" + senderId + '\'' +
               ", senderName='" + senderName + '\'' +
               ", messageType='" + messageType + '\'' +
               ", content='" + (content != null ? content.substring(0, Math.min(50, content.length())) : "") + "..." + '\'' +
               ", hasAttachment=" + hasAttachment +
               ", createdAt=" + createdAt +
               '}';
    }
}