package com.im.backend.repository;

import com.im.backend.entity.QuantumCommunicationPerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 量子通信性能优化数据仓库接口
 * 提供性能数据的CRUD操作和复杂查询
 * 
 * @author IM System
 * @version 1.0.0
 * @since 2026-03-25
 */
@Repository
public interface QuantumCommunicationPerformanceRepository 
    extends JpaRepository<QuantumCommunicationPerformanceEntity, Long> {

    /**
     * 根据会话ID查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findBySessionId(String sessionId);

    /**
     * 根据用户ID查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByUserId(String userId);

    /**
     * 根据会话ID和用户ID查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findBySessionIdAndUserId(String sessionId, String userId);

    /**
     * 根据会话ID和采样时间范围查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findBySessionIdAndSampledAtBetween(
        String sessionId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据用户ID和采样时间范围查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByUserIdAndSampledAtBetween(
        String userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找指定时间段内的所有性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findBySampledAtBetween(
        LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据会话ID和用户ID查找最新性能记录
     */
    Optional<QuantumCommunicationPerformanceEntity> findTopBySessionIdAndUserIdOrderBySampledAtDesc(
        String sessionId, String userId);

    /**
     * 根据用户ID查找最新性能记录
     */
    Optional<QuantumCommunicationPerformanceEntity> findTopByUserIdOrderBySampledAtDesc(String userId);

    /**
     * 根据优化策略查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByOptimizationStrategy(
        com.im.backend.entity.QuantumCommunicationPerformanceEntity.OptimizationStrategy strategy);

    /**
     * 根据通信类型查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByCommunicationType(
        com.im.backend.entity.QuantumCommunicationPerformanceEntity.CommunicationType type);

    /**
     * 根据QKD协议类型查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByQkdProtocol(
        com.im.backend.entity.QuantumCommunicationPerformanceEntity.QKDProtocol protocol);

    /**
     * 根据链路质量评分范围查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkQualityScoreBetween(Integer minScore, Integer maxScore);

    /**
     * 查找链路质量低于阈值的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkQualityScoreLessThan(Integer threshold);

    /**
     * 查找链路质量高于阈值的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkQualityScoreGreaterThan(Integer threshold);

    /**
     * 查找量子误码率高于阈值的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByQuantumBitErrorRateGreaterThan(Double threshold);

    /**
     * 查找密钥协商延迟高于阈值的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByKeyNegotiationLatencyMsGreaterThan(Long threshold);

    /**
     * 查找密钥分发成功率低于阈值的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByKeyDistributionSuccessRateLessThan(Double threshold);

    /**
     * 查找实时监控数据
     */
    List<QuantumCommunicationPerformanceEntity> findByIsRealtimeMonitoring(Boolean isRealtimeMonitoring);

    /**
     * 查找有效数据记录
     */
    List<QuantumCommunicationPerformanceEntity> findByIsValid(Boolean isValid);

    /**
     * 根据设备ID查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByDeviceId(String deviceId);

    /**
     * 根据协议版本查找性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByProtocolVersion(String protocolVersion);

    /**
     * 统计指定会话的性能记录数量
     */
    long countBySessionId(String sessionId);

    /**
     * 统计指定用户的性能记录数量
     */
    long countByUserId(String userId);

    /**
     * 统计指定时间段内的性能记录数量
     */
    long countBySampledAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定优化策略的记录数量
     */
    long countByOptimizationStrategy(
        com.im.backend.entity.QuantumCommunicationPerformanceEntity.OptimizationStrategy strategy);

    /**
     * 删除指定会话的性能记录
     */
    void deleteBySessionId(String sessionId);

    /**
     * 删除指定用户的性能记录
     */
    void deleteByUserId(String userId);

    /**
     * 删除指定时间段之前的性能记录（用于数据清理）
     */
    void deleteBySampledAtBefore(LocalDateTime cutoffTime);

    /**
     * 复杂查询：查找需要优化的会话
     * 条件：链路质量<60且最近1小时内有记录
     */
    @Query("SELECT DISTINCT p.sessionId FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.linkQualityScore < 60 " +
           "AND p.sampledAt >= :cutoffTime " +
           "GROUP BY p.sessionId " +
           "HAVING COUNT(p) >= 3")  // 至少3条记录才认为需要优化
    List<String> findSessionsNeedingOptimization(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 复杂查询：获取用户性能统计摘要
     */
    @Query("SELECT p.userId, " +
           "COUNT(p) as totalRecords, " +
           "AVG(p.linkQualityScore) as avgLinkQuality, " +
           "AVG(p.keyNegotiationLatencyMs) as avgLatency, " +
           "AVG(p.keyDistributionSuccessRate) as avgSuccessRate, " +
           "AVG(p.quantumBitErrorRate) as avgQBER " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.sampledAt >= :startTime AND p.sampledAt <= :endTime " +
           "AND p.isValid = true " +
           "GROUP BY p.userId")
    List<Object[]> getUserPerformanceSummary(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 复杂查询：获取会话性能趋势
     */
    @Query("SELECT DATE_TRUNC('hour', p.sampledAt) as hourWindow, " +
           "AVG(p.linkQualityScore) as avgQuality, " +
           "AVG(p.keyNegotiationLatencyMs) as avgLatency, " +
           "AVG(p.quantumBitErrorRate) as avgQBER, " +
           "COUNT(p) as recordCount " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.sessionId = :sessionId " +
           "AND p.sampledAt >= :startTime AND p.sampledAt <= :endTime " +
           "AND p.isValid = true " +
           "GROUP BY DATE_TRUNC('hour', p.sampledAt) " +
           "ORDER BY hourWindow")
    List<Object[]> getSessionPerformanceTrend(@Param("sessionId") String sessionId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 复杂查询：查找最佳性能配置
     * 返回每种协议和策略组合下的平均性能指标
     */
    @Query("SELECT p.qkdProtocol, p.optimizationStrategy, " +
           "AVG(p.linkQualityScore) as avgQuality, " +
           "AVG(p.keyNegotiationLatencyMs) as avgLatency, " +
           "AVG(p.keyDistributionSuccessRate) as avgSuccessRate, " +
           "COUNT(p) as sampleCount " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.isValid = true " +
           "GROUP BY p.qkdProtocol, p.optimizationStrategy " +
           "HAVING COUNT(p) >= 5 " +  // 至少有5个样本
           "ORDER BY avgQuality DESC, avgSuccessRate DESC, avgLatency ASC")
    List<Object[]> findOptimalConfigurations();

    /**
     * 复杂查询：检测性能异常
     * 查找与历史模式显著偏离的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.sessionId = :sessionId " +
           "AND p.isValid = true " +
           "AND p.sampledAt >= :startTime " +
           "AND (" +
           "  (p.linkQualityScore < :qualityThreshold) OR " +
           "  (p.keyNegotiationLatencyMs > :latencyThreshold) OR " +
           "  (p.quantumBitErrorRate > :qberThreshold) OR " +
           "  (p.keyDistributionSuccessRate < :successThreshold)" +
           ") " +
           "ORDER BY p.sampledAt DESC")
    List<QuantumCommunicationPerformanceEntity> detectPerformanceAnomalies(
        @Param("sessionId") String sessionId,
        @Param("startTime") LocalDateTime startTime,
        @Param("qualityThreshold") Integer qualityThreshold,
        @Param("latencyThreshold") Long latencyThreshold,
        @Param("qberThreshold") Double qberThreshold,
        @Param("successThreshold") Double successThreshold);

    /**
     * 复杂查询：获取协议切换效果评估
     */
    @Query("SELECT p1, p2 FROM QuantumCommunicationPerformanceEntity p1 " +
           "JOIN QuantumCommunicationPerformanceEntity p2 " +
           "ON p1.sessionId = p2.sessionId AND p1.userId = p2.userId " +
           "WHERE p1.sampledAt < p2.sampledAt " +
           "AND DATE_TRUNC('minute', p1.sampledAt) = DATE_TRUNC('minute', p2.sampledAt) " +
           "AND p1.qkdProtocol <> p2.qkdProtocol " +
           "AND p1.isValid = true AND p2.isValid = true " +
           "ORDER BY p1.sampledAt DESC")
    List<Object[]> findProtocolSwitchEffects();

    /**
     * 复杂查询：获取用户性能排名
     */
    @Query("SELECT p.userId, " +
           "AVG(p.linkQualityScore) as avgQuality, " +
           "AVG(p.keyDistributionSuccessRate) as avgSuccessRate, " +
           "COUNT(p) as totalRecords " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.sampledAt >= :startTime AND p.sampledAt <= :endTime " +
           "AND p.isValid = true " +
           "GROUP BY p.userId " +
           "HAVING COUNT(p) >= :minRecords " +
           "ORDER BY avgQuality DESC, avgSuccessRate DESC")
    List<Object[]> getUserPerformanceRanking(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("minRecords") Integer minRecords);

    /**
     * 复杂查询：分析优化策略效果
     */
    @Query("SELECT p.optimizationStrategy, " +
           "AVG(p.optimizationImprovementPercent) as avgImprovement, " +
           "AVG(p.latencyReducedMs) as avgLatencyReduction, " +
           "AVG(p.bandwidthSavedKb) as avgBandwidthSaved, " +
           "COUNT(p) as strategyCount " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.optimizationImprovementPercent IS NOT NULL " +
           "AND p.isValid = true " +
           "GROUP BY p.optimizationStrategy " +
           "ORDER BY avgImprovement DESC")
    List<Object[]> analyzeOptimizationStrategyEffectiveness();

    /**
     * 复杂查询：获取设备性能对比
     */
    @Query("SELECT p.deviceId, " +
           "AVG(p.linkQualityScore) as avgQuality, " +
           "AVG(p.keyNegotiationLatencyMs) as avgLatency, " +
           "AVG(p.signalStrength) as avgSignal, " +
           "COUNT(p) as recordCount " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.deviceId IS NOT NULL " +
           "AND p.sampledAt >= :startTime AND p.sampledAt <= :endTime " +
           "AND p.isValid = true " +
           "GROUP BY p.deviceId " +
           "HAVING COUNT(p) >= 10 " +  // 至少有10条记录
           "ORDER BY avgQuality DESC")
    List<Object[]> compareDevicePerformance(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 复杂查询：查找性能关联模式
     * 分析不同因素之间的相关性
     */
    @Query("SELECT " +
           "CORR(p.linkQualityScore, p.signalStrength) as qualitySignalCorrelation, " +
           "CORR(p.quantumBitErrorRate, p.environmentNoiseLevel) as qberNoiseCorrelation, " +
           "CORR(p.keyNegotiationLatencyMs, p.temperature) as latencyTempCorrelation, " +
           "COUNT(p) as sampleSize " +
           "FROM QuantumCommunicationPerformanceEntity p " +
           "WHERE p.isValid = true " +
           "AND p.linkQualityScore IS NOT NULL " +
           "AND p.signalStrength IS NOT NULL " +
           "AND p.quantumBitErrorRate IS NOT NULL " +
           "AND p.environmentNoiseLevel IS NOT NULL " +
           "AND p.keyNegotiationLatencyMs IS NOT NULL " +
           "AND p.temperature IS NOT NULL")
    List<Object[]> findPerformanceCorrelations();

    /**
     * 自定义原生SQL查询：复杂统计分析
     * 使用原生SQL处理复杂统计计算
     */
    @Query(value = 
           "WITH performance_stats AS (" +
           "  SELECT " +
           "    session_id, " +
           "    user_id, " +
           "    AVG(link_quality_score) as mean_quality, " +
           "    STDDEV(link_quality_score) as std_quality, " +
           "    AVG(key_negotiation_latency_ms) as mean_latency, " +
           "    STDDEV(key_negotiation_latency_ms) as std_latency, " +
           "    AVG(quantum_bit_error_rate) as mean_qber, " +
           "    STDDEV(quantum_bit_error_rate) as std_qber, " +
           "    COUNT(*) as record_count " +
           "  FROM quantum_communication_performance " +
           "  WHERE is_valid = true " +
           "  AND sampled_at >= :startTime AND sampled_at <= :endTime " +
           "  GROUP BY session_id, user_id " +
           "  HAVING COUNT(*) >= 5" +
           ") " +
           "SELECT " +
           "  session_id, " +
           "  user_id, " +
           "  mean_quality, " +
           "  std_quality, " +
           "  mean_latency, " +
           "  std_latency, " +
           "  mean_qber, " +
           "  std_qber, " +
           "  record_count, " +
           "  CASE " +
           "    WHEN mean_quality > 80 AND std_quality < 10 AND mean_latency < 300 THEN 'EXCELLENT' " +
           "    WHEN mean_quality > 70 AND std_quality < 15 AND mean_latency < 500 THEN 'GOOD' " +
           "    WHEN mean_quality > 60 AND mean_latency < 800 THEN 'FAIR' " +
           "    ELSE 'POOR' " +
           "  END as performance_category " +
           "FROM performance_stats " +
           "ORDER BY mean_quality DESC, mean_latency ASC",
           nativeQuery = true)
    List<Object[]> getDetailedPerformanceStatistics(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询：获取性能记录分页列表
     * 注意：这个方法需要配合Pageable参数使用
     */
    // List<QuantumCommunicationPerformanceEntity> findBySessionId(String sessionId, Pageable pageable);

    /**
     * 分页查询：获取用户性能记录分页列表
     */
    // List<QuantumCommunicationPerformanceEntity> findByUserId(String userId, Pageable pageable);

    /**
     * 分页查询：根据时间范围获取记录分页列表
     */
    // List<QuantumCommunicationPerformanceEntity> findBySampledAtBetween(
    //     LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}