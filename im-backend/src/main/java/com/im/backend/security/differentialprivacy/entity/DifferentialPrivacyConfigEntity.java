package com.im.backend.security.differentialprivacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 差分隐私配置实体
 * 存储差分隐私框架的配置参数
 */
@Entity
@Table(name = "differential_privacy_config")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifferentialPrivacyConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;
    
    @Column(name = "config_value", length = 2000)
    private String configValue;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "data_type", nullable = false)
    private String dataType;
    
    @Column(name = "is_sensitive")
    private Boolean isSensitive;
    
    @Column(name = "privacy_budget_limit")
    private Double privacyBudgetLimit;
    
    @Column(name = "epsilon")
    private Double epsilon;
    
    @Column(name = "delta")
    private Double delta;
    
    @Column(name = "noise_mechanism")
    private String noiseMechanism;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval;
    
    @Column(name = "approval_status")
    private String approvalStatus;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (version == null) {
            version = 1;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum DataType {
        DOUBLE,
        INTEGER,
        STRING,
        BOOLEAN,
        JSON,
        LIST,
        MAP
    }
    
    public enum NoiseMechanism {
        LAPLACE,
        GAUSSIAN,
        EXPONENTIAL,
        CAUCHY,
        CUSTOM
    }
    
    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED,
        AUTO_APPROVED
    }
}