package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 差分隐私配置实体
 * 用于管理 AI 训练和数据分析中的隐私保护参数
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "differential_privacy_config", indexes = {
    @Index(name = "idx_dpc_status", columnList = "status"),
    @Index(name = "idx_dpc_created_at", columnList = "createdAt")
})
public class DifferentialPrivacyConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 配置名称 */
    @Column(nullable = false, length = 100)
    private String configName;

    /** 配置描述 */
    @Column(length = 500)
    private String description;

    /** 隐私预算 epsilon 值 (越小越隐私，通常 0.1-10) */
    @Column(nullable = false, precision = 10, scale = 6)
    private Double epsilonBudget;

    /** 隐私预算 delta 值 (通常 1e-5 或更小) */
    @Column(nullable = false, precision = 15, scale = 12)
    private Double deltaBudget;

    /** 噪声机制类型：LAPLACE/GAUSSIAN/EXPONENTIAL */
    @Column(nullable = false, length = 20)
    private String noiseMechanism;

    /** 是否启用差分隐私 */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 应用场景：MODEL_TRAINING/DATA_ANALYSIS/QUERY_RESPONSE/FEDERATED_LEARNING */
    @Column(nullable = false, length = 30)
    private String applicationScenario;

    /** 数据最小化级别：MINIMAL/STANDARD/COMPREHENSIVE */
    @Column(length = 20)
    private String dataMinimizationLevel;

    /** 隐私影响评估分数 (0-100) */
    @Column(precision = 5, scale = 2)
    private Double privacyImpactScore;

    /** 累计隐私预算消耗 */
    @Column(precision = 10, scale = 6, defaultValue = "0.0")
    private Double budgetConsumed = 0.0;

    /** 预算刷新周期：DAILY/WEEKLY/MONTHLY */
    @Column(length = 20)
    private String budgetRefreshCycle;

    /** 上次预算刷新时间 */
    private LocalDateTime lastBudgetRefresh;

    /** 状态：ACTIVE/SUSPENDED/EXHAUSTED */
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    /** 创建者 ID */
    @Column(nullable = false)
    private Long creatorId;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
