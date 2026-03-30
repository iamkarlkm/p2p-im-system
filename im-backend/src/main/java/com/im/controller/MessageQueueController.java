package com.im.controller;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.DeadLetterMessage;
import com.im.entity.MessageQueue;
import com.im.entity.QueueMessage;
import com.im.service.IMessageQueueService;
import com.im.service.DeadLetterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 消息队列控制器
 * 功能 #1: 消息队列核心系统 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/queue")
public class MessageQueueController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageQueueController.class);
    
    @Autowired
    private IMessageQueueService queueService;
    
    @Autowired
    private DeadLetterHandler deadLetterHandler;
    
    // ==================== 队列管理 ====================
    
    /**
     * 创建队列
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createQueue(
            @RequestParam String queueName,
            @RequestParam(required = false) String topic) {
        
        try {
            MessageQueue queue = queueService.createQueue(queueName, topic != null ? topic : "default");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("queue", queue);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * 获取队列信息
     */
    @GetMapping("/info/{queueName}")
    public ResponseEntity<Map<String, Object>> getQueueInfo(@PathVariable String queueName) {
        Optional<MessageQueue> queue = queueService.getQueue(queueName);
        
        Map<String, Object> response = new HashMap<>();
        if (queue.isPresent()) {
            response.put("success", true);
            response.put("queue", queue.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("error", "Queue not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 列出所有队列
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listQueues() {
        List<MessageQueue> queues = queueService.listActiveQueues();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("queues", queues);
        response.put("count", queues.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除队列
     */
    @PostMapping("/delete/{queueName}")
    public ResponseEntity<Map<String, Object>> deleteQueue(@PathVariable String queueName) {
        boolean deleted = queueService.deleteQueue(queueName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", deleted);
        response.put("queueName", queueName);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 暂停队列
     */
    @PostMapping("/pause/{queueName}")
    public ResponseEntity<Map<String, Object>> pauseQueue(@PathVariable String queueName) {
        boolean paused = queueService.pauseQueue(queueName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", paused);
        response.put("queueName", queueName);
        response.put("action", "paused");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 恢复队列
     */
    @PostMapping("/resume/{queueName}")
    public ResponseEntity<Map<String, Object>> resumeQueue(@PathVariable String queueName) {
        boolean resumed = queueService.resumeQueue(queueName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", resumed);
        response.put("queueName", queueName);
        response.put("action", "resumed");
        return ResponseEntity.ok(response);
    }
    
    // ==================== 消息发送 ====================
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResponseEntity<MessageReceipt> sendMessage(@RequestBody SendMessageRequest request) {
        MessageReceipt receipt = queueService.sendMessage(request);
        
        if (receipt.isSuccess()) {
            return ResponseEntity.ok(receipt);
        } else {
            return ResponseEntity.badRequest().body(receipt);
        }
    }
    
    /**
     * 批量发送
     */
    @PostMapping("/send/batch")
    public ResponseEntity<Map<String, Object>> sendBatch(@RequestBody List<SendMessageRequest> requests) {
        List<MessageReceipt> receipts = queueService.sendMessages(requests);
        
        long successCount = receipts.stream().filter(MessageReceipt::isSuccess).count();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("receipts", receipts);
        response.put("successCount", successCount);
        response.put("failCount", receipts.size() - successCount);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 广播消息
     */
    @PostMapping("/broadcast/{topic}")
    public ResponseEntity<Map<String, Object>> broadcast(
            @PathVariable String topic,
            @RequestBody String payload) {
        
        List<MessageReceipt> receipts = queueService.broadcastMessage(topic, payload);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("receipts", receipts);
        response.put("targetCount", receipts.size());
        return ResponseEntity.ok(response);
    }
    
    // ==================== 消息消费 ====================
    
    /**
     * 消费消息
     */
    @PostMapping("/consume/{queueName}")
    public ResponseEntity<Map<String, Object>> consume(
            @PathVariable String queueName,
            @RequestParam String consumerId,
            @RequestParam(defaultValue = "1") int batchSize) {
        
        List<QueueMessage> messages = queueService.consumeMessages(queueName, consumerId, batchSize);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messages", messages);
        response.put("count", messages.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 确认消息
     */
    @PostMapping("/ack/{messageId}")
    public ResponseEntity<Map<String, Object>> acknowledge(@PathVariable String messageId) {
        boolean acked = queueService.acknowledgeMessage(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", acked);
        response.put("messageId", messageId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量确认
     */
    @PostMapping("/ack/batch")
    public ResponseEntity<Map<String, Object>> acknowledgeBatch(@RequestBody List<String> messageIds) {
        int successCount = 0;
        for (String messageId : messageIds) {
            if (queueService.acknowledgeMessage(messageId)) {
                successCount++;
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("successCount", successCount);
        response.put("failCount", messageIds.size() - successCount);
        return ResponseEntity.ok(response);
    }
    
    // ==================== 队列统计 ====================
    
    /**
     * 队列统计
     */
    @GetMapping("/stats/{queueName}")
    public ResponseEntity<QueueStatsResponse> getStats(@PathVariable String queueName) {
        QueueStatsResponse stats = queueService.getQueueStats(queueName);
        
        if (stats != null) {
            return ResponseEntity.ok(stats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 所有队列统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAllStats() {
        List<QueueStatsResponse> stats = queueService.getAllQueueStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", stats);
        response.put("queueCount", stats.size());
        return ResponseEntity.ok(response);
    }
    
    // ==================== 死信管理 ====================
    
    /**
     * 获取死信列表
     */
    @GetMapping("/dead-letter")
    public ResponseEntity<Map<String, Object>> getDeadLetters(
            @RequestParam(required = false) String queueName) {
        
        List<DeadLetterMessage> deadLetters;
        if (queueName != null) {
            deadLetters = deadLetterHandler.getDeadLettersByQueue(queueName);
        } else {
            deadLetters = deadLetterHandler.getUnresolvedDeadLetters();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deadLetters", deadLetters);
        response.put("count", deadLetters.size());
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重试死信
     */
    @PostMapping("/dead-letter/{deadLetterId}/retry")
    public ResponseEntity<Map<String, Object>> retryDeadLetter(@PathVariable String deadLetterId) {
        boolean success = queueService.retryFromDeadLetter(deadLetterId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("deadLetterId", deadLetterId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 死信统计
     */
    @GetMapping("/dead-letter/stats")
    public ResponseEntity<Map<String, Object>> getDeadLetterStats() {
        Map<String, Object> stats = deadLetterHandler.getStats();
        return ResponseEntity.ok(stats);
    }
}
