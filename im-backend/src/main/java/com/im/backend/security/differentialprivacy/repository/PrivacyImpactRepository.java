package com.im.backend.security.differentialprivacy.repository;

import com.im.backend.security.differentialprivacy.entity.PrivacyImpactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 隐私影响评估仓储接口
 */
@Repository
public interface PrivacyImpactRepository extends JpaRepository<PrivacyImpactEntity, Long> {
    
    List<PrivacyImpactEntity> findByOperationId(String operationId);
    
    List<PrivacyImpactEntity> findByUserId(String userId);
    
    List<PrivacyImpactEntity> findBySessionId(String sessionId);
    
    List<PrivacyImpactEntity> findByAiModelId(String aiModelId);
    
    List<PrivacyImpactEntity> findByDatasetId(String datasetId);
    
    Page<PrivacyImpactEntity> findByUserId(String userId, Pageable pageable);
    
    Page<PrivacyImpactEntity> findByOperationType(String operationType, Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.createdAt BETWEEN :startTime AND :endTime")
    List<PrivacyImpactEntity> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.impactSeverity = :severity ORDER BY pi.createdAt DESC")
    List<PrivacyImpactEntity> findByImpactSeverity(@Param("severity") String severity, Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.riskLevel = :riskLevel ORDER BY pi.createdAt DESC")
    List<PrivacyImpactEntity> findByRiskLevel(@Param("riskLevel") String riskLevel, Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.complianceCheckPassed = false")
    List<PrivacyImpactEntity> findFailedComplianceChecks(Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.processingSuccess = false")
    List<PrivacyImpactEntity> findFailedProcessing(Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.userConsentObtained = false")
    List<PrivacyImpactEntity> findMissingUserConsent(Pageable pageable);
    
    @Query("SELECT COUNT(pi) FROM PrivacyImpactEntity pi WHERE pi.operationType = :operationType AND pi.createdAt BETWEEN :startTime AND :endTime")
    Long countByOperationTypeAndTimeRange(@Param("operationType") String operationType, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(pi.epsilonConsumed) FROM PrivacyImpactEntity pi WHERE pi.operationType = :operationType")
    Double getAverageEpsilonConsumedByOperationType(@Param("operationType") String operationType);
    
    @Query("SELECT SUM(pi.epsilonConsumed) FROM PrivacyImpactEntity pi WHERE pi.userId = :userId")
    Double getTotalEpsilonConsumedByUser(@Param("userId") String userId);
    
    @Query("SELECT pi.operationType, COUNT(pi) FROM PrivacyImpactEntity pi GROUP BY pi.operationType")
    List<Object[]> countByOperationType();
    
    @Query("SELECT pi.impactSeverity, COUNT(pi) FROM PrivacyImpactEntity pi GROUP BY pi.impactSeverity")
    List<Object[]> countByImpactSeverity();
    
    @Query("SELECT pi.riskLevel, COUNT(pi) FROM PrivacyImpactEntity pi GROUP BY pi.riskLevel")
    List<Object[]> countByRiskLevel();
    
    @Query("SELECT AVG(pi.overallImpactScore) FROM PrivacyImpactEntity pi WHERE pi.createdAt BETWEEN :startTime AND :endTime")
    Double getAverageImpactScoreInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(pi.overallRiskScore) FROM PrivacyImpactEntity pi WHERE pi.createdAt BETWEEN :startTime AND :endTime")
    Double getAverageRiskScoreInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.overallRiskScore > :threshold ORDER BY pi.overallRiskScore DESC")
    List<PrivacyImpactEntity> findHighRiskAssessments(@Param("threshold") Double threshold, Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.reidentificationRiskScore > :threshold")
    List<PrivacyImpactEntity> findHighReidentificationRisk(@Param("threshold") Double threshold, Pageable pageable);
    
    @Query("SELECT pi FROM PrivacyImpactEntity pi WHERE pi.dataMinimizationScore < :threshold")
    List<PrivacyImpactEntity> findLowDataMinimization(@Param("threshold") Double threshold, Pageable pageable);
}