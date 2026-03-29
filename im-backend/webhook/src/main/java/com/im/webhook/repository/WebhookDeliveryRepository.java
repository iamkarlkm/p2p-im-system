package com.im.webhook.repository;

import com.im.webhook.model.WebhookDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Webhook投递记录仓库
 */
@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, String> {
    
    /**
     * 根据事件ID查询投递记录
     */
    List<WebhookDelivery> findByEventId(String eventId);
    
    /**
     * 根据WebhookID查询投递记录
     */
    List<WebhookDelivery> findByWebhookId(String webhookId);
    
    /**
     * 根据状态查询投递记录
     */
    List<WebhookDelivery> findByStatus(WebhookDelivery.DeliveryStatus status);
    
    /**
     * 查询时间范围内的投递记录
     */
    List<WebhookDelivery> findByWebhookIdAndDeliveredAtBetween(
            String webhookId,
            LocalDateTime start,
            LocalDateTime end
    );
    
    /**
     * 统计平均响应时间
     */
    @Query("SELECT AVG(d.responseTimeMs) FROM WebhookDelivery d WHERE d.webhookId = :webhookId AND d.deliveredAt BETWEEN :start AND :end")
    Double getAverageResponseTime(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 统计成功率
     */
    @Query("SELECT COUNT(d) FROM WebhookDelivery d WHERE d.webhookId = :webhookId AND d.status = 'SUCCESS' AND d.deliveredAt BETWEEN :start AND :end")
    long countSuccessfulDeliveries(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 统计失败率
     */
    @Query("SELECT COUNT(d) FROM WebhookDelivery d WHERE d.webhookId = :webhookId AND d.status != 'SUCCESS' AND d.deliveredAt BETWEEN :start AND :end")
    long countFailedDeliveries(
            @Param("webhookId") String webhookId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
    
    /**
     * 删除过期投递记录
     */
    void deleteByDeliveredAtBefore(LocalDateTime before);
}
