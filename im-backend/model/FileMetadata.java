package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 文件元数据实体
 */
@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_file_uploaded_by", columnList = "uploadedBy"),
    @Index(name = "idx_file_upload_time", columnList = "uploadTime"),
    @Index(name = "idx_file_expired", columnList = "expired"),
    @Index(name = "idx_file_conversation", columnList = "conversationId")
})
public class FileMetadata {

    @Id
    @Column(length = 64)
    private String fileId;

    /** 原始文件名 */
    @Column(nullable = false, length = 500)
    private String originalName;

    /** 存储路径 */
    @Column(nullable = false, length = 1000)
    private String storagePath;

    /** 文件大小（字节） */
    @Column(nullable = false)
    private long fileSize;

    /** 文件内容类型 */
    @Column(length = 100)
    private String contentType;

    /** 文件扩展名 */
    @Column(length = 20)
    private String extension;

    /** 上传者用户ID */
    @Column(nullable = false)
    private Long uploadedBy;

    /** 上传时间 */
    @Column(nullable = false)
    private LocalDateTime uploadTime;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 文件描述 */
    @Column(length = 1000)
    private String description;

    /** 下载次数 */
    @Column(nullable = false)
    private int downloadCount = 0;

    /** 最后下载时间 */
    private LocalDateTime lastDownloadTime;

    /** 是否已过期 */
    @Column(nullable = false)
    private boolean expired = false;

    /** 会话ID（如果文件属于某个会话） */
    private Long conversationId;

    /** 消息ID（如果文件属于某条消息） */
    private Long messageId;

    /** 文件哈希（用于去重） */
    @Column(length = 128)
    private String fileHash;

    /** 存储类型（LOCAL, OSS, S3等） */
    @Column(length = 20)
    private String storageType = "LOCAL";

    // ========== 生命周期回调 ==========

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
        downloadCount = 0;
        expired = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public LocalDateTime getLastDownloadTime() {
        return lastDownloadTime;
    }

    public void setLastDownloadTime(LocalDateTime lastDownloadTime) {
        this.lastDownloadTime = lastDownloadTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    // ========== 便捷方法 ==========

    /**
     * 检查是否为图片
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 检查是否为视频
     */
    public boolean isVideo() {
        return contentType != null && contentType.startsWith("video/");
    }

    /**
     * 检查是否为文档
     */
    public boolean isDocument() {
        if (contentType == null) return false;
        return contentType.contains("pdf") ||
               contentType.contains("word") ||
               contentType.contains("excel") ||
               contentType.contains("powerpoint") ||
               contentType.contains("text/plain");
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
            "fileId='" + fileId + '\'' +
            ", originalName='" + originalName + '\'' +
            ", fileSize=" + fileSize +
            ", uploadedBy=" + uploadedBy +
            ", uploadTime=" + uploadTime +
            '}';
    }
}
