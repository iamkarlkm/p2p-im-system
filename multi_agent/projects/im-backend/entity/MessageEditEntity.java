package com.im.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息编辑实体 - 记录消息编辑的历史版本
 */
@Entity
@Table(name = "message_edits", indexes = {
    @Index(name = "idx_message_edits_message_id", columnList = "message_id"),
    @Index(name = "idx_message_edits_user_id", columnList = "user_id"),
    @Index(name = "idx_message_edits_conversation_id", columnList = "conversation_id"),
    @Index(name = "idx_message_edits_created_at", columnList = "created_at DESC"),
    @Index(name = "idx_message_edits_version", columnList = "version"),
    @Index(name = "idx_message_edits_edit_type", columnList = "edit_type"),
    @Index(name = "idx_message_edits_status", columnList = "status")
})
public class MessageEditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "message_id", nullable = false)
    private UUID messageId;
    
    @Column(name = "original_message_id", nullable = false)
    private UUID originalMessageId;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;
    
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType; // "TEXT", "MARKDOWN", "HTML", "JSON"
    
    @Lob
    @Column(name = "content", nullable = false)
    private String content;
    
    @Lob
    @Column(name = "original_content")
    private String originalContent;
    
    @Column(name = "edit_type", nullable = false, length = 30)
    private String editType; // "CREATE", "EDIT", "REPLACE", "CORRECT", "ENHANCE"
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest;
    
    @Column(name = "client_message_id")
    private String clientMessageId;
    
    @Column(name = "edit_reason", length = 500)
    private String editReason;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON metadata
    
    @Column(name = "content_hash", length = 64)
    private String contentHash; // SHA-256 hash of content
    
    @Column(name = "original_content_hash", length = 64)
    private String originalContentHash;
    
    @Column(name = "diff_patch", columnDefinition = "TEXT")
    private String diffPatch; // JSON diff patch (RFC 6902)
    
    @Column(name = "edit_size_delta")
    private Integer editSizeDelta; // Character count delta
    
    @Column(name = "edit_word_count")
    private Integer editWordCount;
    
    @Column(name = "has_attachments")
    private Boolean hasAttachments;
    
    @Column(name = "attachments_json", columnDefinition = "TEXT")
    private String attachmentsJson;
    
    @Column(name = "mentions_json", columnDefinition = "TEXT")
    private String mentionsJson;
    
    @Column(name = "links_json", columnDefinition = "TEXT")
    private String linksJson;
    
    @Column(name = "formatting_json", columnDefinition = "TEXT")
    private String formattingJson;
    
    @Column(name = "read_count", nullable = false)
    private Integer readCount = 0;
    
    @Column(name = "reaction_count", nullable = false)
    private Integer reactionCount = 0;
    
    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;
    
    @Column(name = "edit_count", nullable = false)
    private Integer editCount = 0;
    
    @Column(name = "audit_status", nullable = false, length = 20)
    private String auditStatus = "PENDING"; // "PENDING", "APPROVED", "REJECTED", "FLAGGED"
    
    @Column(name = "audit_notes", length = 1000)
    private String auditNotes;
    
    @Column(name = "auditor_id")
    private UUID auditorId;
    
    @Column(name = "audit_timestamp")
    private LocalDateTime auditTimestamp;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE"; // "ACTIVE", "DELETED", "ARCHIVED", "HIDDEN"
    
    @Column(name = "privacy_level", nullable = false, length = 20)
    private String privacyLevel = "STANDARD"; // "PUBLIC", "STANDARD", "PRIVATE", "CONFIDENTIAL"
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "client_version", length = 50)
    private String clientVersion;
    
    @Column(name = "platform", length = 30)
    private String platform; // "WEB", "DESKTOP", "IOS", "ANDROID"
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "sync_status", nullable = false, length = 20)
    private String syncStatus = "SYNCED"; // "SYNCED", "PENDING", "FAILED", "CONFLICT"
    
    @Column(name = "conflict_resolution", length = 20)
    private String conflictResolution; // "KEEP_NEWEST", "KEEP_ORIGINAL", "MERGE", "MANUAL"
    
    @Column(name = "conflict_details", columnDefinition = "TEXT")
    private String conflictDetails;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "version_expires_at")
    private LocalDateTime versionExpiresAt;
    
    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "last_modified_by")
    private UUID lastModifiedBy;
    
    @Column(name = "tags", length = 1000)
    private String tags; // Comma-separated tags
    
    @Column(name = "custom_fields", columnDefinition = "TEXT")
    private String customFields; // JSON custom fields
    
    @Version
    private Long entityVersion;
    
    // Constructors
    public MessageEditEntity() {}
    
    public MessageEditEntity(UUID messageId, UUID originalMessageId, UUID userId, UUID conversationId,
                           String contentType, String content, Integer version, Boolean isLatest) {
        this.messageId = messageId;
        this.originalMessageId = originalMessageId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.contentType = contentType;
        this.content = content;
        this.version = version;
        this.isLatest = isLatest;
        this.editType = version == 1 ? "CREATE" : "EDIT";
        this.status = "ACTIVE";
        this.privacyLevel = "STANDARD";
        this.syncStatus = "SYNCED";
        this.readCount = 0;
        this.reactionCount = 0;
        this.replyCount = 0;
        this.editCount = 0;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
    
    public UUID getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(UUID originalMessageId) { this.originalMessageId = originalMessageId; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
    
    public String getEditType() { return editType; }
    public void setEditType(String editType) { this.editType = editType; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    public Boolean getIsLatest() { return isLatest; }
    public void setIsLatest(Boolean isLatest) { this.isLatest = isLatest; }
    
    public String getClientMessageId() { return clientMessageId; }
    public void setClientMessageId(String clientMessageId) { this.clientMessageId = clientMessageId; }
    
    public String getEditReason() { return editReason; }
    public void setEditReason(String editReason) { this.editReason = editReason; }
    
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    
    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }
    
    public String getOriginalContentHash() { return originalContentHash; }
    public void setOriginalContentHash(String originalContentHash) { this.originalContentHash = originalContentHash; }
    
    public String getDiffPatch() { return diffPatch; }
    public void setDiffPatch(String diffPatch) { this.diffPatch = diffPatch; }
    
    public Integer getEditSizeDelta() { return editSizeDelta; }
    public void setEditSizeDelta(Integer editSizeDelta) { this.editSizeDelta = editSizeDelta; }
    
    public Integer getEditWordCount() { return editWordCount; }
    public void setEditWordCount(Integer editWordCount) { this.editWordCount = editWordCount; }
    
    public Boolean getHasAttachments() { return hasAttachments; }
    public void setHasAttachments(Boolean hasAttachments) { this.hasAttachments = hasAttachments; }
    
    public String getAttachmentsJson() { return attachmentsJson; }
    public void setAttachmentsJson(String attachmentsJson) { this.attachmentsJson = attachmentsJson; }
    
    public String getMentionsJson() { return mentionsJson; }
    public void setMentionsJson(String mentionsJson) { this.mentionsJson = mentionsJson; }
    
    public String getLinksJson() { return linksJson; }
    public void setLinksJson(String linksJson) { this.linksJson = linksJson; }
    
    public String getFormattingJson() { return formattingJson; }
    public void setFormattingJson(String formattingJson) { this.formattingJson = formattingJson; }
    
    public Integer getReadCount() { return readCount; }
    public void setReadCount(Integer readCount) { this.readCount = readCount; }
    
    public Integer getReactionCount() { return reactionCount; }
    public void setReactionCount(Integer reactionCount) { this.reactionCount = reactionCount; }
    
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    
    public Integer getEditCount() { return editCount; }
    public void setEditCount(Integer editCount) { this.editCount = editCount; }
    
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    
    public String getAuditNotes() { return auditNotes; }
    public void setAuditNotes(String auditNotes) { this.auditNotes = auditNotes; }
    
    public UUID getAuditorId() { return auditorId; }
    public void setAuditorId(UUID auditorId) { this.auditorId = auditorId; }
    
    public LocalDateTime getAuditTimestamp() { return auditTimestamp; }
    public void setAuditTimestamp(LocalDateTime auditTimestamp) { this.auditTimestamp = auditTimestamp; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPrivacyLevel() { return privacyLevel; }
    public void setPrivacyLevel(String privacyLevel) { this.privacyLevel = privacyLevel; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getClientVersion() { return clientVersion; }
    public void setClientVersion(String clientVersion) { this.clientVersion = clientVersion; }
    
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    
    public String getConflictResolution() { return conflictResolution; }
    public void setConflictResolution(String conflictResolution) { this.conflictResolution = conflictResolution; }
    
    public String getConflictDetails() { return conflictDetails; }
    public void setConflictDetails(String conflictDetails) { this.conflictDetails = conflictDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public LocalDateTime getVersionExpiresAt() { return versionExpiresAt; }
    public void setVersionExpiresAt(LocalDateTime versionExpiresAt) { this.versionExpiresAt = versionExpiresAt; }
    
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    public UUID getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(UUID lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    public String getCustomFields() { return customFields; }
    public void setCustomFields(String customFields) { this.customFields = customFields; }
    
    public Long getEntityVersion() { return entityVersion; }
    public void setEntityVersion(Long entityVersion) { this.entityVersion = entityVersion; }
}