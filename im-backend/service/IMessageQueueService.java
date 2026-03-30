package com.im.service;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.QueueMessage;
import java.util.List;
import java.util.function.Consumer;

/**
 * 消息队列服务接口
 * 功能 #1: 消息队列核心系统
 */
public interface IMessageQueueService {
    
    /**
     * 发送消息到队列
     */
    MessageReceipt sendMessage(SendMessageRequest request);
    
    /**
     * 批量发送消息
     */
    List<MessageReceipt> sendBatchMessages(List<SendMessageRequest> requests);
    
    /**
     * 消费消息
     */
    QueueMessage consumeMessage(String queueName, long timeout);
    
    /**
     * 批量消费消息
     */
    List<QueueMessage> consumeBatchMessages(String queueName, int batchSize, long timeout);
    
    /**
     * 确认消息消费成功
     */
    boolean acknowledgeMessage(String queueName, String messageId);
    
    /**
     * 否定确认，消息重新入队
     */
    boolean negativeAcknowledge(String queueName, String messageId, boolean requeue);
    
    /**
     * 创建队列
     */
    boolean createQueue(String queueName, boolean durable, boolean exclusive);
    
    /**
     * 删除队列
     */
    boolean deleteQueue(String queueName);
    
    /**
     * 获取队列统计信息
     */
    QueueStatsResponse getQueueStats(String queueName);
    
    /**
     * 订阅队列消息（推模式）
     */
    void subscribe(String queueName, Consumer<QueueMessage> consumer);
    
    /**
     * 取消订阅
     */
    void unsubscribe(String queueName, String consumerId);
    
    /**
     * 暂停队列
     */
    boolean pauseQueue(String queueName);
    
    /**
     * 恢复队列
     */
    boolean resumeQueue(String queueName);
    
    /**
     * 清空队列
     */
    boolean purgeQueue(String queueName);
    
    /**
     * 移动消息到死信队列
     */
    boolean moveToDeadLetter(String queueName, String messageId, String reason);
}
