package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 边缘缓存消息实体
 * 用于存储边缘节点上的缓存消息数据
 */
@Entity
@Table(name = "edge_cache_messages", indexes = {
    @Index(name = "idx_edge_cache_message_id", columnList = "message_id"),
    @Index(name = "idx_edge_cache_node_id", columnList = "edge_node_id"),
    @Index(name = "idx_edge_cache_user_id", columnList = "user_id"),
    @Index(name = "idx_edge_cache_status", columnList = "cache_status"),
    @Index(name = "idx_edge_cache_ttl", columnList = "ttl_expiry"),
    @Index(name = "idx_edge_cache_priority", columnList = "cache_priority")
})
public class EdgeCacheMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "message_id", nullable = false, length = 100)
    private String messageId; // 原始消息ID

    @Column(name = "edge_node_id", nullable = false)
    private UUID edgeNodeId; // 边缘节点ID

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId; // 用户ID

    @Column(name = "conversation_id", nullable = false, length = 100)
    private String conversationId; // 会话ID

    @Column(name = "message_type", nullable = false, length = 30)
    private String messageType; // TEXT, IMAGE, VIDEO, AUDIO, FILE, SYSTEM

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 消息内容（JSON或原始文本）

    @Column(name = "compressed_content", columnDefinition = "MEDIUMBLOB")
    private byte[] compressedContent; // 压缩后的内容

    @Column(name = "content_hash", length = 64)
    private String contentHash; // 内容哈希值

    @Column(name = "original_size")
    private Long originalSize; // 原始内容大小（字节）

    @Column(name = "compressed_size")
    private Long compressedSize; // 压缩后大小（字节）

    @Column(name = "compression_ratio")
    private Double compressionRatio; // 压缩比率

    @Column(name = "cache_status", nullable = false, length = 20)
    private String cacheStatus; // ACTIVE, EXPIRED, DELETED, SYNCED, PENDING

    @Column(name = "cache_priority", nullable = false)
    private Integer cachePriority = 5; // 缓存优先级（1-10，1最高）

    @Column(name = "access_frequency")
    private Integer accessFrequency = 0; // 访问频率

    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    @Column(name = "ttl_expiry")
    private LocalDateTime ttlExpiry; // 缓存过期时间

    @Column(name = "ttl_seconds")
    private Long ttlSeconds = 86400L; // 默认TTL：24小时

    @Column(name = "sync_status", length = 20)
    private String syncStatus; // UNSYNCED, SYNCED, SYNCING, FAILED

    @Column(name = "sync_attempts")
    private Integer syncAttempts = 0;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "sync_error", length = 500)
    private String syncError;

    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted = true;

    @Column(name = "encryption_algorithm", length = 50)
    private String encryptionAlgorithm; // AES256, CHACHA20, etc.

    @Column(name = "encryption_key_id", length = 100)
    private String encryptionKeyId;

    @Column(name = "integrity_check", length = 100)
    private String integrityCheck; // 完整性校验值

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON格式的元数据

    @Column(name = "tags", length = 300)
    private String tags; // 逗号分隔的标签

    @Column(name = "cache_hit_count")
    private Long cacheHitCount = 0L;

    @Column(name = "cache_miss_count")
    private Long cacheMissCount = 0L;

    @Column(name = "cache_cost_ms")
    private Long cacheCostMs; // 缓存访问耗时（毫秒）

    @Column(name = "origin_server", length = 100)
    private String originServer; // 原始服务器地址

    @Column(name = "origin_timestamp")
    private LocalDateTime originTimestamp; // 原始消息时间戳

    @Column(name = "local_timestamp", nullable = false)
    private LocalDateTime localTimestamp; // 本地缓存时间戳

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_pinned", nullable = false)
    private Boolean isPinned = false; // 是否固定（不会被自动清理）

    @Column(name = "is_offline_available", nullable = false)
    private Boolean isOfflineAvailable = true; // 是否支持离线访问

    @Column(name = "bandwidth_saved")
    private Long bandwidthSaved; // 节省的带宽（字节）

    @Column(name = "latency_reduced_ms")
    private Long latencyReducedMs; // 减少的延迟（毫秒）

    @Column(name = "replication_factor")
    private Integer replicationFactor = 1; // 副本因子

    @Column(name = "current_replicas")
    private Integer currentReplicas = 1; // 当前副本数

    @Column(name = "storage_location", length = 200)
    private String storageLocation; // 存储位置（路径或URL）

    @Column(name = "validation_status", length = 20)
    private String validationStatus; // VALID, INVALID, PENDING

    @Column(name = "validation_timestamp")
    private LocalDateTime validationTimestamp;

    // 构造函数
    public EdgeCacheMessageEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.localTimestamp = LocalDateTime.now();
    }

    public EdgeCacheMessageEntity(String messageId, UUID edgeNodeId, String userId, 
                                  String conversationId, String messageType, String content) {
        this();
        this.messageId = messageId;
        this.edgeNodeId = edgeNodeId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.messageType = messageType;
        this.content = content;
        this.cacheStatus = "ACTIVE";
        this.syncStatus = "UNSYNCED";
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public UUID getEdgeNodeId() { return edgeNodeId; }
    public void setEdgeNodeId(UUID edgeNodeId) { this.edgeNodeId = edgeNodeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public byte[] getCompressedContent() { return compressedContent; }
    public void setCompressedContent(byte[] compressedContent) { this.compressedContent = compressedContent; }

    public String getContentHash() { return contentHash; }
    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public Long getOriginalSize() { return originalSize; }
    public void setOriginalSize(Long originalSize) { this.originalSize = originalSize; }

    public Long getCompressedSize() { return compressedSize; }
    public void setCompressedSize(Long compressedSize) { this.compressedSize = compressedSize; }

    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }

    public String getCacheStatus() { return cacheStatus; }
    public void setCacheStatus(String cacheStatus) { this.cacheStatus = cacheStatus; }

    public Integer getCachePriority() { return cachePriority; }
    public void setCachePriority(Integer cachePriority) { this.cachePriority = cachePriority; }

    public Integer getAccessFrequency() { return accessFrequency; }
    public void setAccessFrequency(Integer accessFrequency) { this.accessFrequency = accessFrequency; }

    public LocalDateTime getLastAccessTime() { return lastAccessTime; }
    public void setLastAccessTime(LocalDateTime lastAccessTime) { this.lastAccessTime = lastAccessTime; }

    public LocalDateTime getTtlExpiry() { return ttlExpiry; }
    public void setTtlExpiry(LocalDateTime ttlExpiry) { this.ttlExpiry = ttlExpiry; }

    public Long getTtlSeconds() { return ttlSeconds; }
    public void setTtlSeconds(Long ttlSeconds) { this.ttlSeconds = ttlSeconds; }

    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    public Integer getSyncAttempts() { return syncAttempts; }
    public void setSyncAttempts(Integer syncAttempts) { this.syncAttempts = syncAttempts; }

    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public String getSyncError() { return syncError; }
    public void setSyncError(String syncError) { this.syncError = syncError; }

    public Boolean getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Boolean isEncrypted) { this.isEncrypted = isEncrypted; }

    public String getEncryptionAlgorithm() { return encryptionAlgorithm; }
    public void setEncryptionAlgorithm(String encryptionAlgorithm) { this.encryptionAlgorithm = encryptionAlgorithm; }

    public String getEncryptionKeyId() { return encryptionKeyId; }
    public void setEncryptionKeyId(String encryptionKeyId) { this.encryptionKeyId = encryptionKeyId; }

    public String getIntegrityCheck() { return integrityCheck; }
    public void setIntegrityCheck(String integrityCheck) { this.integrityCheck = integrityCheck; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Long getCacheHitCount() { return cacheHitCount; }
    public void setCacheHitCount(Long cacheHitCount) { this.cacheHitCount = cacheHitCount; }

    public Long getCacheMissCount() { return cacheMissCount; }
    public void setCacheMissCount(Long cacheMissCount) { this.cacheMissCount = cacheMissCount; }

    public Long getCacheCostMs() { return cacheCostMs; }
    public void setCacheCostMs(Long cacheCostMs) { this.cacheCostMs = cacheCostMs; }

    public String getOriginServer() { return originServer; }
    public void setOriginServer(String originServer) { this.originServer = originServer; }

    public LocalDateTime getOriginTimestamp() { return originTimestamp; }
    public void setOriginTimestamp(LocalDateTime originTimestamp) { this.originTimestamp = originTimestamp; }

    public LocalDateTime getLocalTimestamp() { return localTimestamp; }
    public void setLocalTimestamp(LocalDateTime localTimestamp) { this.localTimestamp = localTimestamp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

    public Boolean getIsOfflineAvailable() { return isOfflineAvailable; }
    public void setIsOfflineAvailable(Boolean isOfflineAvailable) { this.isOfflineAvailable = isOfflineAvailable; }

    public Long getBandwidthSaved() { return bandwidthSaved; }
    public void setBandwidthSaved(Long bandwidthSaved) { this.bandwidthSaved = bandwidthSaved; }

    public Long getLatencyReducedMs() { return latencyReducedMs; }
    public void setLatencyReducedMs(Long latencyReducedMs) { this.latencyReducedMs = latencyReducedMs; }

    public Integer getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(Integer replicationFactor) { this.replicationFactor = replicationFactor; }

    public Integer getCurrentReplicas() { return currentReplicas; }
    public void setCurrentReplicas(Integer currentReplicas) { this.currentReplicas = currentReplicas; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public LocalDateTime getValidationTimestamp() { return validationTimestamp; }
    public void setValidationTimestamp(LocalDateTime validationTimestamp) { this.validationTimestamp = validationTimestamp; }

    // 辅助方法
    public boolean isActive() {
        return "ACTIVE".equals(cacheStatus);
    }

    public boolean isExpired() {
        if (ttlExpiry == null) return false;
        return LocalDateTime.now().isAfter(ttlExpiry);
    }

    public boolean needsSync() {
        return "UNSYNCED".equals(syncStatus) || "FAILED".equals(syncStatus);
    }

    public void incrementAccess() {
        this.accessFrequency++;
        this.lastAccessTime = LocalDateTime.now();
        this.cacheHitCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementMiss() {
        this.cacheMissCount++;
    }

    public void compressContent() {
        if (content != null && content.length() > 1024) { // 大于1KB才压缩
            try {
                // 模拟压缩逻辑（实际应用中会使用GZIP或其他压缩算法）
                byte[] originalBytes = content.getBytes("UTF-8");
                this.originalSize = (long) originalBytes.length;
                // 简单压缩：移除空格和换行（模拟）
                String compressed = content.replaceAll("\\s+", " ").trim();
                this.compressedContent = compressed.getBytes("UTF-8");
                this.compressedSize = (long) this.compressedContent.length;
                this.compressionRatio = this.originalSize.doubleValue() / this.compressedSize.doubleValue();
            } catch (Exception e) {
                // 压缩失败，保持原样
                this.compressedContent = null;
                this.compressedSize = null;
                this.compressionRatio = null;
            }
        }
    }

    public void setTtlFromNow() {
        if (ttlSeconds != null) {
            this.ttlExpiry = LocalDateTime.now().plusSeconds(ttlSeconds);
        }
    }

    public void markAsSynced() {
        this.syncStatus = "SYNCED";
        this.lastSyncTime = LocalDateTime.now();
        this.syncAttempts = 0;
        this.syncError = null;
    }

    public void markAsFailed(String error) {
        this.syncStatus = "FAILED";
        this.syncError = error;
        this.syncAttempts++;
    }

    public void calculateBandwidthSavings() {
        if (originalSize != null && cacheHitCount != null && cacheHitCount > 0) {
            // 节省的带宽 = 每次访问节省的大小 × 访问次数
            long savedPerHit = originalSize - (compressedSize != null ? compressedSize : originalSize);
            this.bandwidthSaved = savedPerHit * cacheHitCount;
        }
    }

    public void validate() {
        if (contentHash != null && content != null) {
            try {
                // 简单的哈希验证（实际应用中会使用SHA-256等）
                String calculatedHash = Integer.toHexString(content.hashCode());
                if (calculatedHash.equals(contentHash)) {
                    this.validationStatus = "VALID";
                } else {
                    this.validationStatus = "INVALID";
                }
            } catch (Exception e) {
                this.validationStatus = "INVALID";
            }
        } else {
            this.validationStatus = "PENDING";
        }
        this.validationTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("EdgeCacheMessage[id=%s, messageId=%s, node=%s, status=%s, hits=%d]", 
            id, messageId, edgeNodeId, cacheStatus, cacheHitCount);
    }
}