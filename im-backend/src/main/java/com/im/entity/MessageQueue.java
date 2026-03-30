package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息队列实体类
 * 功能 #1: 消息队列核心系统 - 消息队列引擎
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class MessageQueue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 队列状态枚举 ====================
    public enum QueueStatus {
        ACTIVE("活跃"),
        PAUSED("暂停"),
        FULL("已满"),
        DESTROYED("已销毁");
        
        private final String description;
        
        QueueStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String queueName;
    private String topic;
    private String description;
    private QueueStatus status;
    private Long maxSize;
    private Long currentSize;
    private Long maxPriority;
    private Boolean persistent;
    private Long ttl;
    private Integer maxRetryCount;
    private String producerGroup;
    private String consumerGroup;
    private LocalDateTime createTime;
    private LocalDateTime lastActiveTime;
    private AtomicLong messageCount;
    private AtomicLong processedCount;
    private AtomicLong failedCount;
    private Integer consumerCount;
    private Integer partitionCount;
    private String owner;
    private String tags;
    
    // ==================== 构造函数 ====================
    public MessageQueue() {
        this.status = QueueStatus.ACTIVE;
        this.currentSize = 0L;
        this.maxSize = 100000L;
        this.maxPriority = 100L;
        this.persistent = true;
        this.maxRetryCount = 3;
        this.createTime = LocalDateTime.now();
        this.lastActiveTime = LocalDateTime.now();
        this.messageCount = new AtomicLong(0);
        this.processedCount = new AtomicLong(0);
        this.failedCount = new AtomicLong(0);
        this.consumerCount = 0;
        this.partitionCount = 1;
    }
    
    public MessageQueue(String queueName) {
        this();
        this.queueName = queueName;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 是否已满
     */
    public boolean isFull() {
        return currentSize >= maxSize;
    }
    
    /**
     * 增加消息计数
     */
    public void incrementMessageCount() {
        this.messageCount.incrementAndGet();
        this.currentSize++;
        this.lastActiveTime = LocalDateTime.now();
    }
    
    /**
     * 增加处理计数
     */
    public void incrementProcessedCount() {
        this.processedCount.incrementAndGet();
        this.currentSize--;
    }
    
    /**
     * 增加失败计数
     */
    public void incrementFailedCount() {
        this.failedCount.incrementAndGet();
    }
    
    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        long total = processedCount.get() + failedCount.get();
        return total == 0 ? 0.0 : (double) processedCount.get() / total * 100;
    }
    
    /**
     * 暂停队列
     */
    public void pause() {
        this.status = QueueStatus.PAUSED;
    }
    
    /**
     * 恢复队列
     */
    public void resume() {
        this.status = QueueStatus.ACTIVE;
    }
    
    /**
     * 销毁队列
     */
    public void destroy() {
        this.status = QueueStatus.DESTROYED;
    }
    
    /**
     * 是否活跃
     */
    public boolean isActive() {
        return status == QueueStatus.ACTIVE;
    }
    
    // ==================== Getter & Setter ====================
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }
    
    public Long getMaxSize() { return maxSize; }
    public void setMaxSize(Long maxSize) { this.maxSize = maxSize; }
    
    public Long getCurrentSize() { return currentSize; }
    public void setCurrentSize(Long currentSize) { this.currentSize = currentSize; }
    
    public Long getMaxPriority() { return maxPriority; }
    public void setMaxPriority(Long maxPriority) { this.maxPriority = maxPriority; }
    
    public Boolean getPersistent() { return persistent; }
    public void setPersistent(Boolean persistent) { this.persistent = persistent; }
    
    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
    
    public Integer getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(Integer maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    
    public String getProducerGroup() { return producerGroup; }
    public void setProducerGroup(String producerGroup) { this.producerGroup = producerGroup; }
    
    public String getConsumerGroup() { return consumerGroup; }
    public void setConsumerGroup(String consumerGroup) { this.consumerGroup = consumerGroup; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    public Long getMessageCount() { return messageCount.get(); }
    public Long getProcessedCount() { return processedCount.get(); }
    public Long getFailedCount() { return failedCount.get(); }
    
    public Integer getConsumerCount() { return consumerCount; }
    public void setConsumerCount(Integer consumerCount) { this.consumerCount = consumerCount; }
    
    public Integer getPartitionCount() { return partitionCount; }
    public void setPartitionCount(Integer partitionCount) { this.partitionCount = partitionCount; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    
    @Override
    public String toString() {
        return "MessageQueue{" +
                "queueName='" + queueName + '\'' +
                ", status=" + status +
                ", currentSize=" + currentSize +
                ", maxSize=" + maxSize +
                '}';
    }
}
