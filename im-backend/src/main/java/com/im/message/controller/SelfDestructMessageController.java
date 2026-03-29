package com.im.message.controller;

import com.im.message.dto.SelfDestructMessageDTO;
import com.im.message.service.SelfDestructMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阅后即焚消息控制器
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/self-destruct-messages")
public class SelfDestructMessageController {

    private static final Logger logger = LoggerFactory.getLogger(SelfDestructMessageController.class);

    private final SelfDestructMessageService messageService;

    @Autowired
    public SelfDestructMessageController(SelfDestructMessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 创建阅后即焚消息
     */
    @PostMapping
    public ResponseEntity<SelfDestructMessageDTO> createMessage(
            @RequestAttribute("userId") String userId,
            @RequestBody SelfDestructMessageDTO.CreateRequest request) {
        logger.info("POST /api/self-destruct-messages - user: {}", userId);
        SelfDestructMessageDTO message = messageService.createMessage(userId, request);
        return ResponseEntity.ok(message);
    }

    /**
     * 阅读消息
     */
    @PostMapping("/{messageId}/read")
    public ResponseEntity<SelfDestructMessageDTO.ReadResponse> readMessage(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId) {
        logger.info("POST /api/self-destruct-messages/{}/read - user: {}", messageId, userId);
        SelfDestructMessageDTO.ReadResponse response = messageService.readMessage(messageId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<SelfDestructMessageDTO> getMessage(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId) {
        logger.info("GET /api/self-destruct-messages/{} - user: {}", messageId, userId);
        
        // 先尝试作为发送者获取
        try {
            SelfDestructMessageDTO message = messageService.getMessageForSender(messageId, userId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            // 尝试作为接收者获取
            SelfDestructMessageDTO message = messageService.getMessageForReceiver(messageId, userId);
            return ResponseEntity.ok(message);
        }
    }

    /**
     * 获取会话中的消息列表
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<SelfDestructMessageDTO>> getMessagesByConversation(
            @RequestAttribute("userId") String userId,
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("GET /api/self-destruct-messages/conversation/{} - user: {}", conversationId, userId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SelfDestructMessageDTO> result = messageService.getMessagesByConversation(conversationId, userId, pageable);
        
        return ResponseEntity.ok(result.getContent());
    }

    /**
     * 获取发送的消息
     */
    @GetMapping("/sent")
    public ResponseEntity<List<SelfDestructMessageDTO>> getSentMessages(
            @RequestAttribute("userId") String userId) {
        logger.info("GET /api/self-destruct-messages/sent - user: {}", userId);
        List<SelfDestructMessageDTO> messages = messageService.getSentMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取接收的消息
     */
    @GetMapping("/received")
    public ResponseEntity<List<SelfDestructMessageDTO>> getReceivedMessages(
            @RequestAttribute("userId") String userId) {
        logger.info("GET /api/self-destruct-messages/received - user: {}", userId);
        List<SelfDestructMessageDTO> messages = messageService.getReceivedMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestAttribute("userId") String userId) {
        logger.info("GET /api/self-destruct-messages/unread/count - user: {}", userId);
        Long count = messageService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取会话未读数量
     */
    @GetMapping("/conversation/{conversationId}/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCountByConversation(
            @RequestAttribute("userId") String userId,
            @PathVariable String conversationId) {
        logger.info("GET /api/self-destruct-messages/conversation/{}/unread/count - user: {}", conversationId, userId);
        Long count = messageService.getUnreadCountByConversation(conversationId, userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId) {
        logger.info("DELETE /api/self-destruct-messages/{} - user: {}", messageId, userId);
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 销毁消息
     */
    @PostMapping("/{messageId}/destroy")
    public ResponseEntity<Void> destroyMessage(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId) {
        logger.info("POST /api/self-destruct-messages/{}/destroy - user: {}", messageId, userId);
        messageService.destroyMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 检测截图
     */
    @PostMapping("/{messageId}/screenshot-detect")
    public ResponseEntity<SelfDestructMessageDTO.ScreenshotDetectResponse> detectScreenshot(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId,
            @RequestBody SelfDestructMessageDTO.ScreenshotDetectRequest request) {
        logger.info("POST /api/self-destruct-messages/{}/screenshot-detect - user: {}", messageId, userId);
        SelfDestructMessageDTO.ScreenshotDetectResponse response = messageService.detectScreenshot(messageId, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取被截图的消息列表
     */
    @GetMapping("/screenshot-detected")
    public ResponseEntity<List<SelfDestructMessageDTO>> getScreenshotDetectedMessages(
            @RequestAttribute("userId") String userId) {
        logger.info("GET /api/self-destruct-messages/screenshot-detected - user: {}", userId);
        List<SelfDestructMessageDTO> messages = messageService.getScreenshotDetectedMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 检查消息是否已销毁
     */
    @GetMapping("/{messageId}/destroyed")
    public ResponseEntity<Map<String, Boolean>> isMessageDestroyed(
            @PathVariable String messageId) {
        logger.info("GET /api/self-destruct-messages/{}/destroyed", messageId);
        Boolean destroyed = messageService.isMessageDestroyed(messageId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("destroyed", destroyed);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取消息剩余时间
     */
    @GetMapping("/{messageId}/remaining-seconds")
    public ResponseEntity<Map<String, Integer>> getRemainingSeconds(
            @RequestAttribute("userId") String userId,
            @PathVariable String messageId) {
        logger.info("GET /api/self-destruct-messages/{}/remaining-seconds - user: {}", messageId, userId);
        Integer remaining = messageService.getRemainingSeconds(messageId, userId);
        Map<String, Integer> response = new HashMap<>();
        response.put("remainingSeconds", remaining);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取阅后即焚配置选项
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        
        // 支持的销毁时长选项
        config.put("durationOptions", List.of(3, 5, 10, 15, 30, 60));
        
        // 默认配置
        config.put("defaultDuration", 10);
        config.put("defaultBlurPreview", true);
        config.put("defaultAllowScreenshot", false);
        config.put("defaultAllowForward", false);
        
        // 内容类型
        config.put("contentTypes", List.of("TEXT", "IMAGE", "VIDEO", "AUDIO", "FILE"));
        
        return ResponseEntity.ok(config);
    }
}
