package com.im.webhook.repository;

import com.im.webhook.model.WebhookConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Webhook配置仓库
 */
@Repository
public interface WebhookConfigRepository extends JpaRepository<WebhookConfig, String> {
    
    /**
     * 根据状态查询配置
     */
    List<WebhookConfig> findByStatus(WebhookConfig.WebhookStatus status);
    
    /**
     * 根据应用ID和状态查询
     */
    List<WebhookConfig> findByAppIdAndStatus(String appId, WebhookConfig.WebhookStatus status);
    
    /**
     * 根据应用ID查询所有配置
     */
    List<WebhookConfig> findByAppId(String appId);
    
    /**
     * 统计应用的Webhook数量
     */
    long countByAppId(String appId);
    
    /**
     * 查询活跃但长时间未触发的配置
     */
    @Query("SELECT w FROM WebhookConfig w WHERE w.status = 'ACTIVE' AND w.lastTriggeredAt < :since")
    List<WebhookConfig> findInactiveSince(@Param("since") LocalDateTime since);
    
    /**
     * 查询失败率高的配置
     */
    @Query("SELECT w FROM WebhookConfig w WHERE w.failureCount > 0 AND (w.failureCount * 1.0 / w.triggerCount) > :threshold")
    List<WebhookConfig> findHighFailureRate(@Param("threshold") double threshold);
}
