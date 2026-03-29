package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FileUploadResponseDTO {

    private String uploadId;
    private String fileName;
    private Long fileSize;
    private String status;
    private boolean completed;
    private boolean instantUpload;
    private boolean resumeUpload;
    private int progress;
    private int totalChunks;
    private int chunkSize;
    private String fileUrl;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private List<Integer> uploadedChunks;
    private String checksum;

    // Getters and Setters
    public String getUploadId() { return uploadId; }
    public void setUploadId(String uploadId) { this.uploadId = uploadId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isInstantUpload() { return instantUpload; }
    public void setInstantUpload(boolean instantUpload) { this.instantUpload = instantUpload; }

    public boolean isResumeUpload() { return resumeUpload; }
    public void setResumeUpload(boolean resumeUpload) { this.resumeUpload = resumeUpload; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTotalChunks() { return totalChunks; }
    public void setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; }

    public int getChunkSize() { return chunkSize; }
    public void setChunkSize(int chunkSize) { this.chunkSize = chunkSize; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<Integer> getUploadedChunks() { return uploadedChunks; }
    public void setUploadedChunks(List<Integer> uploadedChunks) { this.uploadedChunks = uploadedChunks; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    @Override
    public String toString() {
        return "FileUploadResponseDTO{uploadId='" + uploadId + "', status='" + status + "', progress=" + progress + "}'";
    }
}
