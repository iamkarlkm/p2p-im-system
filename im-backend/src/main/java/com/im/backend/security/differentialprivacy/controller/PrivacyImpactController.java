package com.im.backend.security.differentialprivacy.controller;

import com.im.backend.security.differentialprivacy.entity.PrivacyImpactEntity;
import com.im.backend.security.differentialprivacy.entity.PrivacyImpactEntity.OperationType;
import com.im.backend.security.differentialprivacy.service.PrivacyImpactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 隐私影响评估控制器
 * 提供隐私影响评估的 REST API
 */
@RestController
@RequestMapping("/api/v1/differential-privacy/impact")
@RequiredArgsConstructor
@Slf4j
public class PrivacyImpactController {
    
    private final PrivacyImpactService impactService;
    
    @PostMapping
    public ResponseEntity<PrivacyImpactEntity> createAssessment(@RequestBody PrivacyImpactEntity assessment) {
        log.info("Creating privacy impact assessment for operation: {}", assessment.getOperationId());
        try {
            PrivacyImpactEntity created = impactService.createAssessment(assessment);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create assessment", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeAssessment(@PathVariable Long id) {
        log.info("Completing assessment: {}", id);
        try {
            PrivacyImpactEntity completed = impactService.completeAssessment(id);
            return ResponseEntity.ok(completed);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/operation/{operationId}")
    public ResponseEntity<List<PrivacyImpactEntity>> getByOperationId(@PathVariable String operationId) {
        log.info("Getting assessments for operation: {}", operationId);
        return ResponseEntity.ok(impactService.getByOperationId(operationId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PrivacyImpactEntity>> getByUserId(@PathVariable String userId) {
        log.info("Getting assessments for user: {}", userId);
        return ResponseEntity.ok(impactService.getByUserId(userId));
    }
    
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<PrivacyImpactEntity>> getByUserIdPage(
            @PathVariable String userId,
            Pageable pageable) {
        log.info("Getting assessments page for user: {}", userId);
        return ResponseEntity.ok(impactService.getByUserIdPage(userId, pageable));
    }
    
    @GetMapping("/operation-type/{operationType}")
    public ResponseEntity<Page<PrivacyImpactEntity>> getByOperationType(
            @PathVariable String operationType,
            Pageable pageable) {
        log.info("Getting assessments for operation type: {}", operationType);
        try {
            OperationType type = OperationType.valueOf(operationType);
            return ResponseEntity.ok(impactService.getByOperationType(type, pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/time-range")
    public ResponseEntity<List<PrivacyImpactEntity>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Getting assessments in time range: {} to {}", startTime, endTime);
        return ResponseEntity.ok(impactService.getByTimeRange(startTime, endTime));
    }
    
    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<PrivacyImpactEntity>> getByImpactSeverity(
            @PathVariable String severity,
            Pageable pageable) {
        log.info("Getting assessments with severity: {}", severity);
        return ResponseEntity.ok(impactService.getByImpactSeverity(severity, pageable));
    }
    
    @GetMapping("/risk/{riskLevel}")
    public ResponseEntity<List<PrivacyImpactEntity>> getByRiskLevel(
            @PathVariable String riskLevel,
            Pageable pageable) {
        log.info("Getting assessments with risk level: {}", riskLevel);
        return ResponseEntity.ok(impactService.getByRiskLevel(riskLevel, pageable));
    }
    
    @GetMapping("/compliance/failed")
    public ResponseEntity<List<PrivacyImpactEntity>> getFailedComplianceChecks(Pageable pageable) {
        log.info("Getting failed compliance checks");
        return ResponseEntity.ok(impactService.getFailedComplianceChecks(pageable));
    }
    
    @GetMapping("/risk/high")
    public ResponseEntity<List<PrivacyImpactEntity>> getHighRiskAssessments(
            @RequestParam Double threshold,
            Pageable pageable) {
        log.info("Getting high risk assessments with threshold: {}", threshold);
        return ResponseEntity.ok(impactService.getHighRiskAssessments(threshold, pageable));
    }
    
    @GetMapping("/stats/operation-type")
    public ResponseEntity<Map<String, Long>> getCountByOperationType() {
        log.info("Getting count by operation type");
        return ResponseEntity.ok(impactService.getCountByOperationType());
    }
    
    @GetMapping("/stats/severity")
    public ResponseEntity<Map<String, Long>> getCountByImpactSeverity() {
        log.info("Getting count by impact severity");
        return ResponseEntity.ok(impactService.getCountByImpactSeverity());
    }
    
    @GetMapping("/stats/risk-level")
    public ResponseEntity<Map<String, Long>> getCountByRiskLevel() {
        log.info("Getting count by risk level");
        return ResponseEntity.ok(impactService.getCountByRiskLevel());
    }
    
    @GetMapping("/stats/epsilon/operation-type/{operationType}")
    public ResponseEntity<?> getAverageEpsilonByOperationType(@PathVariable String operationType) {
        log.info("Getting average epsilon for operation type: {}", operationType);
        try {
            OperationType type = OperationType.valueOf(operationType);
            Double avgEpsilon = impactService.getAverageEpsilonByOperationType(type);
            Map<String, Double> result = new HashMap<>();
            result.put("averageEpsilon", avgEpsilon);
            return ResponseEntity.ok(avgEpsilon != null ? result : ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats/epsilon/user/{userId}")
    public ResponseEntity<Map<String, Double>> getTotalEpsilonByUser(@PathVariable String userId) {
        log.info("Getting total epsilon for user: {}", userId);
        Map<String, Double> result = new HashMap<>();
        result.put("totalEpsilon", impactService.getTotalEpsilonByUser(userId));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/stats/impact-score")
    public ResponseEntity<Map<String, Double>> getAverageImpactScore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Getting average impact score in time range");
        Map<String, Double> result = new HashMap<>();
        result.put("averageImpactScore", impactService.getAverageImpactScoreInTimeRange(startTime, endTime));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/stats/risk-score")
    public ResponseEntity<Map<String, Double>> getAverageRiskScore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Getting average risk score in time range");
        Map<String, Double> result = new HashMap<>();
        result.put("averageRiskScore", impactService.getAverageRiskScoreInTimeRange(startTime, endTime));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        log.info("Getting overview statistics");
        Map<String, Object> stats = new HashMap<>();
        stats.put("byOperationType", impactService.getCountByOperationType());
        stats.put("bySeverity", impactService.getCountByImpactSeverity());
        stats.put("byRiskLevel", impactService.getCountByRiskLevel());
        return ResponseEntity.ok(stats);
    }
}