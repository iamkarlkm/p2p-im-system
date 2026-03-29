package com.im.backend.security.differentialprivacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 隐私预算实体
 * 跟踪每个用户/会话的差分隐私预算消耗
 */
@Entity
@Table(name = "privacy_budget")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyBudgetEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "budget_type", nullable = false)
    private String budgetType;
    
    @Column(name = "total_budget", nullable = false)
    private Double totalBudget;
    
    @Column(name = "consumed_budget", nullable = false)
    private Double consumedBudget;
    
    @Column(name = "remaining_budget", nullable = false)
    private Double remainingBudget;
    
    @Column(name = "budget_period", nullable = false)
    private String budgetPeriod;
    
    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;
    
    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;
    
    @Column(name = "last_consumed_at")
    private LocalDateTime lastConsumedAt;
    
    @Column(name = "consumption_count")
    private Long consumptionCount;
    
    @Column(name = "avg_epsilon_per_consumption")
    private Double avgEpsilonPerConsumption;
    
    @Column(name = "max_epsilon_per_consumption")
    private Double maxEpsilonPerConsumption;
    
    @Column(name = "violation_count")
    private Long violationCount;
    
    @Column(name = "warning_threshold")
    private Double warningThreshold;
    
    @Column(name = "block_threshold")
    private Double blockThreshold;
    
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    
    @Column(name = "block_reason")
    private String blockReason;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "metadata_json")
    private String metadataJson;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (consumedBudget == null) {
            consumedBudget = 0.0;
        }
        if (remainingBudget == null && totalBudget != null) {
            remainingBudget = totalBudget - consumedBudget;
        }
        if (consumptionCount == null) {
            consumptionCount = 0L;
        }
        if (violationCount == null) {
            violationCount = 0L;
        }
        if (warningThreshold == null) {
            warningThreshold = 0.8; // 80% 警告阈值
        }
        if (blockThreshold == null) {
            blockThreshold = 1.0; // 100% 阻止阈值
        }
        if (isBlocked == null) {
            isBlocked = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void consumeBudget(Double epsilon) {
        this.consumedBudget += epsilon;
        this.remainingBudget = this.totalBudget - this.consumedBudget;
        this.consumptionCount++;
        this.lastConsumedAt = LocalDateTime.now();
        
        // 更新统计
        if (this.avgEpsilonPerConsumption == null) {
            this.avgEpsilonPerConsumption = epsilon;
        } else {
            this.avgEpsilonPerConsumption = (this.avgEpsilonPerConsumption * (this.consumptionCount - 1) + epsilon) / this.consumptionCount;
        }
        
        if (this.maxEpsilonPerConsumption == null || epsilon > this.maxEpsilonPerConsumption) {
            this.maxEpsilonPerConsumption = epsilon;
        }
        
        // 检查阈值
        if (this.remainingBudget / this.totalBudget <= this.blockThreshold) {
            this.isBlocked = true;
            this.blockReason = "Privacy budget exceeded block threshold";
            this.violationCount++;
        } else if (this.remainingBudget / this.totalBudget <= this.warningThreshold) {
            this.blockReason = "Privacy budget approaching limit";
        }
    }
    
    public boolean canConsume(Double epsilon) {
        if (isBlocked) {
            return false;
        }
        return (consumedBudget + epsilon) <= totalBudget;
    }
    
    public enum BudgetType {
        USER_LIFETIME,
        SESSION_TEMPORARY,
        DATASET_SPECIFIC,
        QUERY_BASED,
        TIERED
    }
    
    public enum BudgetPeriod {
        DAILY,
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        LIFETIME,
        SESSION,
        CUSTOM
    }
}