package com.im.backend.controller;

import com.im.backend.dto.AtMentionRequest;
import com.im.backend.dto.AtMentionResponse;
import com.im.backend.entity.AtMentionSettings;
import com.im.backend.service.AtMentionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @提及控制器
 */
@RestController
@RequestMapping("/api/at-mention")
@RequiredArgsConstructor
public class AtMentionController {

    private final AtMentionService atMentionService;

    /**
     * 处理消息中的@提及
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processMentions(
            @Valid @RequestBody AtMentionRequest request) {
        List<AtMentionResponse> mentions = atMentionService.processMentions(request, request.getMessagePreview());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", mentions);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取@提及列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMentions(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AtMentionResponse> mentions = atMentionService.getUserMentions(userId, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", mentions.getContent());
        result.put("totalPages", mentions.getTotalPages());
        result.put("totalElements", mentions.getTotalElements());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取未读@提及数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam Long userId) {
        long count = atMentionService.getUnreadCount(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取群聊未读@提及数量
     */
    @GetMapping("/unread-count/room")
    public ResponseEntity<Map<String, Object>> getUnreadCountInRoom(
            @RequestParam Long userId,
            @RequestParam Long roomId) {
        long count = atMentionService.getUnreadCountInRoom(userId, roomId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 标记已读
     */
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestParam Long userId,
            @RequestBody List<Long> mentionIds) {
        int updated = atMentionService.markAsRead(userId, mentionIds);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("updated", updated);
        return ResponseEntity.ok(result);
    }

    /**
     * 标记群聊内所有@已读
     */
    @PostMapping("/mark-read/room")
    public ResponseEntity<Map<String, Object>> markAllAsReadInRoom(
            @RequestParam Long userId,
            @RequestParam Long roomId) {
        int updated = atMentionService.markAllAsReadInRoom(userId, roomId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("updated", updated);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取@提及设置
     */
    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(@RequestParam Long userId) {
        AtMentionSettings settings = atMentionService.getSettings(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", settings);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新@提及设置
     */
    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestParam Long userId,
            @RequestBody AtMentionSettings settings) {
        AtMentionSettings updated = atMentionService.updateSettings(userId, settings);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", updated);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除消息时清理@提及
     */
    @DeleteMapping("/by-message/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteByMessageId(@PathVariable Long messageId) {
        atMentionService.deleteByMessageId(messageId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return ResponseEntity.ok(result);
    }
}
