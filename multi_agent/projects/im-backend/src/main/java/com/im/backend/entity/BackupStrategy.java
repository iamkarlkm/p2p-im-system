package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 备份策略实体
 */
@Entity
@Table(name = "backup_strategies")
public class BackupStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String component; // MESSAGE, USER, FILE, FULL

    @Column(nullable = false)
    private String type; // FULL, INCREMENTAL, DIFFERENTIAL

    @Column(nullable = false)
    private String storageType; // LOCAL, S3, MINIO

    private String storagePath;

    private String cronExpression;

    @Column(nullable = false)
    private Integer retentionDays;

    private Boolean enabled;

    private Boolean encryptionEnabled;

    private String compressionType; // NONE, GZIP, ZIP

    private Long maxBackupSize; // bytes

    private Integer maxConcurrentBackups;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enabled == null) enabled = true;
        if (retentionDays == null) retentionDays = 30;
        if (compressionType == null) compressionType = "GZIP";
        if (encryptionEnabled == null) encryptionEnabled = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

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

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}
