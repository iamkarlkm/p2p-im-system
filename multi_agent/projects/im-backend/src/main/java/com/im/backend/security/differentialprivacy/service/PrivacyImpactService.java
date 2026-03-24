package com.im.backend.security.differentialprivacy.service;

import com.im.backend.security.differentialprivacy.entity.PrivacyImpactEntity;
import com.im.backend.security.differentialprivacy.entity.PrivacyImpactEntity.OperationType;
import com.im.backend.security.differentialprivacy.repository.PrivacyImpactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 隐私影响评估服务
 * 执行隐私影响评估、风险分析和合规性检查
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrivacyImpactService {
    
    private final PrivacyImpactRepository impactRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public PrivacyImpactEntity createAssessment(PrivacyImpactEntity assessment) {
        log.info("Creating privacy impact assessment for operation: {}", assessment.getOperationId());
        assessment.setProcessingSuccess(true);
        assessment.setComplianceCheckPassed(true);
        return impactRepository.save(assessment);
    }
    
    @Transactional
    public PrivacyImpactEntity completeAssessment(Long id) {
        log.info("Completing privacy impact assessment: {}", id);
        return impactRepository.findById(id)
            .map(assessment -> {
                assessment.completeAssessment();
                assessment.setCompletedAt(LocalDateTime.now());
                return impactRepository.save(assessment);
            })
            .orElseThrow(() -> new RuntimeException("Assessment not found: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getByOperationId(String operationId) {
        return impactRepository.findByOperationId(operationId);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getByUserId(String userId) {
        return impactRepository.findByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public Page<PrivacyImpactEntity> getByUserIdPage(String userId, Pageable pageable) {
        return impactRepository.findByUserId(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<PrivacyImpactEntity> getByOperationType(OperationType operationType, Pageable pageable) {
        return impactRepository.findByOperationType(operationType.name(), pageable);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return impactRepository.findByTimeRange(startTime, endTime);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getByImpactSeverity(String severity, Pageable pageable) {
        return impactRepository.findByImpactSeverity(severity, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getByRiskLevel(String riskLevel, Pageable pageable) {
        return impactRepository.findByRiskLevel(riskLevel, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getFailedComplianceChecks(Pageable pageable) {
        return impactRepository.findFailedComplianceChecks(pageable);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyImpactEntity> getHighRiskAssessments(Double threshold, Pageable pageable) {
        return impactRepository.findHighRiskAssessments(threshold, pageable);
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByOperationType() {
        List<Object[]> results = impactRepository.countByOperationType();
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (Long) result[1]);
        }
        return map;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByImpactSeverity() {
        List<Object[]> results = impactRepository.countByImpactSeverity();
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (Long) result[1]);
        }
        return map;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByRiskLevel() {
        List<Object[]> results = impactRepository.countByRiskLevel();
        Map<String, Long> map = new HashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (Long) result[1]);
        }
        return map;
    }
    
    @Transactional(readOnly = true)
    public Double getAverageEpsilonByOperationType(OperationType operationType) {
        return impactRepository.getAverageEpsilonConsumedByOperationType(operationType.name());
    }
    
    @Transactional(readOnly = true)
    public Double getTotalEpsilonByUser(String userId) {
        return impactRepository.getTotalEpsilonConsumedByUser(userId);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageImpactScoreInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return impactRepository.getAverageImpactScoreInTimeRange(startTime, endTime);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageRiskScoreInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return impactRepository.getAverageRiskScoreInTimeRange(startTime, endTime);
    }
    
    @Transactional(readOnly = true)
    public Long countByOperationTypeAndTimeRange(OperationType operationType, LocalDateTime startTime, LocalDateTime endTime) {
        return impactRepository.countByOperationTypeAndTimeRange(operationType.name(), startTime, endTime);
    }
    
    public String generateRecommendationsJson(List<String> recommendations) throws JsonProcessingException {
        return objectMapper.writeValueAsString(recommendations);
    }
    
    public String generateAuditTrailJson(Map<String, Object> auditData) throws JsonProcessingException {
        return objectMapper.writeValueAsString(auditData);
    }
}