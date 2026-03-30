package com.im.controller;

import com.im.dto.EmojiMessageRequest;
import com.im.dto.EmojiMessageResponse;
import com.im.service.EmojiMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 表情消息控制器
 * 功能#23: 表情消息
 */
@RestController
@RequestMapping("/api/emoji-message")
public class EmojiMessageController {

    @Autowired
    private EmojiMessageService emojiMessageService;

    /**
     * 发送表情消息
     * POST /api/emoji-message/send
     */
    @PostMapping("/send")
    public ResponseEntity<EmojiMessageResponse> sendEmojiMessage(
            @RequestAttribute("userId") Long userId,
            @RequestBody EmojiMessageRequest request) {
        EmojiMessageResponse response = emojiMessageService.sendEmojiMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取表情消息历史
     * GET /api/emoji-message/history/{conversationType}/{conversationId}
     */
    @GetMapping("/history/{conversationType}/{conversationId}")
    public ResponseEntity<List<EmojiMessageResponse>> getEmojiMessageHistory(
            @PathVariable String conversationType,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "50") Integer limit) {
        List<EmojiMessageResponse> history = emojiMessageService.getEmojiMessageHistory(
            conversationId, conversationType, limit);
        return ResponseEntity.ok(history);
    }

    /**
     * 标记表情消息已读
     * POST /api/emoji-message/{messageId}/read
     */
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(
            @PathVariable Long messageId,
            @RequestAttribute("userId") Long userId) {
        Boolean success = emojiMessageService.markAsRead(messageId, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取用户最常用的表情
     * GET /api/emoji-message/top-emojis
     */
    @GetMapping("/top-emojis")
    public ResponseEntity<List<Map<String, Object>>> getTopEmojis(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") Integer topN) {
        List<Map<String, Object>> topEmojis = emojiMessageService.getTopEmojis(userId, topN);
        return ResponseEntity.ok(topEmojis);
    }

    /**
     * 获取表情使用统计
     * GET /api/emoji-message/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getEmojiStatistics(
            @RequestAttribute("userId") Long userId) {
        Map<String, Object> stats = emojiMessageService.getEmojiStatistics(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取表情分类列表
     * GET /api/emoji-message/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getEmojiCategories() {
        List<String> categories = emojiMessageService.getEmojiCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 按分类获取表情
     * GET /api/emoji-message/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<EmojiMessageResponse>> getEmojisByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "50") Integer limit) {
        List<EmojiMessageResponse> emojis = emojiMessageService.getEmojisByCategory(category, limit);
        return ResponseEntity.ok(emojis);
    }
}
