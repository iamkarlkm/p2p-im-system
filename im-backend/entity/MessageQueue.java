package com.im.entity;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消息队列实体类
 * 功能 #1: 消息队列核心系统
 */
public class MessageQueue {
    
    private String queueName;
    private String queueType;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;
    private long maxLength;
    private long maxSize;
    private long messageTTL;
    private String deadLetterExchange;
    private String deadLetterRoutingKey;
    private QueueStatus status;
    private AtomicLong messageCount;
    private AtomicLong consumerCount;
    private Instant createTime;
    private Instant lastActivityTime;
    private String owner;
    private int priority;
    
    public enum QueueStatus {
        ACTIVE, PAUSED, DELETING, ERROR
    }
    
    public MessageQueue() {
        this.status = QueueStatus.ACTIVE;
        this.messageCount = new AtomicLong(0);
        this.consumerCount = new AtomicLong(0);
        this.createTime = Instant.now();
        this.lastActivityTime = Instant.now();
        this.durable = true;
        this.priority = 5;
    }
    
    public MessageQueue(String queueName) {
        this();
        this.queueName = queueName;
    }
    
    // Getters and Setters
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }
    
    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }
    
    public boolean isExclusive() { return exclusive; }
    public void setExclusive(boolean exclusive) { this.exclusive = exclusive; }
    
    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
    
    public Map<String, Object> getArguments() { return arguments; }
    public void setArguments(Map<String, Object> arguments) { this.arguments = arguments; }
    
    public long getMaxLength() { return maxLength; }
    public void setMaxLength(long maxLength) { this.maxLength = maxLength; }
    
    public long getMaxSize() { return maxSize; }
    public void setMaxSize(long maxSize) { this.maxSize = maxSize; }
    
    public long getMessageTTL() { return messageTTL; }
    public void setMessageTTL(long messageTTL) { this.messageTTL = messageTTL; }
    
    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }
    
    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }
    
    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }
    
    public long getMessageCount() { return messageCount.get(); }
    public void setMessageCount(long count) { this.messageCount.set(count); }
    
    public long getConsumerCount() { return consumerCount.get(); }
    public void setConsumerCount(long count) { this.consumerCount.set(count); }
    
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    
    public Instant getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(Instant lastActivityTime) { this.lastActivityTime = lastActivityTime; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    /**
     * 增加消息计数
     */
    public long incrementMessageCount() {
        updateActivityTime();
        return messageCount.incrementAndGet();
    }
    
    /**
     * 减少消息计数
     */
    public long decrementMessageCount() {
        updateActivityTime();
        return messageCount.decrementAndGet();
    }
    
    /**
     * 增加消费者计数
     */
    public long incrementConsumerCount() {
        return consumerCount.incrementAndGet();
    }
    
    /**
     * 减少消费者计数
     */
    public long decrementConsumerCount() {
        return consumerCount.decrementAndGet();
    }
    
    /**
     * 更新最后活动时间
     */
    public void updateActivityTime() {
        this.lastActivityTime = Instant.now();
    }
    
    /**
     * 检查队列是否已满
     */
    public boolean isFull() {
        if (maxLength > 0 && messageCount.get() >= maxLength) {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "MessageQueue{" +
                "queueName='" + queueName + '\'' +
                ", status=" + status +
                ", messageCount=" + messageCount.get() +
                ", consumerCount=" + consumerCount.get() +
                '}';
    }
}
