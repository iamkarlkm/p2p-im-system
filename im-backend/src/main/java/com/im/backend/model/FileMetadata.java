package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_storage_path", columnList = "storagePath", unique = true),
    @Index(name = "idx_file_hash", columnList = "fileHash")
})
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String storagePath;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 64)
    private String fileHash;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column(length = 50)
    private String fileType; // IMAGE, VIDEO, AUDIO, DOCUMENT, OTHER

    @Column(length = 500)
    private String thumbnailPath;

    // 图片/视频元数据
    @Column
    private Integer width;

    @Column
    private Integer height;

    @Column
    private Integer duration; // 视频/音频时长（秒）

    // 访问统计
    @Column
    private Long downloadCount;

    @Column
    private Long viewCount;

    @Column
    private Long lastAccessedAt;

    // 创建信息
    @Column(nullable = false)
    private Long uploadedBy;

    @Column
    private LocalDateTime createdAt;

    // 安全信息
    @Column
    private Boolean isPublic;

    @Column
    private String accessToken;

    @Column
    private LocalDateTime expiresAt;

    // 文件分类标签
    @Column(length = 255)
    private String tags;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Long getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Long downloadCount) { this.downloadCount = downloadCount; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public Long getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Long lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        downloadCount = 0L;
        viewCount = 0L;
        isPublic = false;
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
        this.lastAccessedAt = System.currentTimeMillis();
    }

    public void incrementViewCount() {
        this.viewCount++;
        this.lastAccessedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "FileMetadata{id=" + id + ", originalName='" + originalName + "', fileType='" + fileType + "'}";
    }
}
