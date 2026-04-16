package com.im.service.message.controller;

import com.im.service.message.dto.MessageResponse;
import com.im.service.message.dto.SendMessageRequest;
import com.im.service.message.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息控制器 - REST API接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // ========== 消息发送 ==========

    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        log.info("Sending message: conversationId={}, senderId={}, type={}", 
                request.getConversationId(), request.getSenderId(), request.getType());
        MessageResponse response = messageService.sendMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ========== 消息查询 ==========

    /**
     * 获取单条消息
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        return messageService.getMessage(messageId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取会话消息列表(分页)
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponse>> getConversationMessages(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageResponse> messages = messageService.getConversationMessagesPage(conversationId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取会话消息列表(简单列表)
     */
    @GetMapping("/conversation/{conversationId}/list")
    public ResponseEntity<List<MessageResponse>> getConversationMessagesList(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MessageResponse> messages = messageService.getConversationMessages(conversationId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取会话中指定时间之后的消息
     */
    @GetMapping("/conversation/{conversationId}/since")
    public ResponseEntity<List<MessageResponse>> getMessagesSince(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        List<MessageResponse> messages = messageService.getMessagesSince(conversationId, since, userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取会话最新消息
     */
    @GetMapping("/conversation/{conversationId}/latest")
    public ResponseEntity<MessageResponse> getLatestMessage(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId) {
        return messageService.getLatestMessage(conversationId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== 消息搜索 ==========

    /**
     * 搜索会话中的消息
     */
    @GetMapping("/conversation/{conversationId}/search")
    public ResponseEntity<List<MessageResponse>> searchMessages(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String keyword) {
        List<MessageResponse> messages = messageService.searchMessages(conversationId, keyword, userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 全局搜索用户的消息
     */
    @GetMapping("/search")
    public ResponseEntity<Page<MessageResponse>> searchUserMessages(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageResponse> messages = messageService.searchUserMessages(userId, keyword, page, size);
        return ResponseEntity.ok(messages);
    }

    // ========== 消息状态更新 ==========

    /**
     * 标记消息为已读(单条)
     */
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable String messageId) {
        boolean success = messageService.markAsRead(messageId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        result.put("status", "READ");
        return ResponseEntity.ok(result);
    }

    /**
     * 标记会话中所有消息为已读
     */
    @PostMapping("/conversation/{conversationId}/read-all")
    public ResponseEntity<Map<String, Object>> markConversationAsRead(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId) {
        int count = messageService.markConversationAsRead(conversationId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("conversationId", conversationId);
        result.put("markedCount", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestHeader("X-User-Id") String userId) {
        long count = messageService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("unreadCount", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取会话未读消息数
     */
    @GetMapping("/conversation/{conversationId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCountByConversation(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId) {
        long count = messageService.getUnreadCountByConversation(conversationId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("conversationId", conversationId);
        result.put("unreadCount", count);
        return ResponseEntity.ok(result);
    }

    // ========== 消息撤回 ==========

    /**
     * 撤回消息
     */
    @PostMapping("/{messageId}/recall")
    public ResponseEntity<Map<String, Object>> recallMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = messageService.recallMessage(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        if (!success) {
            result.put("error", "Cannot recall message. It may be too old or you are not the sender.");
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 检查消息是否可以撤回
     */
    @GetMapping("/{messageId}/can-recall")
    public ResponseEntity<Map<String, Object>> canRecall(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean canRecall = messageService.canRecall(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("messageId", messageId);
        result.put("canRecall", canRecall);
        return ResponseEntity.ok(result);
    }

    // ========== 消息删除 ==========

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = messageService.deleteMessage(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        if (!success) {
            result.put("error", "Cannot delete message. It may not exist or you don't have permission.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 批量删除消息
     */
    @PostMapping("/batch-delete")
    public ResponseEntity<Map<String, Object>> batchDeleteMessages(
            @RequestBody List<String> messageIds,
            @RequestHeader("X-User-Id") String userId) {
        int count = messageService.batchDeleteMessages(messageIds, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("deletedCount", count);
        return ResponseEntity.ok(result);
    }

    // ========== 消息置顶 ==========

    /**
     * 置顶消息
     */
    @PostMapping("/{messageId}/pin")
    public ResponseEntity<Map<String, Object>> pinMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = messageService.pinMessage(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        result.put("pinned", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 取消置顶消息
     */
    @PostMapping("/{messageId}/unpin")
    public ResponseEntity<Map<String, Object>> unpinMessage(@PathVariable String messageId) {
        boolean success = messageService.unpinMessage(messageId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        result.put("pinned", false);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取会话置顶消息
     */
    @GetMapping("/conversation/{conversationId}/pinned")
    public ResponseEntity<List<MessageResponse>> getPinnedMessages(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") String userId) {
        List<MessageResponse> messages = messageService.getPinnedMessages(conversationId, userId);
        return ResponseEntity.ok(messages);
    }

    // ========== 消息收藏 ==========

    /**
     * 收藏消息
     */
    @PostMapping("/{messageId}/favorite")
    public ResponseEntity<Map<String, Object>> favoriteMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = messageService.favoriteMessage(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        result.put("favorited", true);
        return ResponseEntity.ok(result);
    }

    /**
     * 取消收藏消息
     */
    @PostMapping("/{messageId}/unfavorite")
    public ResponseEntity<Map<String, Object>> unfavoriteMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId) {
        boolean success = messageService.unfavoriteMessage(messageId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        result.put("favorited", false);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户收藏的消息
     */
    @GetMapping("/favorites")
    public ResponseEntity<Page<MessageResponse>> getFavoriteMessages(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageResponse> messages = messageService.getFavoriteMessages(userId, page, size);
        return ResponseEntity.ok(messages);
    }

    // ========== 消息编辑 ==========

    /**
     * 编辑消息
     */
    @PutMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> editMessage(
            @PathVariable String messageId,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, String> body) {
        String newContent = body.get("content");
        boolean success = messageService.editMessage(messageId, userId, newContent);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("messageId", messageId);
        if (!success) {
            result.put("error", "Cannot edit message. It may be recalled or you are not the sender.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        return ResponseEntity.ok(result);
    }

    // ========== 统计信息 ==========

    /**
     * 获取用户消息统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMessageStats(
            @RequestHeader("X-User-Id") String userId) {
        long sentCount = messageService.getSentMessageCount(userId);
        long unreadCount = messageService.getUnreadCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("sentMessageCount", sentCount);
        result.put("unreadMessageCount", unreadCount);
        return ResponseEntity.ok(result);
    }
}
