package com.im.backend.service;

import com.im.backend.entity.QuantumCommunicationPerformanceEntity;
import com.im.backend.entity.QuantumCommunicationPerformanceEntity.*;
import com.im.backend.repository.QuantumCommunicationPerformanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 量子通信性能优化服务
 * 提供量子密钥分发性能监控、优化策略推荐、自适应切换等功能
 * 
 * @author IM System
 * @version 1.0.0
 * @since 2026-03-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuantumCommunicationPerformanceService {

    private final QuantumCommunicationPerformanceRepository performanceRepository;
    
    // 实时性能监控缓存
    private final Map<String, QuantumCommunicationPerformanceEntity> realtimeMonitorCache = new ConcurrentHashMap<>();
    
    // 会话优化状态跟踪
    private final Map<String, OptimizationState> sessionOptimizationStates = new ConcurrentHashMap<>();
    
    // AI优化模型参数（简化的规则引擎）
    private final Map<String, OptimizationRule> optimizationRules = new ConcurrentHashMap<>();

    /**
     * 记录量子通信性能数据
     */
    @Transactional
    public QuantumCommunicationPerformanceEntity recordPerformance(QuantumCommunicationPerformanceEntity performance) {
        // 验证数据有效性
        if (!performance.validateData()) {
            log.error("量子通信性能数据无效: {}", performance);
            throw new IllegalArgumentException("量子通信性能数据无效");
        }
        
        // 计算衍生指标
        performance.calculateKeyNegotiationLatency();
        performance.calculateLinkQualityScore();
        
        // 生成优化建议
        String recommendation = performance.generatePerformanceRecommendation();
        performance.setPerformanceRecommendation(recommendation);
        
        // 实时监控数据缓存
        if (performance.getIsRealtimeMonitoring() != null && performance.getIsRealtimeMonitoring()) {
            String cacheKey = performance.getSessionId() + "_" + performance.getUserId();
            realtimeMonitorCache.put(cacheKey, performance);
            
            // 触发实时优化决策
            triggerRealTimeOptimization(performance);
        }
        
        // 保存到数据库
        QuantumCommunicationPerformanceEntity savedEntity = performanceRepository.save(performance);
        log.info("量子通信性能数据已记录: {}", savedEntity.toPerformanceSummary());
        
        return savedEntity;
    }

    /**
     * 批量记录性能数据
     */
    @Transactional
    public List<QuantumCommunicationPerformanceEntity> batchRecordPerformance(
            List<QuantumCommunicationPerformanceEntity> performances) {
        List<QuantumCommunicationPerformanceEntity> savedEntities = new ArrayList<>();
        
        for (QuantumCommunicationPerformanceEntity performance : performances) {
            try {
                if (performance.validateData()) {
                    performance.calculateKeyNegotiationLatency();
                    performance.calculateLinkQualityScore();
                    performance.setPerformanceRecommendation(performance.generatePerformanceRecommendation());
                    
                    savedEntities.add(performance);
                } else {
                    log.warn("跳过无效的性能数据: {}", performance);
                }
            } catch (Exception e) {
                log.error("处理性能数据失败: {}", performance, e);
            }
        }
        
        if (!savedEntities.isEmpty()) {
            savedEntities = performanceRepository.saveAll(savedEntities);
            log.info("批量记录量子通信性能数据完成: {} 条记录", savedEntities.size());
        }
        
        return savedEntities;
    }

    /**
     * 获取会话性能历史
     */
    public List<QuantumCommunicationPerformanceEntity> getSessionPerformanceHistory(String sessionId, 
                                                                                   LocalDateTime startTime, 
                                                                                   LocalDateTime endTime) {
        List<QuantumCommunicationPerformanceEntity> history = 
            performanceRepository.findBySessionIdAndSampledAtBetween(sessionId, startTime, endTime);
        
        log.debug("获取会话 {} 性能历史: {} 条记录", sessionId, history.size());
        return history;
    }

    /**
     * 获取用户性能统计
     */
    public UserPerformanceStatistics getUserPerformanceStatistics(String userId, LocalDateTime startTime, 
                                                                 LocalDateTime endTime) {
        List<QuantumCommunicationPerformanceEntity> userData = 
            performanceRepository.findByUserIdAndSampledAtBetween(userId, startTime, endTime);
        
        if (userData.isEmpty()) {
            return new UserPerformanceStatistics(userId, 0, 0.0, 0.0, 0.0, 0.0, 0L, 0L);
        }
        
        double avgQBER = userData.stream()
            .filter(p -> p.getQuantumBitErrorRate() != null)
            .mapToDouble(p -> p.getQuantumBitErrorRate().doubleValue())
            .average()
            .orElse(0.0);
        
        double avgSuccessRate = userData.stream()
            .filter(p -> p.getKeyDistributionSuccessRate() != null)
            .mapToDouble(p -> p.getKeyDistributionSuccessRate().doubleValue())
            .average()
            .orElse(0.0);
        
        double avgLatency = userData.stream()
            .filter(p -> p.getKeyNegotiationLatencyMs() != null)
            .mapToDouble(p -> p.getKeyNegotiationLatencyMs().doubleValue())
            .average()
            .orElse(0.0);
        
        double avgLinkQuality = userData.stream()
            .filter(p -> p.getLinkQualityScore() != null)
            .mapToDouble(p -> p.getLinkQualityScore().doubleValue())
            .average()
            .orElse(0.0);
        
        long totalSessions = userData.stream()
            .map(QuantumCommunicationPerformanceEntity::getSessionId)
            .distinct()
            .count();
        
        long totalOptimizationEvents = userData.stream()
            .filter(p -> p.getAppliedOptimization() != null && !p.getAppliedOptimization().isEmpty())
            .count();
        
        return new UserPerformanceStatistics(userId, totalSessions, avgQBER, avgSuccessRate, 
                                            avgLatency, avgLinkQuality, totalOptimizationEvents, userData.size());
    }

    /**
     * 分析性能趋势
     */
    public PerformanceTrendAnalysis analyzePerformanceTrend(String sessionId, int hours) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        
        List<QuantumCommunicationPerformanceEntity> trendData = 
            performanceRepository.findBySessionIdAndSampledAtBetween(sessionId, startTime, endTime);
        
        if (trendData.isEmpty()) {
            return new PerformanceTrendAnalysis(sessionId, hours, 0, 
                PerformanceTrend.STABLE, "无足够数据", Collections.emptyList());
        }
        
        // 按时间窗口分组（每15分钟）
        Map<String, List<QuantumCommunicationPerformanceEntity>> windowedData = trendData.stream()
            .collect(Collectors.groupingBy(entity -> {
                LocalDateTime sampled = entity.getSampledAt();
                return String.format("%02d:%02d", sampled.getHour(), sampled.getMinute() / 15 * 15);
            }));
        
        // 分析趋势
        List<Double> qberTrend = windowedData.values().stream()
            .map(window -> window.stream()
                .filter(p -> p.getQuantumBitErrorRate() != null)
                .mapToDouble(p -> p.getQuantumBitErrorRate().doubleValue())
                .average()
                .orElse(0.0))
            .collect(Collectors.toList());
        
        List<Double> latencyTrend = windowedData.values().stream()
            .map(window -> window.stream()
                .filter(p -> p.getKeyNegotiationLatencyMs() != null)
                .mapToDouble(p -> p.getKeyNegotiationLatencyMs().doubleValue())
                .average()
                .orElse(0.0))
            .collect(Collectors.toList());
        
        List<Integer> qualityTrend = windowedData.values().stream()
            .map(window -> window.stream()
                .filter(p -> p.getLinkQualityScore() != null)
                .mapToDouble(p -> p.getLinkQualityScore().doubleValue())
                .average()
                .orElse(0.0))
            .map(avg -> (int) Math.round(avg))
            .collect(Collectors.toList());
        
        // 判断整体趋势
        PerformanceTrend overallTrend = determineOverallTrend(qberTrend, latencyTrend, qualityTrend);
        String trendDescription = generateTrendDescription(overallTrend, qberTrend, latencyTrend, qualityTrend);
        
        return new PerformanceTrendAnalysis(sessionId, hours, trendData.size(), 
            overallTrend, trendDescription, windowedData.keySet().stream().sorted().collect(Collectors.toList()));
    }

    /**
     * 推荐优化策略
     */
    public OptimizationRecommendation recommendOptimization(String sessionId, String userId) {
        // 获取最新性能数据
        QuantumCommunicationPerformanceEntity latestPerformance = 
            performanceRepository.findTopBySessionIdAndUserIdOrderBySampledAtDesc(sessionId, userId);
        
        if (latestPerformance == null) {
            return new OptimizationRecommendation(sessionId, userId, 
                OptimizationStrategy.RELIABILITY_OPTIMIZATION,
                "无性能数据，使用默认可靠性优化",
                Collections.emptyList(),
                0.0);
        }
        
        // 评估当前状态
        boolean shouldSwitchToClassic = latestPerformance.shouldSwitchToClassicEncryption();
        boolean shouldEnableQuantum = latestPerformance.shouldEnableQuantumEncryption();
        OptimizationStrategy recommendedStrategy = latestPerformance.getRecommendedOptimizationStrategy();
        
        // 生成具体优化措施
        List<String> optimizationMeasures = generateOptimizationMeasures(latestPerformance, recommendedStrategy);
        
        // 预估优化效果
        double estimatedImprovement = estimateOptimizationImprovement(latestPerformance, recommendedStrategy);
        
        // 更新会话优化状态
        updateSessionOptimizationState(sessionId, userId, recommendedStrategy, optimizationMeasures);
        
        return new OptimizationRecommendation(sessionId, userId, recommendedStrategy,
            generateRecommendationRationale(latestPerformance), optimizationMeasures, estimatedImprovement);
    }

    /**
     * 执行自适应协议切换
     */
    @Transactional
    public ProtocolSwitchResult performAdaptiveProtocolSwitch(String sessionId, String userId, 
                                                             NetworkConditions conditions) {
        // 获取当前性能数据
        QuantumCommunicationPerformanceEntity currentPerformance = 
            performanceRepository.findTopBySessionIdAndUserIdOrderBySampledAtDesc(sessionId, userId);
        
        if (currentPerformance == null) {
            log.warn("无法执行协议切换，无性能数据: session={}, user={}", sessionId, userId);
            return new ProtocolSwitchResult(sessionId, userId, false, 
                "无性能数据，无法决策", null, null);
        }
        
        // 评估当前网络条件
        ProtocolSwitchDecision decision = evaluateProtocolSwitch(currentPerformance, conditions);
        
        if (!decision.shouldSwitch()) {
            return new ProtocolSwitchResult(sessionId, userId, false, 
                decision.getReason(), currentPerformance.getQkdProtocol(), 
                currentPerformance.getEncryptionStrategy());
        }
        
        // 执行协议切换
        QuantumCommunicationPerformanceEntity newPerformance = createSwitchPerformanceRecord(
            currentPerformance, decision.getTargetProtocol(), decision.getTargetStrategy());
        
        // 记录切换事件
        QuantumCommunicationPerformanceEntity savedRecord = recordPerformance(newPerformance);
        
        log.info("协议切换执行: session={}, user={}, 从 {} 切换到 {}, 策略: {}",
            sessionId, userId, currentPerformance.getQkdProtocol(), 
            decision.getTargetProtocol(), decision.getTargetStrategy());
        
        return new ProtocolSwitchResult(sessionId, userId, true, 
            decision.getReason(), decision.getTargetProtocol(), decision.getTargetStrategy());
    }

    /**
     * 触发现实时优化
     */
    private void triggerRealTimeOptimization(QuantumCommunicationPerformanceEntity performance) {
        String sessionId = performance.getSessionId();
        String userId = performance.getUserId();
        
        // 检查是否需要立即优化
        if (performance.getLinkQualityScore() != null && performance.getLinkQualityScore() < 40) {
            log.warn("检测到链路质量临界值: session={}, user={}, score={}", 
                sessionId, userId, performance.getLinkQualityScore());
            
            // 触发紧急优化
            OptimizationRecommendation recommendation = recommendOptimization(sessionId, userId);
            
            // 发送实时通知（简化实现）
            sendRealTimeOptimizationAlert(sessionId, userId, recommendation);
        }
        
        // 检查量子误码率异常
        if (performance.getQuantumBitErrorRate() != null && performance.getQuantumBitErrorRate().doubleValue() > 0.08) {
            log.error("量子误码率异常高: session={}, user={}, QBER={}", 
                sessionId, userId, performance.getQuantumBitErrorRate());
            
            // 自动切换到经典加密
            if (performance.shouldSwitchToClassicEncryption()) {
                ProtocolSwitchResult result = performAdaptiveProtocolSwitch(sessionId, userId, 
                    new NetworkConditions(performance.getSignalStrength(), 
                                         performance.getEnvironmentNoiseLevel(),
                                         performance.getTemperature(), performance.getHumidity()));
                log.info("自动切换到经典加密结果: {}", result);
            }
        }
    }

    /**
     * 评估协议切换决策
     */
    private ProtocolSwitchDecision evaluateProtocolSwitch(QuantumCommunicationPerformanceEntity performance, 
                                                         NetworkConditions conditions) {
        // 简单决策逻辑（实际应更复杂）
        if (performance.getLinkQualityScore() == null) {
            return new ProtocolSwitchDecision(false, "无链路质量数据", null, null);
        }
        
        if (performance.getLinkQualityScore() < 30) {
            // 链路质量极差，切换到经典协议
            return new ProtocolSwitchDecision(true, "链路质量极差", 
                QKDProtocol.MEMORY_QKD, // 使用存储器辅助的QKD作为降级方案
                EncryptionStrategy.CLASSIC_ONLY);
        }
        
        if (performance.getKeyDistributionSuccessRate() != null && 
            performance.getKeyDistributionSuccessRate().doubleValue() < 75) {
            // 密钥分发成功率低，尝试更可靠的协议
            return new ProtocolSwitchDecision(true, "密钥分发成功率低",
                QKDProtocol.TF_QKD, // 切换到双场QKD
                EncryptionStrategy.HYBRID_PARALLEL);
        }
        
        if (performance.getKeyNegotiationLatencyMs() != null && 
            performance.getKeyNegotiationLatencyMs() > 800) {
            // 延迟过高，切换到低延迟协议
            return new ProtocolSwitchDecision(true, "密钥协商延迟过高",
                QKDProtocol.BB84, // 经典BB84协议延迟较低
                EncryptionStrategy.PERFORMANCE_PRIORITY);
        }
        
        // 检查网络条件
        if (conditions != null && conditions.getSignalStrength() != null && 
            conditions.getSignalStrength().doubleValue() < -85) {
            return new ProtocolSwitchDecision(true, "信号强度弱",
                QKDProtocol.SATELLITE_QKD, // 卫星QKD对弱信号更鲁棒
                EncryptionStrategy.RELIABILITY_OPTIMIZATION);
        }
        
        return new ProtocolSwitchDecision(false, "当前协议性能良好", null, null);
    }

    /**
     * 生成优化措施
     */
    private List<String> generateOptimizationMeasures(QuantumCommunicationPerformanceEntity performance, 
                                                     OptimizationStrategy strategy) {
        List<String> measures = new ArrayList<>();
        
        switch (strategy) {
            case LATENCY_OPTIMIZATION:
                measures.add("优化密钥协商协议参数");
                measures.add("启用预计算密钥缓存");
                measures.add("减少协议交互轮次");
                measures.add("启用并行密钥生成");
                break;
                
            case THROUGHPUT_OPTIMIZATION:
                measures.add("增加量子信道数量");
                measures.add("优化信号调制方案");
                measures.add("启用批量密钥处理");
                measures.add("提高时钟频率");
                break;
                
            case BANDWIDTH_OPTIMIZATION:
                measures.add("启用数据压缩");
                measures.add("优化协议头开销");
                measures.add("启用差分编码");
                measures.add("减少冗余信息传输");
                break;
                
            case ENERGY_OPTIMIZATION:
                measures.add("启用动态功率控制");
                measures.add("优化激光器工作点");
                measures.add("启用睡眠模式");
                measures.add("减少不必要的数据处理");
                break;
                
            case RELIABILITY_OPTIMIZATION:
                measures.add("启用前向纠错");
                measures.add("增加冗余量子比特");
                measures.add("启用多路径传输");
                measures.add("实施链路质量监控");
                break;
                
            default:
                measures.add("启用自适应参数调整");
                measures.add("实施持续性能监控");
                measures.add("启用AI优化算法");
                measures.add("实施渐进式优化");
        }
        
        // 基于具体性能数据添加针对性措施
        if (performance.getQuantumBitErrorRate() != null && 
            performance.getQuantumBitErrorRate().doubleValue() > 0.05) {
            measures.add("降低量子误码率：优化光子源和检测器");
        }
        
        if (performance.getSignalStrength() != null && 
            performance.getSignalStrength().doubleValue() < -80) {
            measures.add("提升信号强度：调整发射功率和天线方向");
        }
        
        return measures;
    }

    /**
     * 预估优化效果
     */
    private double estimateOptimizationImprovement(QuantumCommunicationPerformanceEntity performance, 
                                                  OptimizationStrategy strategy) {
        double baseImprovement = 0.0;
        
        // 基于当前问题和策略预估改进
        if (performance.getLinkQualityScore() != null && performance.getLinkQualityScore() < 60) {
            baseImprovement += 15.0; // 链路质量优化预期提升
        }
        
        if (performance.getKeyNegotiationLatencyMs() != null && 
            performance.getKeyNegotiationLatencyMs() > 500) {
            baseImprovement += 20.0; // 延迟优化预期提升
        }
        
        if (performance.getKeyDistributionSuccessRate() != null && 
            performance.getKeyDistributionSuccessRate().doubleValue() < 85) {
            baseImprovement += 25.0; // 成功率优化预期提升
        }
        
        // 策略加成
        switch (strategy) {
            case AI_DRIVEN_OPTIMIZATION:
                baseImprovement *= 1.3;
                break;
            case ADAPTIVE_MULTI_OBJECTIVE:
                baseImprovement *= 1.2;
                break;
            case REAL_TIME_ADAPTATION:
                baseImprovement *= 1.15;
                break;
        }
        
        return Math.min(100.0, baseImprovement);
    }

    /**
     * 生成推荐理由
     */
    private String generateRecommendationRationale(QuantumCommunicationPerformanceEntity performance) {
        StringBuilder rationale = new StringBuilder();
        rationale.append("基于以下性能指标:");
        
        if (performance.getLinkQualityScore() != null) {
            rationale.append(String.format(" 链路质量=%d/100", performance.getLinkQualityScore()));
        }
        
        if (performance.getKeyNegotiationLatencyMs() != null) {
            rationale.append(String.format(" 延迟=%dms", performance.getKeyNegotiationLatencyMs()));
        }
        
        if (performance.getKeyDistributionSuccessRate() != null) {
            rationale.append(String.format(" 成功率=%.1f%%", performance.getKeyDistributionSuccessRate().doubleValue()));
        }
        
        if (performance.getQuantumBitErrorRate() != null) {
            rationale.append(String.format(" QBER=%.4f", performance.getQuantumBitErrorRate().doubleValue()));
        }
        
        return rationale.toString();
    }

    /**
     * 更新会话优化状态
     */
    private void updateSessionOptimizationState(String sessionId, String userId, 
                                               OptimizationStrategy strategy, 
                                               List<String> measures) {
        String stateKey = sessionId + "_" + userId;
        OptimizationState state = sessionOptimizationStates.getOrDefault(stateKey, 
            new OptimizationState(sessionId, userId));
        
        state.setCurrentStrategy(strategy);
        state.setAppliedMeasures(measures);
        state.setLastOptimizationTime(LocalDateTime.now());
        state.setOptimizationCount(state.getOptimizationCount() + 1);
        
        sessionOptimizationStates.put(stateKey, state);
    }

    /**
     * 发送实时优化警报（简化实现）
     */
    private void sendRealTimeOptimizationAlert(String sessionId, String userId, 
                                              OptimizationRecommendation recommendation) {
        // 在实际系统中，这里会调用消息推送服务
        log.info("发送实时优化警报: session={}, user={}, 策略={}, 预估提升={}%",
            sessionId, userId, recommendation.getStrategy(), recommendation.getEstimatedImprovement());
    }

    /**
     * 创建协议切换性能记录
     */
    private QuantumCommunicationPerformanceEntity createSwitchPerformanceRecord(
            QuantumCommunicationPerformanceEntity original, 
            QKDProtocol targetProtocol, 
            EncryptionStrategy targetStrategy) {
        QuantumCommunicationPerformanceEntity newRecord = new QuantumCommunicationPerformanceEntity();
        
        // 复制基本信息
        newRecord.setSessionId(original.getSessionId());
        newRecord.setUserId(original.getUserId());
        newRecord.setDeviceId(original.getDeviceId());
        newRecord.setCommunicationType(original.getCommunicationType());
        
        // 设置新协议和策略
        newRecord.setQkdProtocol(targetProtocol);
        newRecord.setEncryptionStrategy(targetStrategy);
        newRecord.setOptimizationStrategy(OptimizationStrategy.REAL_TIME_ADAPTATION);
        
        // 设置切换相关字段
        newRecord.setTraditionalAlgorithm(original.getTraditionalAlgorithm());
        newRecord.setQuantumAlgorithm(original.getQuantumAlgorithm());
        newRecord.setProtocolVersion(original.getProtocolVersion());
        newRecord.setLinkMonitoringStatus(LinkMonitoringStatus.AUTO_ADJUSTING);
        
        // 设置时间戳
        newRecord.setKeyNegotiationStartTime(LocalDateTime.now());
        newRecord.setSampledAt(LocalDateTime.now());
        newRecord.setIsRealtimeMonitoring(true);
        newRecord.setAppliedOptimization("协议切换: " + original.getQkdProtocol() + " -> " + targetProtocol);
        
        return newRecord;
    }

    /**
     * 判断整体趋势
     */
    private PerformanceTrend determineOverallTrend(List<Double> qberTrend, 
                                                  List<Double> latencyTrend, 
                                                  List<Integer> qualityTrend) {
        if (qberTrend.size() < 2) {
            return PerformanceTrend.STABLE;
        }
        
        // 计算各项指标的变化趋势
        double qberChange = calculateTrendChange(qberTrend);
        double latencyChange = calculateTrendChange(latencyTrend);
        double qualityChange = calculateTrendChange(qualityTrend.stream()
            .map(Integer::doubleValue).collect(Collectors.toList()));
        
        // 综合判断
        if (qberChange > 0.02 && latencyChange > 0.1 && qualityChange < -0.1) {
            return PerformanceTrend.DEGRADING_RAPIDLY;
        } else if (qberChange > 0.01 || latencyChange > 0.05 || qualityChange < -0.05) {
            return PerformanceTrend.DEGRADING;
        } else if (qberChange < -0.01 && latencyChange < -0.05 && qualityChange > 0.05) {
            return PerformanceTrend.IMPROVING_RAPIDLY;
        } else if (qberChange < -0.005 || latencyChange < -0.02 || qualityChange > 0.02) {
            return PerformanceTrend.IMPROVING;
        } else {
            return PerformanceTrend.STABLE;
        }
    }

    /**
     * 计算趋势变化率
     */
    private double calculateTrendChange(List<Double> values) {
        if (values.size() < 2) {
            return 0.0;
        }
        
        double first = values.get(0);
        double last = values.get(values.size() - 1);
        
        if (Math.abs(first) < 0.0001) {
            return 0.0;
        }
        
        return (last - first) / Math.abs(first);
    }

    /**
     * 生成趋势描述
     */
    private String generateTrendDescription(PerformanceTrend trend, List<Double> qberTrend,
                                           List<Double> latencyTrend, List<Integer> qualityTrend) {
        switch (trend) {
            case IMPROVING_RAPIDLY:
                return "性能快速提升，各项指标均有显著改善";
            case IMPROVING:
                return "性能稳步提升，优化措施效果良好";
            case STABLE:
                return "性能保持稳定，无明显波动";
            case DEGRADING:
                return "性能出现下降，建议检查链路质量";
            case DEGRADING_RAPIDLY:
                return "性能快速下降，需要立即干预";
            default:
                return "趋势分析完成";
        }
    }

    // ========== 内部类和数据结构 ==========
    
    /**
     * 用户性能统计
     */
    @Data
    public static class UserPerformanceStatistics {
        private final String userId;
        private final long totalSessions;
        private final double averageQBER;
        private final double averageSuccessRate;
        private final double averageLatency;
        private final double averageLinkQuality;
        private final long totalOptimizationEvents;
        private final long totalRecords;
        
        public UserPerformanceStatistics(String userId, long totalSessions, double averageQBER,
                                       double averageSuccessRate, double averageLatency, 
                                       double averageLinkQuality, long totalOptimizationEvents,
                                       long totalRecords) {
            this.userId = userId;
            this.totalSessions = totalSessions;
            this.averageQBER = averageQBER;
            this.averageSuccessRate = averageSuccessRate;
            this.averageLatency = averageLatency;
            this.averageLinkQuality = averageLinkQuality;
            this.totalOptimizationEvents = totalOptimizationEvents;
            this.totalRecords = totalRecords;
        }
    }
    
    /**
     * 性能趋势分析
     */
    @Data
    public static class PerformanceTrendAnalysis {
        private final String sessionId;
        private final int analysisHours;
        private final int dataPoints;
        private final PerformanceTrend overallTrend;
        private final String trendDescription;
        private final List<String> timeWindows;
    }
    
    /**
     * 优化推荐
     */
    @Data
    public static class OptimizationRecommendation {
        private final String sessionId;
        private final String userId;
        private final OptimizationStrategy strategy;
        private final String rationale;
        private final List<String> measures;
        private final double estimatedImprovement;
    }
    
    /**
     * 协议切换结果
     */
    @Data
    public static class ProtocolSwitchResult {
        private final String sessionId;
        private final String userId;
        private final boolean switched;
        private final String reason;
        private final QKDProtocol targetProtocol;
        private final EncryptionStrategy targetStrategy;
    }
    
    /**
     * 协议切换决策
     */
    @Data
    private static class ProtocolSwitchDecision {
        private final boolean shouldSwitch;
        private final String reason;
        private final QKDProtocol targetProtocol;
        private final EncryptionStrategy targetStrategy;
    }
    
    /**
     * 网络条件
     */
    @Data
    public static class NetworkConditions {
        private final BigDecimal signalStrength;
        private final BigDecimal environmentNoiseLevel;
        private final BigDecimal temperature;
        private final BigDecimal humidity;
        
        public NetworkConditions(BigDecimal signalStrength, BigDecimal environmentNoiseLevel,
                               BigDecimal temperature, BigDecimal humidity) {
            this.signalStrength = signalStrength;
            this.environmentNoiseLevel = environmentNoiseLevel;
            this.temperature = temperature;
            this.humidity = humidity;
        }
    }
    
    /**
     * 优化状态
     */
    @Data
    private static class OptimizationState {
        private final String sessionId;
        private final String userId;
        private OptimizationStrategy currentStrategy;
        private List<String> appliedMeasures;
        private LocalDateTime lastOptimizationTime;
        private int optimizationCount;
        
        public OptimizationState(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.appliedMeasures = new ArrayList<>();
            this.lastOptimizationTime = LocalDateTime.now();
            this.optimizationCount = 0;
        }
    }
    
    /**
     * 优化规则
     */
    @Data
    private static class OptimizationRule {
        private final String ruleId;
        private final String condition;
        private final OptimizationStrategy strategy;
        private final List<String> actions;
        private final double priority;
    }
    
    /**
     * 性能趋势枚举
     */
    public enum PerformanceTrend {
        IMPROVING_RAPIDLY,      // 快速提升
        IMPROVING,              // 稳步提升
        STABLE,                 // 保持稳定
        DEGRADING,              // 缓慢下降
        DEGRADING_RAPIDLY       // 快速下降
    }
}