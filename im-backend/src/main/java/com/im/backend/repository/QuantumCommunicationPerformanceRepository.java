package com.im.backend.repository;

import com.im.backend.entity.QuantumCommunicationPerformanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 量子通信性能数据仓库
 * Quantum Communication Performance Repository
 * 
 * 提供量子通信性能记录的持久化操作和复杂查询
 */
@Repository
public interface QuantumCommunicationPerformanceRepository extends 
        JpaRepository<QuantumCommunicationPerformanceEntity, Long> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据链路ID查询所有性能记录
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkId(String linkId);

    /**
     * 根据链路ID查询最近的记录（按时间倒序）
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkIdOrderByTimestampDesc(String linkId);

    /**
     * 根据链路ID查询最近的N条记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId ORDER BY p.timestamp DESC")
    List<QuantumCommunicationPerformanceEntity> findRecentByLinkId(@Param("linkId") String linkId, @Param("limit") int limit);

    /**
     * 根据协议类型查询
     */
    List<QuantumCommunicationPerformanceEntity> findByProtocolType(String protocolType);

    /**
     * 根据加密策略查询
     */
    List<QuantumCommunicationPerformanceEntity> findByEncryptionStrategy(String encryptionStrategy);

    /**
     * 查询活跃的记录
     */
    List<QuantumCommunicationPerformanceEntity> findByIsActiveTrue();

    /**
     * 根据链路ID和活跃状态查询
     */
    List<QuantumCommunicationPerformanceEntity> findByLinkIdAndIsActiveTrue(String linkId);

    /**
     * 根据QoS级别查询
     */
    List<QuantumCommunicationPerformanceEntity> findByQosLevel(String qosLevel);

    /**
     * 根据安全级别查询
     */
    List<QuantumCommunicationPerformanceEntity> findBySecurityLevel(String securityLevel);

    /**
     * 根据链路ID和时间范围查询
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId AND p.timestamp BETWEEN :start AND :end ORDER BY p.timestamp DESC")
    List<QuantumCommunicationPerformanceEntity> findByLinkIdAndTimeRange(
            @Param("linkId") String linkId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 根据时间范围查询
     */
    List<QuantumCommunicationPerformanceEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // ==================== 性能指标查询 ====================

    /**
     * 获取链路的最新性能记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId ORDER BY p.timestamp DESC")
    Optional<QuantumCommunicationPerformanceEntity> findLatestByLinkId(@Param("linkId") String linkId);

    /**
     * 查询延迟超过阈值的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.latency > :threshold")
    List<QuantumCommunicationPerformanceEntity> findByLatencyAboveThreshold(@Param("threshold") Double threshold);

    /**
     * 查询QBER超过阈值的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.qber > :threshold")
    List<QuantumCommunicationPerformanceEntity> findByQberAboveThreshold(@Param("threshold") Double threshold);

    /**
     * 查询密钥率低于阈值的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.keyRate < :threshold")
    List<QuantumCommunicationPerformanceEntity> findByKeyRateBelowThreshold(@Param("threshold") Double threshold);

    /**
     * 查询成功率低于阈值的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.successRate < :threshold")
    List<QuantumCommunicationPerformanceEntity> findBySuccessRateBelowThreshold(@Param("threshold") Double threshold);

    /**
     * 查询质量评分低于阈值的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.qualityScore < :threshold")
    List<QuantumCommunicationPerformanceEntity> findByQualityScoreBelowThreshold(@Param("threshold") Double threshold);

    // ==================== 统计分析查询 ====================

    /**
     * 计算链路的平均密钥率
     */
    @Query("SELECT AVG(p.keyRate) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateAverageKeyRateByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的平均延迟
     */
    @Query("SELECT AVG(p.latency) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateAverageLatencyByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的平均QBER
     */
    @Query("SELECT AVG(p.qber) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateAverageQberByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的平均成功率
     */
    @Query("SELECT AVG(p.successRate) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateAverageSuccessRateByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的平均质量评分
     */
    @Query("SELECT AVG(p.qualityScore) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateAverageQualityScoreByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的最大密钥率
     */
    @Query("SELECT MAX(p.keyRate) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateMaxKeyRateByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的最小延迟
     */
    @Query("SELECT MIN(p.latency) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateMinLatencyByLinkId(@Param("linkId") String linkId);

    /**
     * 计算链路的密钥率标准差
     */
    @Query("SELECT STDDEV(p.keyRate) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Double calculateKeyRateStdDevByLinkId(@Param("linkId") String linkId);

    /**
     * 按协议类型统计记录数量
     */
    @Query("SELECT p.protocolType as protocolType, COUNT(p) as count FROM QuantumCommunicationPerformanceEntity p GROUP BY p.protocolType")
    List<Map<String, Object>> countByProtocolType();

    /**
     * 按QoS级别统计记录数量
     */
    @Query("SELECT p.qosLevel as qosLevel, COUNT(p) as count FROM QuantumCommunicationPerformanceEntity p GROUP BY p.qosLevel")
    List<Map<String, Object>> countByQosLevel();

    /**
     * 按加密策略统计记录数量
     */
    @Query("SELECT p.encryptionStrategy as encryptionStrategy, COUNT(p) as count FROM QuantumCommunicationPerformanceEntity p GROUP BY p.encryptionStrategy")
    List<Map<String, Object>> countByEncryptionStrategy();

    /**
     * 按安全级别统计记录数量
     */
    @Query("SELECT p.securityLevel as securityLevel, COUNT(p) as count FROM QuantumCommunicationPerformanceEntity p GROUP BY p.securityLevel")
    List<Map<String, Object>> countBySecurityLevel();

    // ==================== 高级查询方法 ====================

    /**
     * 查询高质量的链路（质量评分>=8.0）
     */
    @Query("SELECT DISTINCT p.linkId FROM QuantumCommunicationPerformanceEntity p WHERE p.qualityScore >= 8.0")
    List<String> findHighQualityLinkIds();

    /**
     * 查询需要优化的链路
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.needsOptimization = true")
    List<QuantumCommunicationPerformanceEntity> findLinksNeedingOptimization();

    /**
     * 查询最近需要优化的链路
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.needsOptimization = true AND p.timestamp > :since")
    List<QuantumCommunicationPerformanceEntity> findRecentLinksNeedingOptimization(@Param("since") LocalDateTime since);

    /**
     * 查询推荐的协议切换
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.recommendedProtocol IS NOT NULL AND p.recommendedProtocol != p.protocolType")
    List<QuantumCommunicationPerformanceEntity> findProtocolSwitchRecommendations();

    /**
     * 查询特定QoS级别的活跃链路
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.qosLevel = :qosLevel AND p.isActive = true")
    List<QuantumCommunicationPerformanceEntity> findActiveByQosLevel(@Param("qosLevel") String qosLevel);

    /**
     * 查询特定安全级别的活跃链路
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.securityLevel = :securityLevel AND p.isActive = true")
    List<QuantumCommunicationPerformanceEntity> findActiveBySecurityLevel(@Param("securityLevel") String securityLevel);

    // ==================== 批量操作 ====================

    /**
     * 批量删除过期记录
     */
    @Query("DELETE FROM QuantumCommunicationPerformanceEntity p WHERE p.timestamp < :cutoffDate")
    int deleteByTimestampBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 批量更新活跃状态
     */
    @Query("UPDATE QuantumCommunicationPerformanceEntity p SET p.isActive = :active WHERE p.linkId = :linkId")
    int updateActiveStatusByLinkId(@Param("linkId") String linkId, @Param("active") boolean active);

    // ==================== 聚合查询 ====================

    /**
     * 获取所有唯一的链路ID
     */
    @Query("SELECT DISTINCT p.linkId FROM QuantumCommunicationPerformanceEntity p")
    List<String> findAllLinkIds();

    /**
     * 获取活跃的唯一链路ID
     */
    @Query("SELECT DISTINCT p.linkId FROM QuantumCommunicationPerformanceEntity p WHERE p.isActive = true")
    List<String> findActiveLinkIds();

    /**
     * 统计总记录数
     */
    @Query("SELECT COUNT(p) FROM QuantumCommunicationPerformanceEntity p")
    Long countTotalRecords();

    /**
     * 统计活跃记录数
     */
    @Query("SELECT COUNT(p) FROM QuantumCommunicationPerformanceEntity p WHERE p.isActive = true")
    Long countActiveRecords();

    /**
     * 统计特定链路的记录数
     */
    @Query("SELECT COUNT(p) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId")
    Long countByLinkId(@Param("linkId") String linkId);

    // ==================== 性能趋势查询 ====================

    /**
     * 获取链路的时间序列性能数据
     */
    @Query("SELECT p.timestamp as timestamp, p.qualityScore as qualityScore, p.keyRate as keyRate, p.latency as latency, p.qber as qber FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId AND p.timestamp BETWEEN :start AND :end ORDER BY p.timestamp")
    List<Map<String, Object>> findTimeSeriesDataByLinkIdAndTimeRange(
            @Param("linkId") String linkId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 获取最新的N条活跃记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.isActive = true ORDER BY p.timestamp DESC")
    List<QuantumCommunicationPerformanceEntity> findLatestActiveRecords(@Param("limit") int limit);

    /**
     * 查询异常记录（QBER过高或成功率过低）
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.qber > :qberThreshold OR p.successRate < :successThreshold")
    List<QuantumCommunicationPerformanceEntity> findAnomalousRecords(
            @Param("qberThreshold") Double qberThreshold,
            @Param("successThreshold") Double successThreshold);

    /**
     * 查询低质量链路
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.qualityScore < :threshold ORDER BY p.qualityScore ASC")
    List<QuantumCommunicationPerformanceEntity> findLowQualityRecords(@Param("threshold") Double threshold);

    // ==================== 优化相关查询 ====================

    /**
     * 查询已优化的记录
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.optimizationApplied = true")
    List<QuantumCommunicationPerformanceEntity> findOptimizedRecords();

    /**
     * 查询特定链路的优化历史
     */
    @Query("SELECT p FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId AND p.optimizationApplied = true ORDER BY p.timestamp DESC")
    List<QuantumCommunicationPerformanceEntity> findOptimizationHistoryByLinkId(@Param("linkId") String linkId);

    /**
     * 计算优化效果统计
     */
    @Query("SELECT AVG(p.improvementPercentage) FROM QuantumCommunicationPerformanceEntity p WHERE p.linkId = :linkId AND p.optimizationApplied = true")
    Double calculateAverageImprovementByLinkId(@Param("linkId") String linkId);
}
