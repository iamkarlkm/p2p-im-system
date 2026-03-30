package com.im.dto;

import java.time.Instant;
import java.util.Map;

/**
 * 队列统计响应DTO
 * 功能 #1: 消息队列核心系统
 */
public class QueueStatsResponse {
    
    private String queueName;
    private long messageCount;
    private long consumerCount;
    private long messageRate;
    private long consumerRate;
    private long memoryUsage;
    private long diskUsage;
    private double cpuUsage;
    private Instant lastActivityTime;
    private String queueStatus;
    private Map<String, Long> messageTypeDistribution;
    private long deadLetterCount;
    private long avgDeliveryTime;
    private long maxDeliveryTime;
    private long failedMessageCount;
    private long expiredMessageCount;
    private long throughput;
    private double availability;
    
    // Getters and Setters
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public long getMessageCount() { return messageCount; }
    public void setMessageCount(long messageCount) { this.messageCount = messageCount; }
    
    public long getConsumerCount() { return consumerCount; }
    public void setConsumerCount(long consumerCount) { this.consumerCount = consumerCount; }
    
    public long getMessageRate() { return messageRate; }
    public void setMessageRate(long messageRate) { this.messageRate = messageRate; }
    
    public long getConsumerRate() { return consumerRate; }
    public void setConsumerRate(long consumerRate) { this.consumerRate = consumerRate; }
    
    public long getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(long memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public long getDiskUsage() { return diskUsage; }
    public void setDiskUsage(long diskUsage) { this.diskUsage = diskUsage; }
    
    public double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public Instant getLastActivityTime() { return lastActivityTime; }
    public void setLastActivityTime(Instant lastActivityTime) { this.lastActivityTime = lastActivityTime; }
    
    public String getQueueStatus() { return queueStatus; }
    public void setQueueStatus(String queueStatus) { this.queueStatus = queueStatus; }
    
    public Map<String, Long> getMessageTypeDistribution() { return messageTypeDistribution; }
    public void setMessageTypeDistribution(Map<String, Long> messageTypeDistribution) { this.messageTypeDistribution = messageTypeDistribution; }
    
    public long getDeadLetterCount() { return deadLetterCount; }
    public void setDeadLetterCount(long deadLetterCount) { this.deadLetterCount = deadLetterCount; }
    
    public long getAvgDeliveryTime() { return avgDeliveryTime; }
    public void setAvgDeliveryTime(long avgDeliveryTime) { this.avgDeliveryTime = avgDeliveryTime; }
    
    public long getMaxDeliveryTime() { return maxDeliveryTime; }
    public void setMaxDeliveryTime(long maxDeliveryTime) { this.maxDeliveryTime = maxDeliveryTime; }
    
    public long getFailedMessageCount() { return failedMessageCount; }
    public void setFailedMessageCount(long failedMessageCount) { this.failedMessageCount = failedMessageCount; }
    
    public long getExpiredMessageCount() { return expiredMessageCount; }
    public void setExpiredMessageCount(long expiredMessageCount) { this.expiredMessageCount = expiredMessageCount; }
    
    public long getThroughput() { return throughput; }
    public void setThroughput(long throughput) { this.throughput = throughput; }
    
    public double getAvailability() { return availability; }
    public void setAvailability(double availability) { this.availability = availability; }
    
    @Override
    public String toString() {
        return "QueueStatsResponse{" +
                "queueName='" + queueName + '\'' +
                ", messageCount=" + messageCount +
                ", consumerCount=" + consumerCount +
                ", queueStatus='" + queueStatus + '\'' +
                '}';
    }
}
