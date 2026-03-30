package com.im.controller;

import com.im.dto.MessageReceipt;
import com.im.dto.QueueStatsResponse;
import com.im.dto.SendMessageRequest;
import com.im.entity.QueueMessage;
import com.im.service.IMessageQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 消息队列控制器
 * 功能 #1: 消息队列核心系统
 */
@RestController
@RequestMapping("/api/v1/queue")
public class MessageQueueController {
    
    @Autowired
    private IMessageQueueService messageQueueService;
    
    @PostMapping("/send")
    public MessageReceipt sendMessage(@RequestBody SendMessageRequest request) {
        return messageQueueService.sendMessage(request);
    }
    
    @PostMapping("/send/batch")
    public List<MessageReceipt> sendBatchMessages(@RequestBody List<SendMessageRequest> requests) {
        return messageQueueService.sendBatchMessages(requests);
    }
    
    @GetMapping("/consume")
    public QueueMessage consumeMessage(
            @RequestParam String queueName,
            @RequestParam(defaultValue = "5000") long timeout) {
        return messageQueueService.consumeMessage(queueName, timeout);
    }
    
    @PostMapping("/ack")
    public boolean acknowledgeMessage(@RequestBody Map<String, String> params) {
        return messageQueueService.acknowledgeMessage(params.get("queueName"), params.get("messageId"));
    }
    
    @PostMapping("/nack")
    public boolean negativeAcknowledge(@RequestBody Map<String, Object> params) {
        String queueName = (String) params.get("queueName");
        String messageId = (String) params.get("messageId");
        boolean requeue = (Boolean) params.getOrDefault("requeue", true);
        return messageQueueService.negativeAcknowledge(queueName, messageId, requeue);
    }
    
    @PostMapping("/create")
    public boolean createQueue(@RequestBody Map<String, Object> params) {
        String queueName = (String) params.get("queueName");
        boolean durable = (Boolean) params.getOrDefault("durable", true);
        boolean exclusive = (Boolean) params.getOrDefault("exclusive", false);
        return messageQueueService.createQueue(queueName, durable, exclusive);
    }
    
    @DeleteMapping("/delete/{queueName}")
    public boolean deleteQueue(@PathVariable String queueName) {
        return messageQueueService.deleteQueue(queueName);
    }
    
    @GetMapping("/stats/{queueName}")
    public QueueStatsResponse getQueueStats(@PathVariable String queueName) {
        return messageQueueService.getQueueStats(queueName);
    }
    
    @PostMapping("/pause/{queueName}")
    public boolean pauseQueue(@PathVariable String queueName) {
        return messageQueueService.pauseQueue(queueName);
    }
    
    @PostMapping("/resume/{queueName}")
    public boolean resumeQueue(@PathVariable String queueName) {
        return messageQueueService.resumeQueue(queueName);
    }
    
    @PostMapping("/purge/{queueName}")
    public boolean purgeQueue(@PathVariable String queueName) {
        return messageQueueService.purgeQueue(queueName);
    }
    
    @PostMapping("/dead-letter")
    public boolean moveToDeadLetter(@RequestBody Map<String, String> params) {
        return messageQueueService.moveToDeadLetter(
            params.get("queueName"),
            params.get("messageId"),
            params.get("reason")
        );
    }
}
