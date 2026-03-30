package com.im.service;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.DeadLetterMessage;
import com.im.entity.MessageQueue;
import com.im.entity.QueueMessage;

import java.util.List;
import java.util.Optional;

/**
 * 消息队列服务接口
 * 功能 #1: 消息队列核心系统 - 服务接口定义
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IMessageQueueService {
    
    // ==================== 队列管理 ====================
    
    /**
     * 创建队列
     */
    MessageQueue createQueue(String queueName, String topic);
    
    /**
     * 获取队列
     */
    Optional<MessageQueue> getQueue(String queueName);
    
    /**
     * 删除队列
     */
    boolean deleteQueue(String queueName);
    
    /**
     * 暂停队列
     */
    boolean pauseQueue(String queueName);
    
    /**
     * 恢复队列
     */
    boolean resumeQueue(String queueName);
    
    /**
     * 列出活跃队列
     */
    List<MessageQueue> listActiveQueues();
    
    // ==================== 消息发送 ====================
    
    /**
     * 发送消息
     */
    MessageReceipt sendMessage(SendMessageRequest request);
    
    /**
     * 批量发送
     */
    List<MessageReceipt> sendMessages(List<SendMessageRequest> requests);
    
    /**
     * 广播消息
     */
    List<MessageReceipt> broadcastMessage(String topic, String payload);
    
    // ==================== 消息消费 ====================
    
    /**
     * 消费单条消息
     */
    Optional<QueueMessage> consumeMessage(String queueName, String consumerId);
    
    /**
     * 批量消费
     */
    List<QueueMessage> consumeMessages(String queueName, String consumerId, int batchSize);
    
    /**
     * 确认消息
     */
    boolean acknowledgeMessage(String messageId);
    
    /**
     * 重试消息
     */
    boolean retryMessage(String messageId, String error);
    
    // ==================== 消息查询 ====================
    
    /**
     * 获取消息
     */
    Optional<QueueMessage> getMessage(String messageId);
    
    /**
     * 获取待处理数量
     */
    long getPendingCount(String queueName);
    
    /**
     * 检查重复
     */
    boolean isDuplicate(String traceId, String queueName);
    
    // ==================== 队列统计 ====================
    
    /**
     * 获取队列统计
     */
    QueueStatsResponse getQueueStats(String queueName);
    
    /**
     * 获取所有队列统计
     */
    List<QueueStatsResponse> getAllQueueStats();
    
    // ==================== 重试与死信 ====================
    
    /**
     * 获取可重试消息
     */
    List<QueueMessage> getRetryableMessages(String queueName, int limit);
    
    /**
     * 发送到死信队列
     */
    void sendToDeadLetter(QueueMessage message, String reason);
    
    /**
     * 从死信队列重试
     */
    boolean retryFromDeadLetter(String deadLetterId);
}
