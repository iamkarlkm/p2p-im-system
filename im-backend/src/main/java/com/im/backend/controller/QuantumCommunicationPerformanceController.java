package com.im.backend.controller;

import com.im.backend.entity.QuantumCommunicationPerformanceEntity;
import com.im.backend.service.QuantumCommunicationPerformanceService;
import com.im.backend.repository.QuantumCommunicationPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 量子通信性能优化控制器
 * Quantum Communication Performance Optimization Controller
 * 
 * 提供量子密钥分发性能监控、链路质量评估、自适应协议切换等REST API
 */
@RestController
@RequestMapping("/api/v1/quantum-performance")
public class QuantumCommunicationPerformanceController {

    @Autowired
    private QuantumCommunicationPerformanceService performanceService;

    @Autowired
    private QuantumCommunicationPerformanceRepository performanceRepository;

    // ==================== 基础CRUD接口 ====================

    /**
     * 创建性能监控记录
     */
    @PostMapping("/records")
    public ResponseEntity<QuantumCommunicationPerformanceEntity> createPerformanceRecord(
            @RequestBody QuantumCommunicationPerformanceEntity record) {
        QuantumCommunicationPerformanceEntity created = performanceService.createPerformanceRecord(record);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 根据ID获取性能记录
     */
    @GetMapping("/records/{id}")
    public ResponseEntity<QuantumCommunicationPerformanceEntity> getPerformanceRecordById(@PathVariable Long id) {
        Optional<QuantumCommunicationPerformanceEntity> record = performanceRepository.findById(id);
        return record.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 获取所有性能记录
     */
    @GetMapping("/records")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getAllPerformanceRecords() {
        List<QuantumCommunicationPerformanceEntity> records = performanceRepository.findAll();
        return ResponseEntity.ok(records);
    }

    /**
     * 更新性能记录
     */
    @PutMapping("/records/{id}")
    public ResponseEntity<QuantumCommunicationPerformanceEntity> updatePerformanceRecord(
            @PathVariable Long id,
            @RequestBody QuantumCommunicationPerformanceEntity record) {
        record.setId(id);
        QuantumCommunicationPerformanceEntity updated = performanceService.updatePerformanceRecord(record);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除性能记录
     */
    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deletePerformanceRecord(@PathVariable Long id) {
        performanceService.deletePerformanceRecord(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== 查询接口 ====================

    /**
     * 根据链路ID获取性能记录
     */
    @GetMapping("/records/link/{linkId}")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsByLinkId(
            @PathVariable String linkId) {
        List<QuantumCommunicationPerformanceEntity> records = performanceRepository.findByLinkId(linkId);
        return ResponseEntity.ok(records);
    }

    /**
     * 根据协议类型获取性能记录
     */
    @GetMapping("/records/protocol/{protocolType}")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsByProtocolType(
            @PathVariable String protocolType) {
        List<QuantumCommunicationPerformanceEntity> records = 
                performanceRepository.findByProtocolType(protocolType);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取活跃的性能记录
     */
    @GetMapping("/records/active")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getActiveRecords() {
        List<QuantumCommunicationPerformanceEntity> records = performanceRepository.findByIsActiveTrue();
        return ResponseEntity.ok(records);
    }

    /**
     * 根据时间范围查询性能记录
     */
    @GetMapping("/records/time-range")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsByTimeRange(
            @RequestParam("start") String startTime,
            @RequestParam("end") String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        List<QuantumCommunicationPerformanceEntity> records = 
                performanceRepository.findByTimestampBetween(start, end);
        return ResponseEntity.ok(records);
    }

    /**
     * 根据QoS级别查询
     */
    @GetMapping("/records/qos/{qosLevel}")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsByQosLevel(
            @PathVariable String qosLevel) {
        List<QuantumCommunicationPerformanceEntity> records = 
                performanceRepository.findByQosLevel(qosLevel);
        return ResponseEntity.ok(records);
    }

    /**
     * 根据安全级别查询
     */
    @GetMapping("/records/security/{securityLevel}")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsBySecurityLevel(
            @PathVariable String securityLevel) {
        List<QuantumCommunicationPerformanceEntity> records = 
                performanceRepository.findBySecurityLevel(securityLevel);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取高质量的链路记录
     */
    @GetMapping("/records/high-quality")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getHighQualityRecords() {
        List<QuantumCommunicationPerformanceEntity> records = performanceRepository.findHighQualityLinks();
        return ResponseEntity.ok(records);
    }

    /**
     * 获取需要优化的链路记录
     */
    @GetMapping("/records/needs-optimization")
    public ResponseEntity<List<QuantumCommunicationPerformanceEntity>> getRecordsNeedingOptimization() {
        List<QuantumCommunicationPerformanceEntity> records = performanceRepository.findLinksNeedingOptimization();
        return ResponseEntity.ok(records);
    }

    // ==================== 性能监控接口 ====================

    /**
     * 记录性能指标
     */
    @PostMapping("/metrics/record")
    public ResponseEntity<QuantumCommunicationPerformanceEntity> recordMetrics(
            @RequestBody Map<String, Object> metrics) {
        String linkId = (String) metrics.get("linkId");
        Double latency = ((Number) metrics.get("latency")).doubleValue();
        Double qber = ((Number) metrics.get("qber")).doubleValue();
        Double keyRate = ((Number) metrics.get("keyRate")).doubleValue();
        Double successRate = ((Number) metrics.get("successRate")).doubleValue();

        QuantumCommunicationPerformanceEntity record = performanceService.recordPerformanceMetrics(
                linkId, latency, qber, keyRate, successRate);
        return ResponseEntity.ok(record);
    }

    /**
     * 获取当前性能状态
     */
    @GetMapping("/status/{linkId}")
    public ResponseEntity<Map<String, Object>> getCurrentPerformanceStatus(@PathVariable String linkId) {
        Map<String, Object> status = performanceService.getCurrentPerformanceStatus(linkId);
        return ResponseEntity.ok(status);
    }

    /**
     * 计算链路质量评分
     */
    @GetMapping("/quality-score/{linkId}")
    public ResponseEntity<Map<String, Object>> calculateLinkQualityScore(@PathVariable String linkId) {
        Double score = performanceService.calculateLinkQualityScore(linkId);
        String rating = performanceService.getLinkQualityRating(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("qualityScore", score);
        response.put("qualityRating", rating);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 批量计算链路质量评分
     */
    @PostMapping("/quality-score/batch")
    public ResponseEntity<Map<String, Object>> batchCalculateQualityScores(
            @RequestBody List<String> linkIds) {
        Map<String, Double> scores = performanceService.batchCalculateQualityScores(linkIds);

        Map<String, Object> response = new HashMap<>();
        response.put("scores", scores);
        response.put("count", scores.size());
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    // ==================== 协议切换接口 ====================

    /**
     * 获取协议切换建议
     */
    @GetMapping("/protocol/recommendation/{linkId}")
    public ResponseEntity<Map<String, Object>> getProtocolRecommendation(@PathVariable String linkId) {
        String recommendation = performanceService.getProtocolRecommendation(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("recommendedProtocol", recommendation);
        response.put("reason", "Based on current link quality and performance metrics");
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 检查是否需要协议切换
     */
    @GetMapping("/protocol/should-switch/{linkId}")
    public ResponseEntity<Map<String, Object>> shouldSwitchProtocol(@PathVariable String linkId) {
        boolean shouldSwitch = performanceService.shouldSwitchProtocol(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("shouldSwitch", shouldSwitch);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 执行协议切换
     */
    @PostMapping("/protocol/switch")
    public ResponseEntity<Map<String, Object>> switchProtocol(
            @RequestBody Map<String, String> request) {
        String linkId = request.get("linkId");
        String newProtocol = request.get("newProtocol");

        boolean success = performanceService.switchProtocol(linkId, newProtocol);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("newProtocol", newProtocol);
        response.put("success", success);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取可用协议列表
     */
    @GetMapping("/protocol/available")
    public ResponseEntity<List<String>> getAvailableProtocols() {
        List<String> protocols = performanceService.getAvailableProtocols();
        return ResponseEntity.ok(protocols);
    }

    // ==================== 加密策略接口 ====================

    /**
     * 获取加密策略推荐
     */
    @GetMapping("/encryption/recommendation/{linkId}")
    public ResponseEntity<Map<String, Object>> getEncryptionRecommendation(@PathVariable String linkId) {
        String recommendation = performanceService.getEncryptionRecommendation(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("recommendedStrategy", recommendation);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 评估链路安全性
     */
    @GetMapping("/security/assessment/{linkId}")
    public ResponseEntity<Map<String, Object>> assessLinkSecurity(@PathVariable String linkId) {
        Map<String, Object> assessment = performanceService.assessLinkSecurity(linkId);
        return ResponseEntity.ok(assessment);
    }

    // ==================== 优化接口 ====================

    /**
     * 获取优化建议
     */
    @GetMapping("/optimization/suggestions/{linkId}")
    public ResponseEntity<List<String>> getOptimizationSuggestions(@PathVariable String linkId) {
        List<String> suggestions = performanceService.getOptimizationSuggestions(linkId);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * 执行自动优化
     */
    @PostMapping("/optimization/auto/{linkId}")
    public ResponseEntity<Map<String, Object>> performAutoOptimization(@PathVariable String linkId) {
        boolean optimized = performanceService.performAutoOptimization(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("optimized", optimized);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 应用优化参数
     */
    @PostMapping("/optimization/apply")
    public ResponseEntity<Map<String, Object>> applyOptimizationParameters(
            @RequestBody Map<String, Object> parameters) {
        String linkId = (String) parameters.get("linkId");
        @SuppressWarnings("unchecked")
        Map<String, Double> params = (Map<String, Double>) parameters.get("parameters");

        boolean applied = performanceService.applyOptimizationParameters(linkId, params);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("applied", applied);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    // ==================== 统计分析接口 ====================

    /**
     * 计算平均密钥率
     */
    @GetMapping("/statistics/avg-key-rate/{linkId}")
    public ResponseEntity<Map<String, Object>> calculateAverageKeyRate(@PathVariable String linkId) {
        Double avgKeyRate = performanceRepository.calculateAverageKeyRateByLinkId(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("averageKeyRate", avgKeyRate);
        response.put("unit", "bits/second");

        return ResponseEntity.ok(response);
    }

    /**
     * 计算平均QBER
     */
    @GetMapping("/statistics/avg-qber/{linkId}")
    public ResponseEntity<Map<String, Object>> calculateAverageQber(@PathVariable String linkId) {
        Double avgQber = performanceRepository.calculateAverageQberByLinkId(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("averageQber", avgQber);
        response.put("unit", "percentage");

        return ResponseEntity.ok(response);
    }

    /**
     * 计算平均成功率
     */
    @GetMapping("/statistics/avg-success-rate/{linkId}")
    public ResponseEntity<Map<String, Object>> calculateAverageSuccessRate(@PathVariable String linkId) {
        Double avgSuccessRate = performanceRepository.calculateAverageSuccessRateByLinkId(linkId);

        Map<String, Object> response = new HashMap<>();
        response.put("linkId", linkId);
        response.put("averageSuccessRate", avgSuccessRate);
        response.put("unit", "percentage");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取协议类型分布统计
     */
    @GetMapping("/statistics/protocol-distribution")
    public ResponseEntity<List<Map<String, Object>>> getProtocolDistribution() {
        List<Map<String, Object>> distribution = performanceRepository.countByProtocolType();
        return ResponseEntity.ok(distribution);
    }

    /**
     * 获取QoS级别分布统计
     */
    @GetMapping("/statistics/qos-distribution")
    public ResponseEntity<List<Map<String, Object>>> getQosDistribution() {
        List<Map<String, Object>> distribution = performanceRepository.countByQosLevel();
        return ResponseEntity.ok(distribution);
    }

    // ==================== 趋势分析接口 ====================

    /**
     * 分析性能趋势
     */
    @GetMapping("/trends/analysis/{linkId}")
    public ResponseEntity<Map<String, Object>> analyzePerformanceTrends(@PathVariable String linkId) {
        Map<String, Object> trends = performanceService.analyzePerformanceTrends(linkId);
        return ResponseEntity.ok(trends);
    }

    /**
     * 预测未来性能
     */
    @GetMapping("/trends/predict/{linkId}")
    public ResponseEntity<Map<String, Object>> predictFuturePerformance(
            @PathVariable String linkId,
            @RequestParam(defaultValue = "24") int hoursAhead) {
        Map<String, Object> prediction = performanceService.predictFuturePerformance(linkId, hoursAhead);
        return ResponseEntity.ok(prediction);
    }

    // ==================== 告警接口 ====================

    /**
     * 检查异常并告警
     */
    @PostMapping("/alerts/check")
    public ResponseEntity<List<Map<String, Object>>> checkAnomaliesAndAlert() {
        List<Map<String, Object>> alerts = performanceService.checkAnomaliesAndAlert();
        return ResponseEntity.ok(alerts);
    }

    /**
     * 获取活跃告警
     */
    @GetMapping("/alerts/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveAlerts() {
        List<Map<String, Object>> alerts = performanceService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * 清除告警
     */
    @PostMapping("/alerts/clear/{alertId}")
    public ResponseEntity<Map<String, Object>> clearAlert(@PathVariable String alertId) {
        boolean cleared = performanceService.clearAlert(alertId);

        Map<String, Object> response = new HashMap<>();
        response.put("alertId", alertId);
        response.put("cleared", cleared);

        return ResponseEntity.ok(response);
    }

    // ==================== 系统管理接口 ====================

    /**
     * 获取全局性能统计
     */
    @GetMapping("/system/statistics")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        Map<String, Object> stats = performanceService.getGlobalPerformanceStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = performanceService.getSystemHealthStatus();
        return ResponseEntity.ok(health);
    }

    /**
     * 清理过期记录
     */
    @PostMapping("/system/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpiredRecords(
            @RequestParam(defaultValue = "30") int daysOld) {
        int deleted = performanceService.cleanupExpiredRecords(daysOld);

        Map<String, Object> response = new HashMap<>();
        response.put("deletedRecords", deleted);
        response.put("olderThanDays", daysOld);

        return ResponseEntity.ok(response);
    }

    /**
     * 导出性能报告
     */
    @GetMapping("/system/export-report")
    public ResponseEntity<Map<String, Object>> exportPerformanceReport(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        String report = performanceService.exportPerformanceReport(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("report", report);
        response.put("startTime", startTime);
        response.put("endTime", endTime);

        return ResponseEntity.ok(response);
    }
}
