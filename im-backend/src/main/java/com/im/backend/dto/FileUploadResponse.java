package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 文件上传响应DTO
 * 功能#17: 文件上传下载
 */
public class FileUploadResponse {
    
    private Long id;
    private String fileName;
    private String storedName;
    private Long fileSize;
    private String mimeType;
    private String downloadUrl;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    public FileUploadResponse() {}
    
    public FileUploadResponse(Long id, String fileName, String storedName, Long fileSize, 
                             String mimeType, String downloadUrl, Boolean isPublic, 
                             LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.fileName = fileName;
        this.storedName = storedName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.downloadUrl = downloadUrl;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
