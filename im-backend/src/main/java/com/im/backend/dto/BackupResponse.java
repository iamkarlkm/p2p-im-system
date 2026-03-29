package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 备份响应DTO
 */
public class BackupResponse {

    private Long id;
    private String name;
    private String component;
    private String type;
    private String storageType;
    private String storagePath;
    private String cronExpression;
    private Integer retentionDays;
    private Boolean enabled;
    private Boolean encryptionEnabled;
    private String compressionType;
    private Long maxBackupSize;
    private Integer maxConcurrentBackups;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 备份记录字段
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    private Long fileSize;
    private Integer recordCount;
    private String errorMessage;
    private String fileName;
    private String checksum;

    // 统计字段
    private Long totalBackups;
    private Long successfulBackups;
    private Long failedBackups;
    private Long totalStorageUsed;
    private LocalDateTime lastBackupTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public Integer getRetentionDays() { return retentionDays; }
    public void setRetentionDays(Integer retentionDays) { this.retentionDays = retentionDays; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Boolean getEncryptionEnabled() { return encryptionEnabled; }
    public void setEncryptionEnabled(Boolean encryptionEnabled) { this.encryptionEnabled = encryptionEnabled; }

    public String getCompressionType() { return compressionType; }
    public void setCompressionType(String compressionType) { this.compressionType = compressionType; }

    public Long getMaxBackupSize() { return maxBackupSize; }
    public void setMaxBackupSize(Long maxBackupSize) { this.maxBackupSize = maxBackupSize; }

    public Integer getMaxConcurrentBackups() { return maxConcurrentBackups; }
    public void setMaxConcurrentBackups(Integer maxConcurrentBackups) { this.maxConcurrentBackups = maxConcurrentBackups; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getRecordCount() { return recordCount; }
    public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }

    public Long getTotalBackups() { return totalBackups; }
    public void setTotalBackups(Long totalBackups) { this.totalBackups = totalBackups; }

    public Long getSuccessfulBackups() { return successfulBackups; }
    public void setSuccessfulBackups(Long successfulBackups) { this.successfulBackups = successfulBackups; }

    public Long getFailedBackups() { return failedBackups; }
    public void setFailedBackups(Long failedBackups) { this.failedBackups = failedBackups; }

    public Long getTotalStorageUsed() { return totalStorageUsed; }
    public void setTotalStorageUsed(Long totalStorageUsed) { this.totalStorageUsed = totalStorageUsed; }

    public LocalDateTime getLastBackupTime() { return lastBackupTime; }
    public void setLastBackupTime(LocalDateTime lastBackupTime) { this.lastBackupTime = lastBackupTime; }
}
