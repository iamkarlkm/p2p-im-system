package com.im.service.impl;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.QueueMessage;
import com.im.entity.MessageQueue;
import com.im.entity.DeadLetterMessage;
import com.im.repository.QueueMessageMapper;
import com.im.repository.MessageQueueMapper;
import com.im.service.IMessageQueueService;
import com.im.service.DeadLetterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 消息队列服务实现类
 * 功能 #1: 消息队列核心系统 - 高性能消息生产/消费
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class MessageQueueServiceImpl implements IMessageQueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageQueueServiceImpl.class);
    
    @Autowired
    private QueueMessageMapper messageMapper;
    
    @Autowired
    private MessageQueueMapper queueMapper;
    
    @Autowired
    private DeadLetterHandler deadLetterHandler;
    
    // 内存队列缓存
    private final ConcurrentHashMap<String, BlockingQueue<QueueMessage>> memoryQueues = new ConcurrentHashMap<>();
    
    // 消费者组管理
    private final ConcurrentHashMap<String, Set<String>> consumerGroups = new ConcurrentHashMap<>();
    
    // 批量发送队列
    private final List<QueueMessage> batchBuffer = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService batchExecutor = Executors.newSingleThreadScheduledExecutor();
    
    public MessageQueueServiceImpl() {
        // 启动批量发送定时器
        batchExecutor.scheduleAtFixedRate(this::flushBatchBuffer, 100, 100, TimeUnit.MILLISECONDS);
    }
    
    // ==================== 队列管理 ====================
    
    @Override
    @Transactional
    public MessageQueue createQueue(String queueName, String topic) {
        if (queueMapper.selectByName(queueName) != null) {
            throw new IllegalArgumentException("Queue already exists: " + queueName);
        }
        
        MessageQueue queue = new MessageQueue();
        queue.setQueueName(queueName);
        queue.setTopic(topic);
        queue.setCreateTime(LocalDateTime.now());
        
        queueMapper.insert(queue);
        
        // 初始化内存队列
        memoryQueues.put(queueName, new PriorityBlockingQueue<>(1000, 
            Comparator.comparingLong(QueueMessage::getPriority).reversed()
                .thenComparing(QueueMessage::getCreateTime)));
        
        logger.info("Queue created: {} with topic: {}", queueName, topic);
        return queue;
    }
    
    @Override
    public Optional<MessageQueue> getQueue(String queueName) {
        return Optional.ofNullable(queueMapper.selectByName(queueName));
    }
    
    @Override
    @Transactional
    public boolean deleteQueue(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue == null) return false;
        
        if (queue.getCurrentSize() > 0) {
            throw new IllegalStateException("Cannot delete non-empty queue: " + queueName);
        }
        
        queueMapper.deleteByName(queueName);
        memoryQueues.remove(queueName);
        logger.info("Queue deleted: {}", queueName);
        return true;
    }
    
    @Override
    @Transactional
    public boolean pauseQueue(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue == null) return false;
        
        queue.pause();
        queueMapper.update(queue);
        logger.info("Queue paused: {}", queueName);
        return true;
    }
    
    @Override
    @Transactional
    public boolean resumeQueue(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue == null) return false;
        
        queue.resume();
        queueMapper.update(queue);
        logger.info("Queue resumed: {}", queueName);
        return true;
    }
    
    @Override
    public List<MessageQueue> listActiveQueues() {
        return queueMapper.selectAllActive();
    }
    
    // ==================== 消息发送 ====================
    
    @Override
    @Transactional
    public MessageReceipt sendMessage(SendMessageRequest request) {
        String queueName = request.getQueueName();
        MessageQueue queue = queueMapper.selectByName(queueName);
        
        if (queue == null) {
            return MessageReceipt.failed(null, "Queue not found: " + queueName);
        }
        
        if (!queue.isActive()) {
            return MessageReceipt.failed(null, "Queue is not active: " + queueName);
        }
        
        if (queue.isFull()) {
            return MessageReceipt.failed(null, "Queue is full: " + queueName);
        }
        
        // 去重检查
        if (request.getHeaders() != null && request.getHeaders().containsKey("traceId")) {
            String traceId = request.getHeaders().get("traceId");
            if (isDuplicate(traceId, queueName)) {
                logger.warn("Duplicate message detected: traceId={}", traceId);
                return MessageReceipt.accepted("DUPLICATE_" + traceId);
            }
        }
        
        QueueMessage message = new QueueMessage();
        message.setQueueName(queueName);
        message.setTopic(request.getTopic() != null ? request.getTopic() : queue.getTopic());
        message.setMessageType(request.getMessageType());
        message.setPayload(request.getPayload());
        message.setHeaders(request.getHeaders());
        message.setPriority(request.getPriority() != null ? request.getPriority() : 0L);
        message.setTtl(request.getTtl());
        message.setProducerId(request.getProducerId());
        message.setPersistent(request.getPersistent() != null ? request.getPersistent() : true);
        message.setMaxRetryCount(request.getMaxRetryCount() != null ? request.getMaxRetryCount() : 3);
        message.setCreateTime(LocalDateTime.now());
        
        // 持久化到数据库
        if (message.getPersistent()) {
            messageMapper.insert(message);
        }
        
        // 放入内存队列
        BlockingQueue<QueueMessage> memQueue = memoryQueues.computeIfAbsent(queueName, 
            k -> new LinkedBlockingQueue<>());
        memQueue.offer(message);
        
        // 更新队列统计
        queue.incrementMessageCount();
        queueMapper.incrementSize(queueName);
        
        logger.debug("Message sent: {} to queue: {}", message.getMessageId(), queueName);
        return MessageReceipt.accepted(message.getMessageId());
    }
    
    @Override
    public List<MessageReceipt> sendMessages(List<SendMessageRequest> requests) {
        return requests.stream()
            .map(this::sendMessage)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<MessageReceipt> broadcastMessage(String topic, String payload) {
        List<MessageQueue> queues = queueMapper.selectByTopic(topic);
        List<MessageReceipt> receipts = new ArrayList<>();
        
        for (MessageQueue queue : queues) {
            SendMessageRequest request = new SendMessageRequest();
            request.setQueueName(queue.getQueueName());
            request.setTopic(topic);
            request.setPayload(payload);
            receipts.add(sendMessage(request));
        }
        
        return receipts;
    }
    
    // ==================== 消息消费 ====================
    
    @Override
    @Transactional
    public Optional<QueueMessage> consumeMessage(String queueName, String consumerId) {
        // 1. 从内存队列获取
        BlockingQueue<QueueMessage> memQueue = memoryQueues.get(queueName);
        if (memQueue != null) {
            QueueMessage message = memQueue.poll();
            if (message != null) {
                return processMessage(message, consumerId);
            }
        }
        
        // 2. 从数据库获取
        List<QueueMessage> pending = messageMapper.selectPendingByQueue(queueName, 1);
        if (!pending.isEmpty()) {
            QueueMessage message = pending.get(0);
            int acquired = messageMapper.acquireMessage(message.getMessageId(), consumerId);
            if (acquired > 0) {
                message.setStatus(QueueMessage.MessageStatus.PROCESSING);
                message.setConsumerId(consumerId);
                message.setProcessTime(LocalDateTime.now());
                return Optional.of(message);
            }
        }
        
        // 3. 获取重试消息
        List<QueueMessage> retryable = getRetryableMessages(queueName, 1);
        if (!retryable.isEmpty()) {
            return Optional.of(retryable.get(0));
        }
        
        return Optional.empty();
    }
    
    @Override
    @Transactional
    public List<QueueMessage> consumeMessages(String queueName, String consumerId, int batchSize) {
        List<QueueMessage> messages = new ArrayList<>();
        
        for (int i = 0; i < batchSize; i++) {
            Optional<QueueMessage> msg = consumeMessage(queueName, consumerId);
            if (msg.isPresent()) {
                messages.add(msg.get());
            } else {
                break;
            }
        }
        
        return messages;
    }
    
    private Optional<QueueMessage> processMessage(QueueMessage message, String consumerId) {
        message.setStatus(QueueMessage.MessageStatus.PROCESSING);
        message.setConsumerId(consumerId);
        message.setProcessTime(LocalDateTime.now());
        
        if (message.getPersistent()) {
            messageMapper.updateStatus(message);
        }
        
        queueMapper.decrementSize(message.getQueueName());
        return Optional.of(message);
    }
    
    // ==================== 消息确认与重试 ====================
    
    @Override
    @Transactional
    public boolean acknowledgeMessage(String messageId) {
        QueueMessage message = messageMapper.selectById(messageId);
        if (message == null) return false;
        
        message.markSuccess();
        messageMapper.updateStatus(message);
        
        logger.debug("Message acknowledged: {}", messageId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean retryMessage(String messageId, String error) {
        QueueMessage message = messageMapper.selectById(messageId);
        if (message == null) return false;
        
        if (message.shouldRetry()) {
            message.incrementRetry();
            message.setErrorMessage(error);
            messageMapper.updateStatus(message);
            
            // 重新放入队列
            BlockingQueue<QueueMessage> memQueue = memoryQueues.get(message.getQueueName());
            if (memQueue != null) {
                memQueue.offer(message);
            }
            
            logger.debug("Message scheduled for retry: {}, count: {}", messageId, message.getRetryCount());
            return true;
        } else {
            // 超过重试次数，进入死信队列
            message.markFailed(error);
            messageMapper.updateStatus(message);
            sendToDeadLetter(message, "Retry exhausted");
            return false;
        }
    }
    
    // ==================== 消息查询 ====================
    
    @Override
    public Optional<QueueMessage> getMessage(String messageId) {
        return Optional.ofNullable(messageMapper.selectById(messageId));
    }
    
    @Override
    public long getPendingCount(String queueName) {
        return messageMapper.countByStatus(queueName, "PENDING");
    }
    
    @Override
    public boolean isDuplicate(String traceId, String queueName) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return messageMapper.countDuplicate(traceId, queueName, fiveMinutesAgo) > 0;
    }
    
    // ==================== 队列统计 ====================
    
    @Override
    public QueueStatsResponse getQueueStats(String queueName) {
        MessageQueue queue = queueMapper.selectByName(queueName);
        if (queue == null) return null;
        
        QueueStatsResponse stats = new QueueStatsResponse();
        stats.setQueueName(queueName);
        stats.setTopic(queue.getTopic());
        stats.setStatus(queue.getStatus().name());
        stats.setCurrentSize(queue.getCurrentSize());
        stats.setMaxSize(queue.getMaxSize());
        stats.setMessageCount(queue.getMessageCount());
        stats.setProcessedCount(queue.getProcessedCount());
        stats.setFailedCount(queue.getFailedCount());
        stats.setSuccessRate(queue.getSuccessRate());
        stats.setConsumerCount(queue.getConsumerCount());
        stats.setPartitionCount(queue.getPartitionCount());
        stats.setCreateTime(queue.getCreateTime());
        stats.setLastActiveTime(queue.getLastActiveTime());
        
        return stats;
    }
    
    @Override
    public List<QueueStatsResponse> getAllQueueStats() {
        return listActiveQueues().stream()
            .map(q -> getQueueStats(q.getQueueName()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    // ==================== 重试与死信 ====================
    
    @Override
    public List<QueueMessage> getRetryableMessages(String queueName, int limit) {
        return messageMapper.selectRetryableMessages(queueName, LocalDateTime.now(), limit);
    }
    
    @Override
    @Transactional
    public void sendToDeadLetter(QueueMessage message, String reason) {
        message.markDeadLetter(reason);
        messageMapper.updateStatus(message);
        
        DeadLetterMessage deadLetter = new DeadLetterMessage(message, 
            DeadLetterMessage.DeadLetterReason.RETRY_EXHAUSTED);
        deadLetter.setErrorDetail(reason);
        
        deadLetterHandler.handle(deadLetter);
        
        logger.warn("Message sent to dead letter queue: {}, reason: {}", 
            message.getMessageId(), reason);
    }
    
    @Override
    @Transactional
    public boolean retryFromDeadLetter(String deadLetterId) {
        return deadLetterHandler.retry(deadLetterId);
    }
    
    // ==================== 内部方法 ====================
    
    private void flushBatchBuffer() {
        if (batchBuffer.isEmpty()) return;
        
        List<QueueMessage> batch = new ArrayList<>(batchBuffer);
        batchBuffer.clear();
        
        try {
            messageMapper.batchInsert(batch);
            logger.debug("Batch flushed: {} messages", batch.size());
        } catch (Exception e) {
            logger.error("Batch insert failed", e);
            // 回退到单条插入
            batch.forEach(msg -> {
                try {
                    messageMapper.insert(msg);
                } catch (Exception ex) {
                    logger.error("Single insert failed: {}", msg.getMessageId(), ex);
                }
            });
        }
    }
}
