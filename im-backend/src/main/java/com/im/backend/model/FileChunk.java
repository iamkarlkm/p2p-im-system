package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_chunks", indexes = {
    @Index(name = "idx_upload_id", columnList = "uploadId"),
    @Index(name = "idx_upload_chunk", columnList = "uploadId, chunkIndex", unique = true)
})
public class FileChunk {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 64)
    private String uploadId;

    @Column(nullable = false)
    private Integer chunkIndex;

    @Column(nullable = false)
    private Integer chunkSize;

    @Column(nullable = false, length = 64)
    private String chunkHash;

    @Column(nullable = false, length = 500)
    private String storagePath;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime expiresAt;

    // 重试次数
    @Column
    private Integer retryCount;

    // 最后重试时间
    @Column
    private LocalDateTime lastRetryAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUploadId() { return uploadId; }
    public void setUploadId(String uploadId) { this.uploadId = uploadId; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }

    public String getChunkHash() { return chunkHash; }
    public void setChunkHash(String chunkHash) { this.chunkHash = chunkHash; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public LocalDateTime getLastRetryAt() { return lastRetryAt; }
    public void setLastRetryAt(LocalDateTime lastRetryAt) { this.lastRetryAt = lastRetryAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // 分片7天后过期
        expiresAt = createdAt.plusDays(7);
        retryCount = 0;
    }

    @Override
    public String toString() {
        return "FileChunk{id='" + id + "', uploadId='" + uploadId + "', chunkIndex=" + chunkIndex + "}";
    }
}
