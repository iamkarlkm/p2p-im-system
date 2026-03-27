package com.im.quantum.performance.controller;

import com.im.quantum.performance.entity.PerformanceMetricsEntity;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.AlertLevel;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.MetricType;
import com.im.quantum.performance.service.QuantumPerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 量子通信性能分析控制器
 * RESTful API 端点
 * 
 * @author Quantum Performance Team
 * @since 2026-03-26
 */
@RestController
@RequestMapping("/api/v1/quantum/performance")
@CrossOrigin(origins = "*")
public class PerformanceAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAnalysisController.class);
    
    @Autowired
    private QuantumPerformanceService performanceService;
    
    /**
     * 记录性能指标
     */
    @PostMapping("/metrics")
    public ResponseEntity<PerformanceMetricsEntity> recordMetrics(
            @RequestBody PerformanceMetricsEntity metrics) {
        logger.info("Recording metrics for session: {}", metrics.getSessionId());
        PerformanceMetricsEntity saved = performanceService.recordMetrics(metrics);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 批量记录性能指标
     */
    @PostMapping("/metrics/batch")
    public ResponseEntity<List<PerformanceMetricsEntity>> recordMetricsBatch(
            @RequestBody List<PerformanceMetricsEntity> metricsList) {
        logger.info("Batch recording {} metrics entries", metricsList.size());
        List<PerformanceMetricsEntity> saved = performanceService.recordMetricsBatch(metricsList);
        return ResponseEntity.ok(saved);
    }
    
    /**
     * 获取最新性能指标
     */
    @GetMapping("/metrics/{sessionId}/latest")
    public ResponseEntity<Map<String, Object>> getLatestMetrics(
            @PathVariable String sessionId,
            @RequestParam(required = false) MetricType type) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (type != null) {
            PerformanceMetricsEntity metrics = performanceService.getLatestMetrics(sessionId, type);
            result.put("metrics", metrics);
        } else {
            Map<MetricType, PerformanceMetricsEntity> allMetrics = 
                performanceService.getSessionLatestMetrics(sessionId);
            result.put("metrics", allMetrics);
        }
        
        result.put("sessionId", sessionId);
        result.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取性能历史数据
     */
    @GetMapping("/metrics/{sessionId}/history")
    public ResponseEntity<Map<String, Object>> getMetricsHistory(
            @PathVariable String sessionId,
            @RequestParam MetricType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "100") int limit) {
        
        List<PerformanceMetricsEntity> history = performanceService.getMetricsHistory(
            sessionId, type, from, to, limit);
        
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("metricType", type);
        result.put("data", history);
        result.put("count", history.size());
        result.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 执行性能分析
     */
    @GetMapping("/analysis/{sessionId}")
    public ResponseEntity<QuantumPerformanceService.PerformanceAnalysis> analyzePerformance(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "3600") long periodSeconds) {
        
        Duration period = Duration.ofSeconds(periodSeconds);
        QuantumPerformanceService.PerformanceAnalysis analysis = 
            performanceService.analyzePerformance(sessionId, period);
        
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * 获取活动告警
     */
    @GetMapping("/alerts/active")
    public ResponseEntity<Map<String, Object>> getActiveAlerts(
            @RequestParam(defaultValue = "WARNING") AlertLevel minLevel) {
        
        List<PerformanceMetricsEntity> alerts = performanceService.getActiveAlerts(minLevel);
        
        Map<String, Object> result = new HashMap<>();
        result.put("alerts", alerts);
        result.put("count", alerts.size());
        result.put("minLevel", minLevel);
        result.put("timestamp", Instant.now().toString());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取告警统计
     */
    @GetMapping("/alerts/statistics")
    public ResponseEntity<QuantumPerformanceService.AlertStatistics> getAlertStatistics(
            @RequestParam(defaultValue = "86400") long periodSeconds) {
        
        Duration period = Duration.ofSeconds(periodSeconds);
        QuantumPerformanceService.AlertStatistics stats = 
            performanceService.getAlertStatistics(period);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 获取系统概览
     */
    @GetMapping("/overview")
    public ResponseEntity<QuantumPerformanceService.SystemOverview> getSystemOverview() {
        QuantumPerformanceService.SystemOverview overview = performanceService.getSystemOverview();
        return ResponseEntity.ok(overview);
    }
    
    /**
     * 获取性能阈值配置
     */
    @GetMapping("/config/thresholds")
    public ResponseEntity<Map<String, Object>> getThresholdsConfig() {
        Map<String, Object> config = new HashMap<>();
        
        Map<String, Double> qber = new HashMap<>();
        qber.put("warning", 0.11);
        qber.put("critical", 0.15);
        config.put("qber", qber);
        
        Map<String, Double> latency = new HashMap<>();
        latency.put("warning", 200.0);
        latency.put("critical", 500.0);
        config.put("latency", latency);
        
        Map<String, Double> packetLoss = new HashMap<>();
        packetLoss.put("warning", 0.03);
        packetLoss.put("critical", 0.05);
        config.put("packetLoss", packetLoss);
        
        Map<String, Double> stability = new HashMap<>();
        stability.put("warning", 0.5);
        stability.put("critical", 0.3);
        config.put("stability", stability);
        
        Map<String, Double> cpu = new HashMap<>();
        cpu.put("warning", 80.0);
        cpu.put("critical", 90.0);
        config.put("cpu", cpu);
        
        Map<String, Double> memory = new HashMap<>();
        memory.put("warning", 2048.0);
        memory.put("critical", 3072.0);
        config.put("memory", memory);
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "quantum-performance");
        health.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(health);
    }
}
