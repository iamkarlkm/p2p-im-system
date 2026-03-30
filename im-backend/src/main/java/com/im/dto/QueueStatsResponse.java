package com.im.dto;

import java.time.LocalDateTime;

/**
 * 队列统计响应DTO
 * 功能 #1: 消息队列核心系统 - 队列统计
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class QueueStatsResponse {
    
    // ==================== 核心字段 ====================
    private String queueName;
    private String topic;
    private String status;
    private Long currentSize;
    private Long maxSize;
    private Long messageCount;
    private Long processedCount;
    private Long failedCount;
    private Double successRate;
    private Integer consumerCount;
    private Integer partitionCount;
    private LocalDateTime createTime;
    private LocalDateTime lastActiveTime;
    private Long pendingCount;
    private Long processingCount;
    private Long deadLetterCount;
    private Double throughputPerSecond;
    private Double averageProcessTimeMs;
    
    // ==================== 构造函数 ====================
    public QueueStatsResponse() {}
    
    // ==================== 业务方法 ====================
    
    /**
     * 计算使用率
     */
    public double getUsageRate() {
        if (maxSize == null || maxSize == 0) return 0.0;
        return (double) currentSize / maxSize * 100;
    }
    
    /**
     * 是否健康
     */
    public boolean isHealthy() {
        return "ACTIVE".equals(status) && getUsageRate() < 80;
    }
    
    /**
     * 获取积压数量
     */
    public long getBacklogCount() {
        return currentSize != null ? currentSize : 0;
    }
    
    // ==================== Getter & Setter ====================
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getCurrentSize() { return currentSize; }
    public void setCurrentSize(Long currentSize) { this.currentSize = currentSize; }
    
    public Long getMaxSize() { return maxSize; }
    public void setMaxSize(Long maxSize) { this.maxSize = maxSize; }
    
    public Long getMessageCount() { return messageCount; }
    public void setMessageCount(Long messageCount) { this.messageCount = messageCount; }
    
    public Long getProcessedCount() { return processedCount; }
    public void setProcessedCount(Long processedCount) { this.processedCount = processedCount; }
    
    public Long getFailedCount() { return failedCount; }
    public void setFailedCount(Long failedCount) { this.failedCount = failedCount; }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    
    public Integer getConsumerCount() { return consumerCount; }
    public void setConsumerCount(Integer consumerCount) { this.consumerCount = consumerCount; }
    
    public Integer getPartitionCount() { return partitionCount; }
    public void setPartitionCount(Integer partitionCount) { this.partitionCount = partitionCount; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    public Long getPendingCount() { return pendingCount; }
    public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
    
    public Long getProcessingCount() { return processingCount; }
    public void setProcessingCount(Long processingCount) { this.processingCount = processingCount; }
    
    public Long getDeadLetterCount() { return deadLetterCount; }
    public void setDeadLetterCount(Long deadLetterCount) { this.deadLetterCount = deadLetterCount; }
    
    public Double getThroughputPerSecond() { return throughputPerSecond; }
    public void setThroughputPerSecond(Double throughputPerSecond) { this.throughputPerSecond = throughputPerSecond; }
    
    public Double getAverageProcessTimeMs() { return averageProcessTimeMs; }
    public void setAverageProcessTimeMs(Double averageProcessTimeMs) { this.averageProcessTimeMs = averageProcessTimeMs; }
    
    @Override
    public String toString() {
        return "QueueStatsResponse{" +
                "queueName='" + queueName + '\'' +
                ", status='" + status + '\'' +
                ", currentSize=" + currentSize +
                ", successRate=" + successRate +
                '}';
    }
}
