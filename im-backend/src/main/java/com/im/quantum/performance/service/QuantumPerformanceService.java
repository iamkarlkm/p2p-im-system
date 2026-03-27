package com.im.quantum.performance.service;

import com.im.quantum.performance.entity.PerformanceMetricsEntity;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.AlertLevel;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.MetricType;
import com.im.quantum.performance.repository.PerformanceMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 量子通信性能监控服务
 * 提供实时性能监控、数据收集、分析和优化建议
 * 
 * @author Quantum Performance Team
 * @since 2026-03-26
 */
@Service
public class QuantumPerformanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuantumPerformanceService.class);
    
    @Autowired
    private PerformanceMetricsRepository metricsRepository;
    
    // 内存缓存，用于快速查询最新数据
    private final Map<String, PerformanceMetricsEntity> latestMetricsCache = new ConcurrentHashMap<>();
    private final Map<String, List<PerformanceMetricsEntity>> sessionMetricsHistory = new ConcurrentHashMap<>();
    
    // 性能阈值配置
    private final Map<String, Double> thresholds = new ConcurrentHashMap<>();
    
    // 告警计数器
    private final AtomicLong alertCounter = new AtomicLong(0);
    
    // 会话统计
    private final Map<String, SessionStats> sessionStatsMap = new ConcurrentHashMap<>();
    
    public QuantumPerformanceService() {
        initializeThresholds();
    }
    
    /**
     * 初始化性能阈值
     */
    private void initializeThresholds() {
        thresholds.put("qber.warning", 0.11);
        thresholds.put("qber.critical", 0.15);
        thresholds.put("latency.warning", 200.0);
        thresholds.put("latency.critical", 500.0);
        thresholds.put("packetLoss.warning", 0.03);
        thresholds.put("packetLoss.critical", 0.05);
        thresholds.put("stability.warning", 0.5);
        thresholds.put("stability.critical", 0.3);
        thresholds.put("cpu.warning", 80.0);
        thresholds.put("cpu.critical", 90.0);
        thresholds.put("memory.warning", 2048.0);
        thresholds.put("memory.critical", 3072.0);
    }
    
    /**
     * 记录性能指标
     */
    @Transactional
    public PerformanceMetricsEntity recordMetrics(PerformanceMetricsEntity metrics) {
        metrics.setTimestamp(Instant.now());
        
        // 自动检测告警级别
        AlertLevel detectedLevel = metrics.detectAlertLevel();
        if (detectedLevel != AlertLevel.NONE) {
            metrics.setAlertLevel(detectedLevel);
            metrics.setAlertMessage(generateAlertMessage(metrics, detectedLevel));
            alertCounter.incrementAndGet();
        }
        
        PerformanceMetricsEntity saved = metricsRepository.save(metrics);
        
        // 更新缓存
        String cacheKey = metrics.getSessionId() + ":" + metrics.getMetricType();
        latestMetricsCache.put(cacheKey, saved);
        
        // 更新会话历史
        sessionMetricsHistory
            .computeIfAbsent(metrics.getSessionId(), k -> new ArrayList<>())
            .add(saved);
        
        // 更新会话统计
        updateSessionStats(saved);
        
        logger.debug("Recorded metrics for session {}: {}", metrics.getSessionId(), metrics.getMetricType());
        return saved;
    }
    
    /**
     * 批量记录性能指标
     */
    @Transactional
    public List<PerformanceMetricsEntity> recordMetricsBatch(List<PerformanceMetricsEntity> metricsList) {
        Instant now = Instant.now();
        for (PerformanceMetricsEntity metrics : metricsList) {
            metrics.setTimestamp(now);
            AlertLevel level = metrics.detectAlertLevel();
            if (level != AlertLevel.NONE) {
                metrics.setAlertLevel(level);
                metrics.setAlertMessage(generateAlertMessage(metrics, level));
                alertCounter.incrementAndGet();
            }
        }
        
        List<PerformanceMetricsEntity> saved = metricsRepository.saveAll(metricsList);
        
        for (PerformanceMetricsEntity m : saved) {
            String cacheKey = m.getSessionId() + ":" + m.getMetricType();
            latestMetricsCache.put(cacheKey, m);
            sessionMetricsHistory.computeIfAbsent(m.getSessionId(), k -> new ArrayList<>()).add(m);
        }
        
        logger.info("Batch recorded {} metrics entries", saved.size());
        return saved;
    }
    
    /**
     * 获取最新性能指标
     */
    public PerformanceMetricsEntity getLatestMetrics(String sessionId, MetricType type) {
        String cacheKey = sessionId + ":" + type;
        PerformanceMetricsEntity cached = latestMetricsCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        return metricsRepository
            .findTopBySessionIdAndMetricTypeOrderByTimestampDesc(sessionId, type)
            .orElse(null);
    }
    
    /**
     * 获取会话的所有最新指标
     */
    public Map<MetricType, PerformanceMetricsEntity> getSessionLatestMetrics(String sessionId) {
        Map<MetricType, PerformanceMetricsEntity> result = new EnumMap<>(MetricType.class);
        
        for (MetricType type : MetricType.values()) {
            PerformanceMetricsEntity metrics = getLatestMetrics(sessionId, type);
            if (metrics != null) {
                result.put(type, metrics);
            }
        }
        
        return result;
    }
    
    /**
     * 获取性能历史数据
     */
    public List<PerformanceMetricsEntity> getMetricsHistory(
            String sessionId, 
            MetricType type, 
            Instant from, 
            Instant to,
            int limit) {
        
        if (from == null) from = Instant.now().minus(Duration.ofHours(24));
        if (to == null) to = Instant.now();
        
        return metricsRepository.findBySessionIdAndMetricTypeAndTimestampBetween(
            sessionId, type, from, to, 
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }
    
    /**
     * 获取所有活动的告警
     */
    public List<PerformanceMetricsEntity> getActiveAlerts(AlertLevel minLevel) {
        return metricsRepository.findActiveAlerts(minLevel);
    }
    
    /**
     * 获取告警统计
     */
    public AlertStatistics getAlertStatistics(Duration duration) {
        Instant since = Instant.now().minus(duration);
        
        List<PerformanceMetricsEntity> alerts = metricsRepository
            .findAlertsSince(since, AlertLevel.INFO);
        
        AlertStatistics stats = new AlertStatistics();
        stats.setTotalAlerts(alerts.size());
        stats.setPeriodStart(since);
        stats.setPeriodEnd(Instant.now());
        
        Map<AlertLevel, Long> byLevel = alerts.stream()
            .collect(Collectors.groupingBy(PerformanceMetricsEntity::getAlertLevel, Collectors.counting()));
        stats.setAlertsByLevel(byLevel);
        
        Map<String, Long> bySession = alerts.stream()
            .collect(Collectors.groupingBy(PerformanceMetricsEntity::getSessionId, Collectors.counting()));
        stats.setAlertsBySession(bySession);
        
        return stats;
    }
    
    /**
     * 执行性能分析
     */
    public PerformanceAnalysis analyzePerformance(String sessionId, Duration period) {
        Instant from = Instant.now().minus(period);
        Instant to = Instant.now();
        
        List<PerformanceMetricsEntity> metricsList = metricsRepository
            .findBySessionIdAndTimestampBetween(sessionId, from, to);
        
        if (metricsList.isEmpty()) {
            return new PerformanceAnalysis(sessionId, "No data available for analysis");
        }
        
        PerformanceAnalysis analysis = new PerformanceAnalysis(sessionId);
        analysis.setAnalysisPeriod(period);
        analysis.setDataPoints(metricsList.size());
        
        // 计算平均指标
        analysis.setAvgKeyGenerationRate(calculateAverage(metricsList, PerformanceMetricsEntity::getKeyGenerationRate));
        analysis.setAvgQber(calculateAverage(metricsList, PerformanceMetricsEntity::getQuantumBitErrorRate));
        analysis.setAvgLatency(calculateAverage(metricsList, PerformanceMetricsEntity::getLatencyMs));
        analysis.setAvgThroughput(calculateAverage(metricsList, PerformanceMetricsEntity::getThroughputMbps));
        analysis.setAvgStability(calculateAverage(metricsList, PerformanceMetricsEntity::getConnectionStability));
        
        // 检测瓶颈
        List<Bottleneck> bottlenecks = detectBottlenecks(metricsList);
        analysis.setBottlenecks(bottlenecks);
        
        // 生成优化建议
        List<OptimizationSuggestion> suggestions = generateOptimizationSuggestions(bottlenecks, analysis);
        analysis.setSuggestions(suggestions);
        
        // 计算总体健康分数
        double healthScore = calculateHealthScore(analysis);
        analysis.setHealthScore(healthScore);
        analysis.setHealthStatus(determineHealthStatus(healthScore));
        
        return analysis;
    }
    
    /**
     * 检测性能瓶颈
     */
    private List<Bottleneck> detectBottlenecks(List<PerformanceMetricsEntity> metrics) {
        List<Bottleneck> bottlenecks = new ArrayList<>();
        
        // QBER分析
        double avgQber = calculateAverage(metrics, PerformanceMetricsEntity::getQuantumBitErrorRate);
        if (avgQber > thresholds.get("qber.warning")) {
            Bottleneck bn = new Bottleneck();
            bn.setType("HIGH_QBER");
            bn.setSeverity(avgQber > thresholds.get("qber.critical") ? "CRITICAL" : "WARNING");
            bn.setDescription(String.format("High quantum bit error rate: %.4f", avgQber));
            bn.setImpact("Reduces key generation efficiency and security");
            bn.setRecommendation("Check quantum channel quality and detector alignment");
            bottlenecks.add(bn);
        }
        
        // 延迟分析
        double avgLatency = calculateAverage(metrics, PerformanceMetricsEntity::getLatencyMs);
        if (avgLatency > thresholds.get("latency.warning")) {
            Bottleneck bn = new Bottleneck();
            bn.setType("HIGH_LATENCY");
            bn.setSeverity(avgLatency > thresholds.get("latency.critical") ? "CRITICAL" : "WARNING");
            bn.setDescription(String.format("High network latency: %.2f ms", avgLatency));
            bn.setImpact("Degrades real-time communication experience");
            bn.setRecommendation("Optimize network routing or upgrade bandwidth");
            bottlenecks.add(bn);
        }
        
        // 稳定性分析
        double avgStability = calculateAverage(metrics, PerformanceMetricsEntity::getConnectionStability);
        if (avgStability < thresholds.get("stability.warning")) {
            Bottleneck bn = new Bottleneck();
            bn.setType("LOW_STABILITY");
            bn.setSeverity(avgStability < thresholds.get("stability.critical") ? "CRITICAL" : "WARNING");
            bn.setDescription(String.format("Low connection stability: %.2f", avgStability));
            bn.setImpact("Frequent disconnections and key loss");
            bn.setRecommendation("Stabilize quantum channel or implement error correction");
            bottlenecks.add(bn);
        }
        
        // 丢包分析
        double avgPacketLoss = calculateAverage(metrics, PerformanceMetricsEntity::getPacketLossRate);
        if (avgPacketLoss > thresholds.get("packetLoss.warning")) {
            Bottleneck bn = new Bottleneck();
            bn.setType("PACKET_LOSS");
            bn.setSeverity(avgPacketLoss > thresholds.get("packetLoss.critical") ? "CRITICAL" : "WARNING");
            bn.setDescription(String.format("High packet loss rate: %.4f", avgPacketLoss));
            bn.setImpact("Reduces effective throughput and increases retransmissions");
            bn.setRecommendation("Check network congestion and buffer sizes");
            bottlenecks.add(bn);
        }
        
        return bottlenecks;
    }
    
    /**
     * 生成优化建议
     */
    private List<OptimizationSuggestion> generateOptimizationSuggestions(
            List<Bottleneck> bottlenecks, 
            PerformanceAnalysis analysis) {
        
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        for (Bottleneck bn : bottlenecks) {
            OptimizationSuggestion sugg = new OptimizationSuggestion();
            sugg.setRelatedBottleneck(bn.getType());
            sugg.setPriority(bn.getSeverity());
            sugg.setDescription(bn.getRecommendation());
            sugg.setExpectedImpact("Improves " + bn.getType().toLowerCase().replace("_", " "));
            
            switch (bn.getType()) {
                case "HIGH_QBER":
                    sugg.setAction("CALIBRATE_DETECTORS");
                    sugg.setEstimatedImprovement("15-25% reduction in QBER");
                    break;
                case "HIGH_LATENCY":
                    sugg.setAction("OPTIMIZE_ROUTING");
                    sugg.setEstimatedImprovement("20-40% latency reduction");
                    break;
                case "LOW_STABILITY":
                    sugg.setAction("ENHANCE_ERROR_CORRECTION");
                    sugg.setEstimatedImprovement("30-50% stability improvement");
                    break;
                case "PACKET_LOSS":
                    sugg.setAction("INCREASE_BUFFER_SIZE");
                    sugg.setEstimatedImprovement("10-20% packet loss reduction");
                    break;
            }
            
            suggestions.add(sugg);
        }
        
        return suggestions;
    }
    
    /**
     * 计算健康分数
     */
    private double calculateHealthScore(PerformanceAnalysis analysis) {
        double score = 100.0;
        
        if (analysis.getAvgQber() != null) {
            score -= analysis.getAvgQber() * 200;
        }
        if (analysis.getAvgLatency() != null && analysis.getAvgLatency() > 100) {
            score -= (analysis.getAvgLatency() - 100) * 0.1;
        }
        if (analysis.getAvgStability() != null) {
            score -= (1 - analysis.getAvgStability()) * 30;
        }
        
        int bottleneckPenalty = analysis.getBottlenecks().size() * 10;
        score -= bottleneckPenalty;
        
        return Math.max(0, Math.min(100, score));
    }
    
    private String determineHealthStatus(double score) {
        if (score >= 90) return "EXCELLENT";
        if (score >= 75) return "GOOD";
        if (score >= 60) return "FAIR";
        if (score >= 40) return "POOR";
        return "CRITICAL";
    }
    
    /**
     * 获取系统概览
     */
    public SystemOverview getSystemOverview() {
        SystemOverview overview = new SystemOverview();
        
        overview.setTotalSessions(sessionStatsMap.size());
        overview.setActiveSessions((int) sessionStatsMap.values().stream()
            .filter(s -> s.isActive()).count());
        overview.setTotalAlerts(alertCounter.get());
        
        // 计算平均性能
        List<PerformanceMetricsEntity> allLatest = new ArrayList<>(latestMetricsCache.values());
        overview.setAvgSystemScore(calculateAverage(allLatest, PerformanceMetricsEntity::calculatePerformanceScore));
        
        // 获取最新的告警
        overview.setRecentAlerts(getActiveAlerts(AlertLevel.WARNING));
        
        return overview;
    }
    
    /**
     * 清理过期数据
     */
    @Scheduled(fixedRate = 3600000) // 每小时执行
    @Transactional
    public void cleanupOldData() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(30));
        int deleted = metricsRepository.deleteByTimestampBefore(cutoff);
        logger.info("Cleaned up {} old performance metrics records", deleted);
    }
    
    // 辅助方法
    private Double calculateAverage(List<PerformanceMetricsEntity> list, java.util.function.Function<PerformanceMetricsEntity, Double> extractor) {
        return list.stream()
            .map(extractor)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }
    
    private String generateAlertMessage(PerformanceMetricsEntity metrics, AlertLevel level) {
        StringBuilder msg = new StringBuilder();
        msg.append(level.name()).append(" alert: ");
        
        if (metrics.getQuantumBitErrorRate() != null && metrics.getQuantumBitErrorRate() > thresholds.get("qber.warning")) {
            msg.append(String.format("QBER is %.4f (threshold: %.4f). ", 
                metrics.getQuantumBitErrorRate(), thresholds.get("qber.warning")));
        }
        if (metrics.getLatencyMs() != null && metrics.getLatencyMs() > thresholds.get("latency.warning")) {
            msg.append(String.format("Latency is %.2f ms. ", metrics.getLatencyMs()));
        }
        if (metrics.getConnectionStability() != null && metrics.getConnectionStability() < thresholds.get("stability.warning")) {
            msg.append(String.format("Stability is %.2f. ", metrics.getConnectionStability()));
        }
        
        return msg.toString();
    }
    
    private void updateSessionStats(PerformanceMetricsEntity metrics) {
        SessionStats stats = sessionStatsMap.computeIfAbsent(
            metrics.getSessionId(), 
            k -> new SessionStats(k)
        );
        stats.updateLastActivity();
    }
    
    // 内部类定义
    
    public static class SessionStats {
        private final String sessionId;
        private Instant lastActivity;
        private int totalMetrics;
        private int alertCount;
        
        public SessionStats(String sessionId) {
            this.sessionId = sessionId;
            this.lastActivity = Instant.now();
        }
        
        public void updateLastActivity() {
            this.lastActivity = Instant.now();
            this.totalMetrics++;
        }
        
        public boolean isActive() {
            return Duration.between(lastActivity, Instant.now()).toMinutes() < 5;
        }
        
        // Getters
        public String getSessionId() { return sessionId; }
        public Instant getLastActivity() { return lastActivity; }
        public int getTotalMetrics() { return totalMetrics; }
        public int getAlertCount() { return alertCount; }
    }
    
    public static class PerformanceAnalysis {
        private final String sessionId;
        private String errorMessage;
        private Duration analysisPeriod;
        private int dataPoints;
        private Double avgKeyGenerationRate;
        private Double avgQber;
        private Double avgLatency;
        private Double avgThroughput;
        private Double avgStability;
        private List<Bottleneck> bottlenecks;
        private List<OptimizationSuggestion> suggestions;
        private double healthScore;
        private String healthStatus;
        
        public PerformanceAnalysis(String sessionId) {
            this.sessionId = sessionId;
            this.bottlenecks = new ArrayList<>();
            this.suggestions = new ArrayList<>();
        }
        
        public PerformanceAnalysis(String sessionId, String errorMessage) {
            this(sessionId);
            this.errorMessage = errorMessage;
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public String getErrorMessage() { return errorMessage; }
        public Duration getAnalysisPeriod() { return analysisPeriod; }
        public void setAnalysisPeriod(Duration analysisPeriod) { this.analysisPeriod = analysisPeriod; }
        public int getDataPoints() { return dataPoints; }
        public void setDataPoints(int dataPoints) { this.dataPoints = dataPoints; }
        public Double getAvgKeyGenerationRate() { return avgKeyGenerationRate; }
        public void setAvgKeyGenerationRate(Double v) { this.avgKeyGenerationRate = v; }
        public Double getAvgQber() { return avgQber; }
        public void setAvgQber(Double v) { this.avgQber = v; }
        public Double getAvgLatency() { return avgLatency; }
        public void setAvgLatency(Double v) { this.avgLatency = v; }
        public Double getAvgThroughput() { return avgThroughput; }
        public void setAvgThroughput(Double v) { this.avgThroughput = v; }
        public Double getAvgStability() { return avgStability; }
        public void setAvgStability(Double v) { this.avgStability = v; }
        public List<Bottleneck> getBottlenecks() { return bottlenecks; }
        public void setBottlenecks(List<Bottleneck> v) { this.bottlenecks = v; }
        public List<OptimizationSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<OptimizationSuggestion> v) { this.suggestions = v; }
        public double getHealthScore() { return healthScore; }
        public void setHealthScore(double v) { this.healthScore = v; }
        public String getHealthStatus() { return healthStatus; }
        public void setHealthStatus(String v) { this.healthStatus = v; }
    }
    
    public static class Bottleneck {
        private String type;
        private String severity;
        private String description;
        private String impact;
        private String recommendation;
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String v) { this.type = v; }
        public String getSeverity() { return severity; }
        public void setSeverity(String v) { this.severity = v; }
        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
        public String getImpact() { return impact; }
        public void setImpact(String v) { this.impact = v; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String v) { this.recommendation = v; }
    }
    
    public static class OptimizationSuggestion {
        private String action;
        private String priority;
        private String description;
        private String relatedBottleneck;
        private String expectedImpact;
        private String estimatedImprovement;
        
        // Getters and Setters
        public String getAction() { return action; }
        public void setAction(String v) { this.action = v; }
        public String getPriority() { return priority; }
        public void setPriority(String v) { this.priority = v; }
        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
        public String getRelatedBottleneck() { return relatedBottleneck; }
        public void setRelatedBottleneck(String v) { this.relatedBottleneck = v; }
        public String getExpectedImpact() { return expectedImpact; }
        public void setExpectedImpact(String v) { this.expectedImpact = v; }
        public String getEstimatedImprovement() { return estimatedImprovement; }
        public void setEstimatedImprovement(String v) { this.estimatedImprovement = v; }
    }
    
    public static class AlertStatistics {
        private int totalAlerts;
        private Instant periodStart;
        private Instant periodEnd;
        private Map<AlertLevel, Long> alertsByLevel;
        private Map<String, Long> alertsBySession;
        
        // Getters and Setters
        public int getTotalAlerts() { return totalAlerts; }
        public void setTotalAlerts(int v) { this.totalAlerts = v; }
        public Instant getPeriodStart() { return periodStart; }
        public void setPeriodStart(Instant v) { this.periodStart = v; }
        public Instant getPeriodEnd() { return periodEnd; }
        public void setPeriodEnd(Instant v) { this.periodEnd = v; }
        public Map<AlertLevel, Long> getAlertsByLevel() { return alertsByLevel; }
        public void setAlertsByLevel(Map<AlertLevel, Long> v) { this.alertsByLevel = v; }
        public Map<String, Long> getAlertsBySession() { return alertsBySession; }
        public void setAlertsBySession(Map<String, Long> v) { this.alertsBySession = v; }
    }
    
    public static class SystemOverview {
        private int totalSessions;
        private int activeSessions;
        private long totalAlerts;
        private Double avgSystemScore;
        private List<PerformanceMetricsEntity> recentAlerts;
        
        // Getters and Setters
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int v) { this.totalSessions = v; }
        public int getActiveSessions() { return activeSessions; }
        public void setActiveSessions(int v) { this.activeSessions = v; }
        public long getTotalAlerts() { return totalAlerts; }
        public void setTotalAlerts(long v) { this.totalAlerts = v; }
        public Double getAvgSystemScore() { return avgSystemScore; }
        public void setAvgSystemScore(Double v) { this.avgSystemScore = v; }
        public List<PerformanceMetricsEntity> getRecentAlerts() { return recentAlerts; }
        public void setRecentAlerts(List<PerformanceMetricsEntity> v) { this.recentAlerts = v; }
    }
}
