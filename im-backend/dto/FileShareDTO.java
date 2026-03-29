package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 文件分享DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileShareDTO {

    /** 是否成功 */
    private boolean success;

    /** 文件ID */
    private String fileId;

    /** 原始文件名 */
    private String originalName;

    /** 文件大小（字节） */
    private long fileSize;

    /** 文件类型 */
    private String contentType;

    /** 存储路径 */
    private String storagePath;

    /** 分享链接 */
    private String shareLink;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryTime;

    /** 下载次数 */
    private int downloadCount;

    /** 文件描述 */
    private String description;

    /** 是否已过期 */
    private boolean expired;

    /** 消息 */
    private String message;

    /** 错误代码 */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    // ========== 构造方法 ==========

    public FileShareDTO() {
    }

    public FileShareDTO(boolean success) {
        this.success = success;
    }

    // ========== Getters & Setters ==========

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

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

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // ========== 便捷方法 ==========

    /**
     * 格式化文件大小显示
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public static FileShareDTO success(String fileId) {
        FileShareDTO dto = new FileShareDTO();
        dto.setSuccess(true);
        dto.setFileId(fileId);
        return dto;
    }

    public static FileShareDTO error(String errorCode, String errorMessage) {
        FileShareDTO dto = new FileShareDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    @Override
    public String toString() {
        return "FileShareDTO{" +
            "success=" + success +
            ", fileId='" + fileId + '\'' +
            ", originalName='" + originalName + '\'' +
            ", fileSize=" + fileSize +
            '}';
    }
}
