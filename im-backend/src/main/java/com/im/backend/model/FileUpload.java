package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_uploads", indexes = {
    @Index(name = "idx_file_hash_status", columnList = "fileHash, status"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_status_updated", columnList = "status, updatedAt")
})
public class FileUpload {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 64)
    private String fileHash;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column(nullable = false)
    private Integer chunkSize;

    @Column(nullable = false)
    private Integer totalChunks;

    @Column(nullable = false)
    private Integer uploadedChunks;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String status; // UPLOADING, COMPLETED, PAUSED, FAILED

    @Column(nullable = false, length = 500)
    private String storagePath;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt;

    @Column(length = 500)
    private String errorMessage;

    // 关联消息ID（如果是通过消息上传）
    @Column
    private Long messageId;

    // 关联会话ID
    @Column
    private Long conversationId;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }

    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }

    public Integer getUploadedChunks() { return uploadedChunks; }
    public void setUploadedChunks(Integer uploadedChunks) { this.uploadedChunks = uploadedChunks; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "FileUpload{id='" + id + "', fileName='" + fileName + "', status='" + status + "'}";
    }
}
