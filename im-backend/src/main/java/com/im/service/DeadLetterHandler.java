package com.im.service;

import com.im.entity.DeadLetterMessage;
import com.im.entity.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 死信消息处理器
 * 功能 #1: 消息队列核心系统 - 死信队列处理
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class DeadLetterHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DeadLetterHandler.class);
    
    // 死信队列内存存储
    private final Map<String, DeadLetterMessage> deadLetterStore = new ConcurrentHashMap<>();
    
    // 重试回调函数
    private DeadLetterRetryCallback retryCallback;
    
    /**
     * 处理死信消息
     */
    public void handle(DeadLetterMessage deadLetter) {
        deadLetter.setDeadLetterId(generateDeadLetterId());
        deadLetterStore.put(deadLetter.getDeadLetterId(), deadLetter);
        
        logger.warn("Dead letter message stored: id={}, originalId={}, reason={}",
            deadLetter.getDeadLetterId(),
            deadLetter.getOriginalMessageId(),
            deadLetter.getReason());
        
        // 触发告警
        sendAlert(deadLetter);
    }
    
    /**
     * 重试死信消息
     */
    public boolean retry(String deadLetterId) {
        DeadLetterMessage deadLetter = deadLetterStore.get(deadLetterId);
        if (deadLetter == null) {
            logger.error("Dead letter not found: {}", deadLetterId);
            return false;
        }
        
        if (Boolean.TRUE.equals(deadLetter.getRetried())) {
            logger.warn("Dead letter already retried: {}", deadLetterId);
            return false;
        }
        
        // 转换回原始消息
        QueueMessage originalMessage = deadLetter.toOriginalMessage();
        
        // 调用重试回调
        if (retryCallback != null) {
            boolean success = retryCallback.retry(originalMessage);
            if (success) {
                deadLetter.markRetried();
                logger.info("Dead letter retry succeeded: {}", deadLetterId);
                return true;
            } else {
                logger.error("Dead letter retry failed: {}", deadLetterId);
                return false;
            }
        }
        
        logger.error("No retry callback registered");
        return false;
    }
    
    /**
     * 标记为已解决
     */
    public boolean resolve(String deadLetterId) {
        DeadLetterMessage deadLetter = deadLetterStore.get(deadLetterId);
        if (deadLetter == null) return false;
        
        deadLetter.markResolved();
        logger.info("Dead letter resolved: {}", deadLetterId);
        return true;
    }
    
    /**
     * 获取死信详情
     */
    public DeadLetterMessage getDeadLetter(String deadLetterId) {
        return deadLetterStore.get(deadLetterId);
    }
    
    /**
     * 获取队列的所有死信
     */
    public List<DeadLetterMessage> getDeadLettersByQueue(String queueName) {
        return deadLetterStore.values().stream()
            .filter(dl -> queueName.equals(dl.getQueueName()))
            .collect(Collectors.toList());
    }
    
    /**
     * 获取所有未解决死信
     */
    public List<DeadLetterMessage> getUnresolvedDeadLetters() {
        return deadLetterStore.values().stream()
            .filter(dl -> !Boolean.TRUE.equals(dl.getResolved()))
            .collect(Collectors.toList());
    }
    
    /**
     * 按原因统计死信
     */
    public Map<DeadLetterMessage.DeadLetterReason, Long> countByReason() {
        return deadLetterStore.values().stream()
            .collect(Collectors.groupingBy(
                DeadLetterMessage::getReason,
                Collectors.counting()
            ));
    }
    
    /**
     * 清理已解决的老死信
     */
    public int cleanupResolved(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        
        List<String> toRemove = deadLetterStore.values().stream()
            .filter(dl -> Boolean.TRUE.equals(dl.getResolved()))
            .filter(dl -> dl.getResolveTime() != null && dl.getResolveTime().isBefore(cutoff))
            .map(DeadLetterMessage::getDeadLetterId)
            .collect(Collectors.toList());
        
        toRemove.forEach(deadLetterStore::remove);
        logger.info("Cleaned up {} resolved dead letters older than {} days", toRemove.size(), daysOld);
        
        return toRemove.size();
    }
    
    /**
     * 注册重试回调
     */
    public void setRetryCallback(DeadLetterRetryCallback callback) {
        this.retryCallback = callback;
    }
    
    /**
     * 发送告警
     */
    private void sendAlert(DeadLetterMessage deadLetter) {
        // 实际项目中这里会集成告警系统
        logger.warn("ALERT: Dead letter message detected - queue={}, reason={}, messageId={}",
            deadLetter.getQueueName(),
            deadLetter.getReason(),
            deadLetter.getOriginalMessageId());
    }
    
    /**
     * 生成死信ID
     */
    private String generateDeadLetterId() {
        return "DL" + System.currentTimeMillis() + "-" + deadLetterStore.size();
    }
    
    /**
     * 重试回调接口
     */
    @FunctionalInterface
    public interface DeadLetterRetryCallback {
        boolean retry(QueueMessage message);
    }
    
    /**
     * 获取统计信息
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalCount", deadLetterStore.size());
        stats.put("unresolvedCount", getUnresolvedDeadLetters().size());
        stats.put("countByReason", countByReason());
        return stats;
    }
}
