package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 文件上传响应 DTO - CDN/MinIO 文件服务
 */
public class FileUploadResponse {
    private String fileId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String mimeType;
    private String downloadUrl;
    private String thumbnailUrl;
    private LocalDateTime uploadTime;

    public FileUploadResponse() {}

    public static FileUploadResponse success(String fileId, String fileName, String fileType,
            Long fileSize, String mimeType, String downloadUrl) {
        FileUploadResponse response = new FileUploadResponse();
        response.fileId = fileId;
        response.fileName = fileName;
        response.fileType = fileType;
        response.fileSize = fileSize;
        response.mimeType = mimeType;
        response.downloadUrl = downloadUrl;
        response.uploadTime = LocalDateTime.now();
        return response;
    }

    // Getters and Setters
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }
}
