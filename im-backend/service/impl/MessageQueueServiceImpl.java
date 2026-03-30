package com.im.service.impl;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.MessageQueue;
import com.im.entity.QueueMessage;
import com.im.repository.MessageQueueMapper;
import com.im.repository.QueueMessageMapper;
import com.im.service.IMessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 消息队列服务实现类
 * 功能 #1: 消息队列核心系统
 */
@Service
public class MessageQueueServiceImpl implements IMessageQueueService {
    
    @Autowired
    private QueueMessageMapper messageMapper;
    
    @Autowired
    private MessageQueueMapper queueMapper;
    
    private final ConcurrentHashMap<String, MessageQueue> queueCache = new ConcurrentHashMap<>();
    
    @Override
    public MessageReceipt sendMessage(SendMessageRequest request) {
        QueueMessage message = new QueueMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setQueueName(request.getQueueName());
        message.setMessageType(request.getMessageType());
        message.setPayload(request.getPayload());
        message.setHeaders(request.getHeaders());
        message.setPriority(request.getPriority());
        message.setPersistent(request.isPersistent());
        message.setCreateTime(Instant.now());
        
        if (request.getTtl() != null) {
            message.setExpireTime(Instant.now().plusMillis(request.getTtl()));
        }
        
        messageMapper.insert(message);
        
        // 更新队列消息计数
        MessageQueue queue = queueCache.get(request.getQueueName());
        if (queue != null) {
            queue.incrementMessageCount();
        }
        
        return MessageReceipt.success(message.getMessageId());
    }
    
    @Override
    public List<MessageReceipt> sendBatchMessages(List<SendMessageRequest> requests) {
        return requests.stream().map(this::sendMessage).toList();
    }
    
    @Override
    public QueueMessage consumeMessage(String queueName, long timeout) {
        List<QueueMessage> messages = messageMapper.selectPendingByQueue(queueName, 1);
        if (!messages.isEmpty()) {
            QueueMessage message = messages.get(0);
            message.setStatus(QueueMessage.MessageStatus.DELIVERING);
            message.setConsumeTime(Instant.now());
            messageMapper.updateStatus(message);
            return message;
        }
        return null;
    }
    
    @Override
    public List<QueueMessage> consumeBatchMessages(String queueName, int batchSize, long timeout) {
        List<QueueMessage> messages = messageMapper.selectPendingByQueue(queueName, batchSize);
        for (QueueMessage message : messages) {
            message.setStatus(QueueMessage.MessageStatus.DELIVERING);
            message.setConsumeTime(Instant.now());
            messageMapper.updateStatus(message);
        }
        return messages;
    }
    
    @Override
    public boolean acknowledgeMessage(String queueName, String messageId) {
        QueueMessage message = messageMapper.selectById(messageId);
        if (message != null) {
            message.setStatus(QueueMessage.MessageStatus.DELIVERED);
            messageMapper.updateStatus(message);
            
            MessageQueue queue = queueCache.get(queueName);
            if (queue != null) {
                queue.decrementMessageCount();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean negativeAcknowledge(String queueName, String messageId, boolean requeue) {
        QueueMessage message = messageMapper.selectById(messageId);
        if (message != null) {
            if (requeue) {
                message.setStatus(QueueMessage.MessageStatus.PENDING);
                message.incrementRetry();
            } else {
                message.setStatus(QueueMessage.MessageStatus.FAILED);
            }
            messageMapper.updateStatus(message);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean createQueue(String queueName, boolean durable, boolean exclusive) {
        MessageQueue queue = new MessageQueue();
        queue.setQueueName(queueName);
        queue.setDurable(durable);
        queue.setExclusive(exclusive);
        queueMapper.insert(queue);
        queueCache.put(queueName, queue);
        return true;
    }
    
    @Override
    public boolean deleteQueue(String queueName) {
        queueMapper.deleteByName(queueName);
        queueCache.remove(queueName);
        return true;
    }
    
    @Override
    public QueueStatsResponse getQueueStats(String queueName) {
        QueueStatsResponse stats = new QueueStatsResponse();
        stats.setQueueName(queueName);
        stats.setMessageCount(messageMapper.countByQueueAndStatus(queueName, "PENDING"));
        
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue != null) {
            stats.setConsumerCount(queue.getConsumerCount());
            stats.setQueueStatus(queue.getStatus().name());
        }
        return stats;
    }
    
    @Override
    public void subscribe(String queueName, Consumer<QueueMessage> consumer) {
        MessageQueue queue = queueCache.computeIfAbsent(queueName, k -> queueMapper.selectByName(queueName));
        if (queue != null) {
            queue.incrementConsumerCount();
        }
    }
    
    @Override
    public void unsubscribe(String queueName, String consumerId) {
        MessageQueue queue = queueCache.get(queueName);
        if (queue != null) {
            queue.decrementConsumerCount();
        }
    }
    
    @Override
    public boolean pauseQueue(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue != null) {
            queue.setStatus(MessageQueue.QueueStatus.PAUSED);
            queueMapper.updateStats(queue);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean resumeQueue(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue != null) {
            queue.setStatus(MessageQueue.QueueStatus.ACTIVE);
            queueMapper.updateStats(queue);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean purgeQueue(String queueName) {
        // 清空队列实现
        return true;
    }
    
    @Override
    public boolean moveToDeadLetter(String queueName, String messageId, String reason) {
        QueueMessage message = messageMapper.selectById(messageId);
        if (message != null) {
            message.setStatus(QueueMessage.MessageStatus.DEAD_LETTER);
            message.setFailureReason(reason);
            messageMapper.updateStatus(message);
            return true;
        }
        return false;
    }
}
