package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 备份请求DTO
 */
public class BackupRequest {

    private String name;
    private String component; // MESSAGE, USER, FILE, FULL
    private String type; // FULL, INCREMENTAL, DIFFERENTIAL
    private String storageType; // LOCAL, S3, MINIO
    private String storagePath;
    private String cronExpression;
    private Integer retentionDays;
    private Boolean enabled;
    private Boolean encryptionEnabled;
    private String compressionType; // NONE, GZIP, ZIP
    private Long maxBackupSize;
    private Integer maxConcurrentBackups;
    private String description;

    // 即时备份请求字段
    private Boolean immediate;
    private Long restoreRecordId; // 用于恢复操作

    // Getters and Setters
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

    public Boolean getImmediate() { return immediate; }
    public void setImmediate(Boolean immediate) { this.immediate = immediate; }

    public Long getRestoreRecordId() { return restoreRecordId; }
    public void setRestoreRecordId(Long restoreRecordId) { this.restoreRecordId = restoreRecordId; }
}
