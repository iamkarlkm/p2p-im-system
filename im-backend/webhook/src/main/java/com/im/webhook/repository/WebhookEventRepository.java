package com.im.webhook.repository;

import com.im.webhook.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Webhook事件仓库
 */
@Repository
public interface WebhookEventRepository extends JpaRepository<WebhookEvent, String> {
    
    /**
     * 根据WebhookID查询事件
     */
    List<WebhookEvent> findByWebhookId(String webhookId);
    
    /**
     * 根据WebhookID和状态查询
     */
    List<WebhookEvent> findByWebhookIdAndStatus(String webhookId, WebhookEvent.EventStatus status);
    
    /**
     * 根据状态查询事件
     */
    List<WebhookEvent> findByStatus(WebhookEvent.EventStatus status);
    
    /**
     * 查询时间范围内的事件
     */
    List<WebhookEvent> findByWebhookIdAndCreatedAtBetween(
            String webhookId, 
            LocalDateTime start, 
            LocalDateTime end
    );
    
    /**
     * 统计事件数量
     */
    long countByWebhookIdAndStatus(String webhookId, WebhookEvent.EventStatus status);
    
    /**
     * 统计时间范围内的事件
     */
    @Query("SELECT COUNT(e) FROM WebhookEvent e WHERE e.webhookId = :webhookId AND e.createdAt BETWEEN :start AND :end")
    long countByWebhookIdAndTimeRange(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 获取状态分布统计
     */
    @Query("SELECT e.status as status, COUNT(e) as count FROM WebhookEvent e WHERE e.webhookId = :webhookId AND e.createdAt BETWEEN :start AND :end GROUP BY e.status")
    List<Map<String, Object>> getStatusDistribution(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 获取事件类型分布
     */
    @Query("SELECT e.eventType as eventType, COUNT(e) as count FROM WebhookEvent e WHERE e.webhookId = :webhookId AND e.createdAt BETWEEN :start AND :end GROUP BY e.eventType")
    List<Map<String, Object>> getEventTypeDistribution(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 获取综合统计
     */
    default Map<String, Object> getStats(String webhookId, LocalDateTime start, LocalDateTime end) {
        // 简化实现，返回基本统计
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("webhookId", webhookId);
        stats.put("startTime", start);
        stats.put("endTime", end);
        stats.put("totalEvents", countByWebhookIdAndTimeRange(webhookId, start, end));
        stats.put("successCount", countByWebhookIdAndStatus(webhookId, WebhookEvent.EventStatus.SUCCESS));
        stats.put("failedCount", countByWebhookIdAndStatus(webhookId, WebhookEvent.EventStatus.FAILED));
        return stats;
    }
    
    /**
     * 查询需要重试的事件
     */
    List<WebhookEvent> findByStatusInAndRetryCountLessThan(
            List<WebhookEvent.EventStatus> statuses,
            int maxRetryCount
    );
    
    /**
     * 删除过期事件
     */
    void deleteByCreatedAtBefore(LocalDateTime before);
}
