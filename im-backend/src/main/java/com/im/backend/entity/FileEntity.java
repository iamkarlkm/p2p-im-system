package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 文件实体 - CDN/MinIO 文件服务
 * 支持云端文件存储 + CDN 加速分发
 */
@Entity
@Table(name = "im_files", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_file_type", columnList = "fileType"),
    @Index(name = "idx_upload_time", columnList = "uploadTime")
})
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 文件唯一标识 (UUID)
    @Column(nullable = false, unique = true, length = 64)
    private String fileId;

    // 上传用户ID
    @Column(nullable = false, length = 64)
    private String userId;

    // 所属会话ID (可选)
    @Column(length = 64)
    private String conversationId;

    // 原文件名
    @Column(nullable = false, length = 255)
    private String originalName;

    // 存储文件名 (MinIO 对象名)
    @Column(nullable = false, length = 255)
    private String storageName;

    // 文件类型 (image/video/audio/document/other)
    @Column(nullable = false, length = 32)
    private String fileType;

    // MIME 类型
    @Column(nullable = false, length = 128)
    private String mimeType;

    // 文件大小 (字节)
    @Column(nullable = false)
    private Long fileSize;

    // 文件后缀
    @Column(length = 16)
    private String extension;

    // MinIO 存储路径
    @Column(nullable = false, length = 512)
    private String objectName;

    // CDN 访问URL (如果启用CDN)
    @Column(length = 512)
    private String cdnUrl;

    // 文件存储路径 (相对路径)
    @Column(length = 255)
    private String filePath;

    // 缩略图路径 (图片/视频)
    @Column(length = 512)
    private String thumbnailPath;

    // 上传时间
    @Column(nullable = false)
    private LocalDateTime uploadTime;

    // 过期时间 (可选)
    private LocalDateTime expireTime;

    // 是否已删除
    @Column(nullable = false)
    private Boolean deleted = false;

    // 删除时间
    private LocalDateTime deleteTime;

    // 文件状态 (uploading/completed/failed/deleted)
    @Column(nullable = false, length = 32)
    private String status = "uploading";

    // 下载次数
    @Column(nullable = false)
    private Integer downloadCount = 0;

    // MD5 校验值
    @Column(length = 64)
    private String md5Checksum;

    // 额外的元数据 (JSON格式)
    @Column(columnDefinition = "TEXT")
    private String metadata;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getStorageName() { return storageName; }
    public void setStorageName(String storageName) { this.storageName = storageName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public String getObjectName() { return objectName; }
    public void setObjectName(String objectName) { this.objectName = objectName; }

    public String getCdnUrl() { return cdnUrl; }
    public void setCdnUrl(String cdnUrl) { this.cdnUrl = cdnUrl; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getDeleteTime() { return deleteTime; }
    public void setDeleteTime(LocalDateTime deleteTime) { this.deleteTime = deleteTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }

    public String getMd5Checksum() { return md5Checksum; }
    public void setMd5Checksum(String md5Checksum) { this.md5Checksum = md5Checksum; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
