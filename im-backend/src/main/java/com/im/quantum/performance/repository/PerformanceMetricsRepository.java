package com.im.quantum.performance.repository;

import com.im.quantum.performance.entity.PerformanceMetricsEntity;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.AlertLevel;
import com.im.quantum.performance.entity.PerformanceMetricsEntity.MetricType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 性能指标数据访问层
 * 
 * @author Quantum Performance Team
 * @since 2026-03-26
 */
@Repository
public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetricsEntity, String> {
    
    /**
     * 查询会话的最新性能指标
     */
    Optional<PerformanceMetricsEntity> findTopBySessionIdAndMetricTypeOrderByTimestampDesc(
        String sessionId, MetricType metricType);
    
    /**
     * 查询会话的所有性能指标
     */
    List<PerformanceMetricsEntity> findBySessionIdAndMetricTypeAndTimestampBetween(
        String sessionId, MetricType metricType, Instant from, Instant to, Pageable pageable);
    
    /**
     * 查询会话在时间段内的所有指标
     */
    @Query("SELECT m FROM PerformanceMetricsEntity m WHERE m.sessionId = :sessionId AND m.timestamp BETWEEN :from AND :to ORDER BY m.timestamp DESC")
    List<PerformanceMetricsEntity> findBySessionIdAndTimestampBetween(
        @Param("sessionId") String sessionId, 
        @Param("from") Instant from, 
        @Param("to") Instant to);
    
    /**
     * 查询活动告警
     */
    @Query("SELECT m FROM PerformanceMetricsEntity m WHERE m.alertLevel >= :minLevel ORDER BY m.timestamp DESC")
    List<PerformanceMetricsEntity> findActiveAlerts(@Param("minLevel") AlertLevel minLevel);
    
    /**
     * 查询时间段内的告警
     */
    @Query("SELECT m FROM PerformanceMetricsEntity m WHERE m.timestamp >= :since AND m.alertLevel >= :minLevel ORDER BY m.timestamp DESC")
    List<PerformanceMetricsEntity> findAlertsSince(
        @Param("since") Instant since, 
        @Param("minLevel") AlertLevel minLevel);
    
    /**
     * 按告警级别统计
     */
    @Query("SELECT m.alertLevel, COUNT(m) FROM PerformanceMetricsEntity m WHERE m.timestamp >= :since GROUP BY m.alertLevel")
    List<Object[]> countAlertsByLevelSince(@Param("since") Instant since);
    
    /**
     * 删除过期数据
     */
    @Modifying
    @Query("DELETE FROM PerformanceMetricsEntity m WHERE m.timestamp < :cutoff")
    int deleteByTimestampBefore(@Param("cutoff") Instant cutoff);
    
    /**
     * 查询会话的最新指标（所有类型）
     */
    @Query(value = "SELECT m.* FROM quantum_performance_metrics m " +
        "INNER JOIN (SELECT session_id, metric_type, MAX(timestamp) as max_ts " +
        "FROM quantum_performance_metrics WHERE session_id = :sessionId " +
        "GROUP BY session_id, metric_type) latest " +
        "ON m.session_id = latest.session_id AND m.metric_type = latest.metric_type AND m.timestamp = latest.max_ts",
        nativeQuery = true)
    List<PerformanceMetricsEntity> findLatestMetricsBySession(@Param("sessionId") String sessionId);
    
    /**
     * 计算会话的平均性能分数
     */
    @Query("SELECT AVG(m.keyGenerationRate) FROM PerformanceMetricsEntity m WHERE m.sessionId = :sessionId AND m.timestamp >= :since")
    Double calculateAverageKeyRate(@Param("sessionId") String sessionId, @Param("since") Instant since);
    
    /**
     * 统计所有会话
     */
    @Query("SELECT COUNT(DISTINCT m.sessionId) FROM PerformanceMetricsEntity m WHERE m.timestamp >= :since")
    Long countActiveSessions(@Param("since") Instant since);
}
