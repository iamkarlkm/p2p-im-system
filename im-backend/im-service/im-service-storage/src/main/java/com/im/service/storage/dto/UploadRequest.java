package com.im.service.storage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 文件上传请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {

    /**
     * 文件内容（Base64编码）
     */
    @NotBlank(message = "文件内容不能为空")
    private String fileContent;

    /**
     * 原始文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String originalName;

    /**
     * 文件描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    /**
     * 是否公开访问
     */
    @NotNull(message = "访问权限不能为空")
    private Boolean isPublic;

    /**
     * 过期时间（分钟，null表示永不过期）
     */
    private Integer expireMinutes;

    /**
     * 文件类型：IMAGE, VIDEO, AUDIO, DOCUMENT, OTHER
     */
    @Size(max = 20, message = "文件类型长度不能超过20个字符")
    private String fileType;

    /**
     * 是否使用文件去重
     */
    private Boolean useDeduplication;

    // 便捷方法：判断是否使用去重
    public boolean isUseDeduplication() {
        return useDeduplication != null && useDeduplication;
    }

    // 便捷方法：获取实际文件类型
    public String getEffectiveFileType() {
        if (fileType != null && !fileType.isEmpty()) {
            return fileType.toUpperCase();
        }
        // 根据扩展名推断类型
        String ext = getExtension();
        return switch (ext.toLowerCase()) {
            case "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg" -> "IMAGE";
            case "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm" -> "VIDEO";
            case "mp3", "wav", "aac", "flac", "ogg", "m4a" -> "AUDIO";
            case "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt" -> "DOCUMENT";
            default -> "OTHER";
        };
    }

    // 获取文件扩展名
    private String getExtension() {
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf(".") + 1);
    }

    // 验证请求
    public boolean isValid() {
        return fileContent != null && !fileContent.isBlank()
                && originalName != null && !originalName.isBlank()
                && isPublic != null;
    }
}
