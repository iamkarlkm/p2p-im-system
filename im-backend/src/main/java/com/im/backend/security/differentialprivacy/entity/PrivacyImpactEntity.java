package com.im.backend.security.differentialprivacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 隐私影响评估实体
 * 记录每次隐私操作的详细影响分析
 */
@Entity
@Table(name = "privacy_impact")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyImpactEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "operation_id", nullable = false)
    private String operationId;
    
    @Column(name = "operation_type", nullable = false)
    private String operationType;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "ai_model_id")
    private String aiModelId;
    
    @Column(name = "dataset_id")
    private String datasetId;
    
    @Column(name = "epsilon_consumed", nullable = false)
    private Double epsilonConsumed;
    
    @Column(name = "delta_consumed")
    private Double deltaConsumed;
    
    @Column(name = "privacy_budget_before")
    private Double privacyBudgetBefore;
    
    @Column(name = "privacy_budget_after")
    private Double privacyBudgetAfter;
    
    @Column(name = "data_sensitivity_score")
    private Double dataSensitivityScore;
    
    @Column(name = "impact_severity")
    private String impactSeverity;
    
    @Column(name = "risk_level")
    private String riskLevel;
    
    @Column(name = "mitigation_measures_applied")
    private String mitigationMeasuresApplied;
    
    @Column(name = "compliance_check_passed")
    private Boolean complianceCheckPassed;
    
    @Column(name = "data_minimization_score")
    private Double dataMinimizationScore;
    
    @Column(name = "purpose_limitation_score")
    private Double purposeLimitationScore;
    
    @Column(name = "storage_limitation_score")
    private Double storageLimitationScore;
    
    @Column(name = "integrity_confidentiality_score")
    private Double integrityConfidentialityScore;
    
    @Column(name = "accountability_score")
    private Double accountabilityScore;
    
    @Column(name = "overall_impact_score")
    private Double overallImpactScore;
    
    @Column(name = "recommendations_json")
    private String recommendationsJson;
    
    @Column(name = "audit_trail_json")
    private String auditTrailJson;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "processing_success")
    private Boolean processingSuccess;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "user_consent_obtained")
    private Boolean userConsentObtained;
    
    @Column(name = "consent_timestamp")
    private LocalDateTime consentTimestamp;
    
    @Column(name = "data_retention_period_days")
    private Integer dataRetentionPeriodDays;
    
    @Column(name = "reidentification_risk_score")
    private Double reidentificationRiskScore;
    
    @Column(name = "inference_attack_risk_score")
    private Double inferenceAttackRiskScore;
    
    @Column(name = "membership_inference_risk_score")
    private Double membershipInferenceRiskScore;
    
    @Column(name = "attribute_inference_risk_score")
    private Double attributeInferenceRiskScore;
    
    @Column(name = "overall_risk_score")
    private Double overallRiskScore;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (processingSuccess == null) {
            processingSuccess = true;
        }
        if (complianceCheckPassed == null) {
            complianceCheckPassed = true;
        }
        if (userConsentObtained == null) {
            userConsentObtained = false;
        }
    }
    
    public void completeAssessment() {
        completedAt = LocalDateTime.now();
        if (createdAt != null && completedAt != null) {
            processingTimeMs = java.time.Duration.between(createdAt, completedAt).toMillis();
        }
        
        // 计算总体影响分数
        calculateOverallImpactScore();
        calculateOverallRiskScore();
    }
    
    private void calculateOverallImpactScore() {
        double totalScore = 0.0;
        int count = 0;
        
        if (dataMinimizationScore != null) { totalScore += dataMinimizationScore; count++; }
        if (purposeLimitationScore != null) { totalScore += purposeLimitationScore; count++; }
        if (storageLimitationScore != null) { totalScore += storageLimitationScore; count++; }
        if (integrityConfidentialityScore != null) { totalScore += integrityConfidentialityScore; count++; }
        if (accountabilityScore != null) { totalScore += accountabilityScore; count++; }
        
        if (count > 0) {
            overallImpactScore = totalScore / count;
            
            // 确定影响严重性
            if (overallImpactScore >= 8.0) {
                impactSeverity = "CRITICAL";
                riskLevel = "HIGH";
            } else if (overallImpactScore >= 6.0) {
                impactSeverity = "HIGH";
                riskLevel = "MEDIUM_HIGH";
            } else if (overallImpactScore >= 4.0) {
                impactSeverity = "MEDIUM";
                riskLevel = "MEDIUM";
            } else if (overallImpactScore >= 2.0) {
                impactSeverity = "LOW";
                riskLevel = "LOW";
            } else {
                impactSeverity = "MINIMAL";
                riskLevel = "VERY_LOW";
            }
        }
    }
    
    private void calculateOverallRiskScore() {
        double totalRisk = 0.0;
        int count = 0;
        
        if (reidentificationRiskScore != null) { totalRisk += reidentificationRiskScore; count++; }
        if (inferenceAttackRiskScore != null) { totalRisk += inferenceAttackRiskScore; count++; }
        if (membershipInferenceRiskScore != null) { totalRisk += membershipInferenceRiskScore; count++; }
        if (attributeInferenceRiskScore != null) { totalRisk += attributeInferenceRiskScore; count++; }
        
        if (count > 0) {
            overallRiskScore = totalRisk / count;
        }
    }
    
    public enum OperationType {
        AI_TRAINING,
        DATA_COLLECTION,
        QUERY_EXECUTION,
        MODEL_INFERENCE,
        DATA_ANALYSIS,
        DATA_SHARING,
        MODEL_DEPLOYMENT,
        PRIVACY_PRESERVING_COMPUTATION,
        FEDERATED_LEARNING_ROUND,
        SECURE_AGGREGATION
    }
    
    public enum ImpactSeverity {
        MINIMAL,
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    public enum RiskLevel {
        VERY_LOW,
        LOW,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH,
        VERY_HIGH
    }
}