package com.im.service.storage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 文件记录实体类
 * 用于存储上传文件的基本信息和元数据
 */
@Data
@Entity
@Table(name = "file_records", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_file_hash", columnList = "fileHash"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件唯一标识
     */
    @Column(unique = true, nullable = false, length = 64)
    private String fileId;

    /**
     * 上传用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 原始文件名
     */
    @Column(nullable = false, length = 255)
    private String originalName;

    /**
     * 存储文件名（带UUID）
     */
    @Column(nullable = false, length = 255)
    private String storedName;

    /**
     * 文件存储路径
     */
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * 文件访问URL
     */
    @Column(length = 500)
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    @Column(nullable = false, length = 100)
    private String mimeType;

    /**
     * 文件类型分类：IMAGE, VIDEO, AUDIO, DOCUMENT, OTHER
     */
    @Column(nullable = false, length = 20)
    private String fileType;

    /**
     * 文件内容哈希（用于去重）
     */
    @Column(length = 64)
    private String fileHash;

    /**
     * 文件扩展名
     */
    @Column(length = 20)
    private String extension;

    /**
     * 文件描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 是否公开访问
     */
    @Column(nullable = false)
    private Boolean isPublic;

    /**
     * 下载次数
     */
    @Column(nullable = false)
    private Integer downloadCount;

    /**
     * 文件状态：ACTIVE, DELETED, EXPIRED
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 过期时间（null表示永不过期）
     */
    private LocalDateTime expireAt;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (downloadCount == null) {
            downloadCount = 0;
        }
        if (isPublic == null) {
            isPublic = false;
        }
        if (status == null) {
            status = FileStatus.ACTIVE.name();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 文件状态枚举
     */
    public enum FileStatus {
        ACTIVE,     // 正常
        DELETED,    // 已删除
        EXPIRED     // 已过期
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        IMAGE,      // 图片
        VIDEO,      // 视频
        AUDIO,      // 音频
        DOCUMENT,   // 文档
        OTHER       // 其他
    }

    /**
     * 增加下载次数
     */
    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    /**
     * 标记为已删除
     */
    public void markAsDeleted() {
        this.status = FileStatus.DELETED.name();
    }

    /**
     * 标记为已过期
     */
    public void markAsExpired() {
        this.status = FileStatus.EXPIRED.name();
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        if (expireAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireAt);
    }

    /**
     * 检查是否可访问
     */
    public boolean isAccessible() {
        return FileStatus.ACTIVE.name().equals(status) && !isExpired();
    }

    /**
     * 格式化文件大小
     */
    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024));
        }
    }
}
