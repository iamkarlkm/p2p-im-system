package com.im.service.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileResponse {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 格式化后的文件大小
     */
    private String formattedSize;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 是否公开访问
     */
    private Boolean isPublic;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 文件状态
     */
    private String status;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 过期时间
     */
    private LocalDateTime expireAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否过期
     */
    private Boolean isExpired;

    /**
     * 是否可访问
     */
    private Boolean isAccessible;

    // 便捷方法：检查是否过期
    public boolean checkExpired() {
        if (expireAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expireAt);
    }

    // 设置过期标志
    public void setExpiredFlag() {
        this.isExpired = checkExpired();
    }

    // 获取文件类型的中文名称
    public String getFileTypeName() {
        if (fileType == null) {
            return "其他";
        }
        return switch (fileType.toUpperCase()) {
            case "IMAGE" -> "图片";
            case "VIDEO" -> "视频";
            case "AUDIO" -> "音频";
            case "DOCUMENT" -> "文档";
            default -> "其他";
        };
    }

    // 检查是否是图片
    public boolean isImage() {
        return "IMAGE".equalsIgnoreCase(fileType);
    }

    // 检查是否是视频
    public boolean isVideo() {
        return "VIDEO".equalsIgnoreCase(fileType);
    }

    // 检查是否是音频
    public boolean isAudio() {
        return "AUDIO".equalsIgnoreCase(fileType);
    }

    // 检查是否是文档
    public boolean isDocument() {
        return "DOCUMENT".equalsIgnoreCase(fileType);
    }

    // 获取文件图标类型
    public String getFileIcon() {
        if (isImage()) return "image";
        if (isVideo()) return "video";
        if (isAudio()) return "audio";
        if (isDocument()) return "document";
        return "file";
    }
}
