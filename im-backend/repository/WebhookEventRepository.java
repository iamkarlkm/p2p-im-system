package com.im.system.repository;

import com.im.system.entity.WebhookEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Webhook 事件仓储接口
 * 提供对 WebhookEventEntity 的 CRUD 操作和复杂查询
 */
@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {
    
    // ===================== 基础查询 =====================
    
    /**
     * 根据事件类型查询
     */
    List<WebhookEventEntity> findByEventType(String eventType);
    
    /**
     * 根据事件类型分页查询
     */
    Page<WebhookEventEntity> findByEventType(String eventType, Pageable pageable);
    
    /**
     * 根据事件类型和子类型查询
     */
    List<WebhookEventEntity> findByEventTypeAndEventSubtype(String eventType, String eventSubtype);
    
    /**
     * 根据投递状态查询
     */
    List<WebhookEventEntity> findByDeliveryStatus(WebhookEventEntity.DeliveryStatus deliveryStatus);
    
    /**
     * 根据投递状态分页查询
     */
    Page<WebhookEventEntity> findByDeliveryStatus(WebhookEventEntity.DeliveryStatus deliveryStatus, Pageable pageable);
    
    /**
     * 根据投递状态和优先级查询
     */
    List<WebhookEventEntity> findByDeliveryStatusAndPriorityLessThanEqual(
            WebhookEventEntity.DeliveryStatus deliveryStatus, Integer priority);
    
    /**
     * 根据订阅ID查询
     */
    List<WebhookEventEntity> findBySubscriptionId(UUID subscriptionId);
    
    /**
     * 根据Webhook URL查询
     */
    List<WebhookEventEntity> findByWebhookUrl(String webhookUrl);
    
    // ===================== 复合状态查询 =====================
    
    /**
     * 查询需要重试的事件（投递失败且未超过最大重试次数）
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus = :status " +
           "AND e.deliveryAttempts < e.maxDeliveryAttempts " +
           "AND (e.nextDeliveryAttempt IS NULL OR e.nextDeliveryAttempt <= :currentTime) " +
           "ORDER BY e.priority ASC, e.createdAt ASC")
    List<WebhookEventEntity> findRetryableEvents(
            @Param("status") WebhookEventEntity.DeliveryStatus status,
            @Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询待投递的事件（PENDING 或 QUEUED 状态）
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus IN ('PENDING', 'QUEUED') " +
           "AND e.deadLettered = false " +
           "ORDER BY e.priority ASC, e.createdAt ASC")
    List<WebhookEventEntity> findPendingEvents();
    
    /**
     * 查询处理中的事件（PROCESSING 或 DELIVERING 状态）
     */
    List<WebhookEventEntity> findByDeliveryStatusIn(List<WebhookEventEntity.DeliveryStatus> statuses);
    
    /**
     * 查询过期的死信事件（dead_lettered = true 且超过TTL）
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deadLettered = true " +
           "AND e.ttlExpiresAt <= :currentTime")
    List<WebhookEventEntity> findExpiredDeadLetters(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询需要归档的事件（已投递成功且超过保留期限）
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus = 'DELIVERED' " +
           "AND e.archived = false " +
           "AND e.ttlExpiresAt <= :currentTime")
    List<WebhookEventEntity> findEventsReadyForArchiving(@Param("currentTime") LocalDateTime currentTime);
    
    // ===================== 时间范围查询 =====================
    
    /**
     * 根据创建时间范围查询
     */
    List<WebhookEventEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据事件时间范围查询
     */
    List<WebhookEventEntity> findByEventTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查询最近N小时内创建的事件
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.createdAt >= :since")
    List<WebhookEventEntity> findCreatedSince(@Param("since") LocalDateTime since);
    
    /**
     * 查询最近一次投递尝试时间
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.lastDeliveryAttempt >= :since")
    List<WebhookEventEntity> findLastDeliveryAttemptSince(@Param("since") LocalDateTime since);
    
    // ===================== 统计查询 =====================
    
    /**
     * 统计各事件类型的数量
     */
    @Query("SELECT e.eventType, COUNT(e) FROM WebhookEventEntity e GROUP BY e.eventType")
    List<Object[]> countByEventType();
    
    /**
     * 统计各投递状态的数量
     */
    @Query("SELECT e.deliveryStatus, COUNT(e) FROM WebhookEventEntity e GROUP BY e.deliveryStatus")
    List<Object[]> countByDeliveryStatus();
    
    /**
     * 统计各优先级的数量
     */
    @Query("SELECT e.priority, COUNT(e) FROM WebhookEventEntity e GROUP BY e.priority ORDER BY e.priority")
    List<Object[]> countByPriority();
    
    /**
     * 统计成功率（已投递 / 总投递尝试）
     */
    @Query("SELECT AVG(CASE WHEN e.deliveryStatus = 'DELIVERED' THEN 1.0 ELSE 0.0 END) " +
           "FROM WebhookEventEntity e WHERE e.deliveryAttempts > 0")
    Double calculateDeliverySuccessRate();
    
    /**
     * 统计平均投递延迟
     */
    @Query("SELECT AVG(e.deliveryLatencyMs) FROM WebhookEventEntity e WHERE e.deliveryLatencyMs IS NOT NULL")
    Double calculateAverageDeliveryLatency();
    
    /**
     * 统计各错误类型的数量
     */
    @Query("SELECT SUBSTRING(e.errorMessage, 1, 100), COUNT(e) FROM WebhookEventEntity e " +
           "WHERE e.errorMessage IS NOT NULL GROUP BY SUBSTRING(e.errorMessage, 1, 100)")
    List<Object[]> countByErrorMessage();
    
    /**
     * 统计各响应状态码的数量
     */
    @Query("SELECT e.lastDeliveryStatusCode, COUNT(e) FROM WebhookEventEntity e " +
           "WHERE e.lastDeliveryStatusCode IS NOT NULL GROUP BY e.lastDeliveryStatusCode")
    List<Object[]> countByStatusCode();
    
    // ===================== 批量操作 =====================
    
    /**
     * 批量更新投递状态
     */
    @Modifying
    @Query("UPDATE WebhookEventEntity e SET e.deliveryStatus = :newStatus, e.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE e.id IN :ids")
    int updateDeliveryStatus(@Param("ids") List<UUID> ids, 
                           @Param("newStatus") WebhookEventEntity.DeliveryStatus newStatus);
    
    /**
     * 批量标记为死信
     */
    @Modifying
    @Query("UPDATE WebhookEventEntity e SET e.deadLettered = true, " +
           "e.deadLetterReason = :reason, e.deadLetteredAt = CURRENT_TIMESTAMP, " +
           "e.deliveryStatus = 'DEAD_LETTER', e.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE e.id IN :ids")
    int markAsDeadLetter(@Param("ids") List<UUID> ids, @Param("reason") String reason);
    
    /**
     * 批量归档事件
     */
    @Modifying
    @Query("UPDATE WebhookEventEntity e SET e.archived = true, " +
           "e.archivedAt = CURRENT_TIMESTAMP, e.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE e.id IN :ids")
    int archiveEvents(@Param("ids") List<UUID> ids);
    
    /**
     * 批量设置下一次投递尝试时间
     */
    @Modifying
    @Query("UPDATE WebhookEventEntity e SET e.nextDeliveryAttempt = :nextAttempt, " +
           "e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    int setNextDeliveryAttempt(@Param("ids") List<UUID> ids, 
                             @Param("nextAttempt") LocalDateTime nextAttempt);
    
    /**
     * 批量清理过期事件
     */
    @Modifying
    @Query("DELETE FROM WebhookEventEntity e WHERE e.archived = true " +
           "AND e.archivedAt <= :cutoffDate")
    int deleteArchivedEventsBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * 批量删除死信事件
     */
    @Modifying
    @Query("DELETE FROM WebhookEventEntity e WHERE e.deadLettered = true " +
           "AND e.deadLetteredAt <= :cutoffDate")
    int deleteDeadLettersBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // ===================== 高级搜索查询 =====================
    
    /**
     * 全文搜索事件数据
     */
    @Query(value = "SELECT * FROM webhook_events e WHERE " +
           "to_tsvector('english', COALESCE(e.event_data, '') || ' ' || COALESCE(e.event_metadata, '')) @@ " +
           "to_tsquery('english', :query)", nativeQuery = true)
    List<WebhookEventEntity> searchByFullText(@Param("query") String query);
    
    /**
     * 根据标签搜索
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.tags LIKE %:tag%")
    List<WebhookEventEntity> searchByTag(@Param("tag") String tag);
    
    /**
     * 根据源系统搜索
     */
    List<WebhookEventEntity> findBySourceSystem(String sourceSystem);
    
    /**
     * 根据源组件搜索
     */
    List<WebhookEventEntity> findBySourceComponent(String sourceComponent);
    
    /**
     * 根据处理节点搜索
     */
    List<WebhookEventEntity> findByProcessingNode(String processingNode);
    
    // ===================== 优先级调度查询 =====================
    
    /**
     * 获取最高优先级的事件（用于调度）
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus = 'PENDING' " +
           "AND e.deadLettered = false " +
           "ORDER BY e.priority ASC, e.createdAt ASC " +
           "LIMIT :limit")
    List<WebhookEventEntity> findTopPriorityEvents(@Param("limit") int limit);
    
    /**
     * 获取需要立即重试的事件
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus = 'FAILED' " +
           "AND e.deliveryAttempts < e.maxDeliveryAttempts " +
           "AND e.nextDeliveryAttempt <= :now " +
           "ORDER BY e.priority ASC, e.createdAt ASC")
    List<WebhookEventEntity> findImmediateRetryEvents(@Param("now") LocalDateTime now);
    
    /**
     * 获取已超时的处理中事件
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.deliveryStatus IN ('PROCESSING', 'DELIVERING') " +
           "AND e.lastDeliveryAttempt <= :timeoutThreshold")
    List<WebhookEventEntity> findTimedOutEvents(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
    
    // ===================== 分区/分片查询 =====================
    
    /**
     * 根据分区键查询
     */
    List<WebhookEventEntity> findByPartitionKey(String partitionKey);
    
    /**
     * 根据分片ID查询
     */
    List<WebhookEventEntity> findByShardId(String shardId);
    
    /**
     * 查询分片中的待处理事件
     */
    @Query("SELECT e FROM WebhookEventEntity e WHERE e.shardId = :shardId " +
           "AND e.deliveryStatus = 'PENDING' " +
           "ORDER BY e.priority ASC, e.createdAt ASC")
    List<WebhookEventEntity> findPendingEventsByShard(@Param("shardId") String shardId);
    
    // ===================== 性能统计查询 =====================
    
    /**
     * 统计每个事件类型的平均投递时间
     */
    @Query("SELECT e.eventType, AVG(e.deliveryLatencyMs) FROM WebhookEventEntity e " +
           "WHERE e.deliveryLatencyMs IS NOT NULL GROUP BY e.eventType")
    List<Object[]> calculateAvgLatencyByEventType();
    
    /**
     * 统计每个Webhook URL的成功率
     */
    @Query("SELECT e.webhookUrl, " +
           "COUNT(e) as total, " +
           "SUM(CASE WHEN e.deliveryStatus = 'DELIVERED' THEN 1 ELSE 0 END) as success, " +
           "AVG(e.deliveryLatencyMs) as avgLatency " +
           "FROM WebhookEventEntity e WHERE e.webhookUrl IS NOT NULL " +
           "GROUP BY e.webhookUrl")
    List<Object[]> calculateWebhookPerformance();
    
    /**
     * 统计每小时的事件量
     */
    @Query(value = "SELECT DATE_TRUNC('hour', created_at) as hour, COUNT(*) as count " +
           "FROM webhook_events WHERE created_at >= :start AND created_at <= :end " +
           "GROUP BY DATE_TRUNC('hour', created_at) ORDER BY hour", nativeQuery = true)
    List<Object[]> countEventsByHour(@Param("start") LocalDateTime start, 
                                   @Param("end") LocalDateTime end);
    
    /**
     * 统计每日的投递成功率
     */
    @Query(value = "SELECT DATE(created_at) as day, " +
           "COUNT(*) as total, " +
           "SUM(CASE WHEN delivery_status = 'DELIVERED' THEN 1 ELSE 0 END) as success " +
           "FROM webhook_events WHERE created_at >= :start AND created_at <= :end " +
           "GROUP BY DATE(created_at) ORDER BY day", nativeQuery = true)
    List<Object[]> calculateDailySuccessRate(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
    
    // ===================== 锁机制查询 =====================
    
    /**
     * 锁定并获取待处理事件（防止并发处理）
     */
    @Query(value = "SELECT * FROM webhook_events e WHERE e.delivery_status = 'PENDING' " +
           "AND e.dead_lettered = false " +
           "ORDER BY e.priority ASC, e.created_at ASC " +
           "FOR UPDATE SKIP LOCKED LIMIT :limit", nativeQuery = true)
    List<WebhookEventEntity> lockAndGetPendingEvents(@Param("limit") int limit);
    
    /**
     * 锁定并获取需要重试的事件
     */
    @Query(value = "SELECT * FROM webhook_events e WHERE e.delivery_status = 'FAILED' " +
           "AND e.delivery_attempts < e.max_delivery_attempts " +
           "AND (e.next_delivery_attempt IS NULL OR e.next_delivery_attempt <= :now) " +
           "FOR UPDATE SKIP LOCKED LIMIT :limit", nativeQuery = true)
    List<WebhookEventEntity> lockAndGetRetryEvents(@Param("now") LocalDateTime now,
                                                 @Param("limit") int limit);
    
    // ===================== 健康检查查询 =====================
    
    /**
     * 检查是否存在长时间处理中的事件
     */
    @Query("SELECT COUNT(e) FROM WebhookEventEntity e WHERE e.deliveryStatus IN ('PROCESSING', 'DELIVERING') " +
           "AND e.lastDeliveryAttempt <= :timeoutThreshold")
    Long countStuckEvents(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
    
    /**
     * 检查死信队列大小
     */
    Long countByDeadLetteredTrue();
    
    /**
     * 检查未归档的已投递事件数量
     */
    Long countByDeliveryStatusAndArchivedFalse(WebhookEventEntity.DeliveryStatus status);
    
    /**
     * 获取最旧未处理的事件
     */
    @Query("SELECT MIN(e.createdAt) FROM WebhookEventEntity e WHERE e.deliveryStatus = 'PENDING'")
    Optional<LocalDateTime> findOldestPendingEventTime();
    
    /**
     * 获取系统中最慢的Webhook URL
     */
    @Query("SELECT e.webhookUrl, MAX(e.deliveryLatencyMs) FROM WebhookEventEntity e " +
           "WHERE e.deliveryLatencyMs IS NOT NULL GROUP BY e.webhookUrl ORDER BY MAX(e.deliveryLatencyMs) DESC")
    List<Object[]> findSlowestWebhookUrls();
}