package com.im.controller;

import com.im.dto.ImageMessageRequest;
import com.im.dto.ImageMessageResponse;
import com.im.service.ImageMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 图片消息控制器
 * 功能#24: 图片消息
 */
@RestController
@RequestMapping("/api/image-message")
public class ImageMessageController {

    @Autowired
    private ImageMessageService imageMessageService;

    /**
     * 发送图片消息
     * POST /api/image-message/send
     */
    @PostMapping("/send")
    public ResponseEntity<ImageMessageResponse> sendImageMessage(
            @RequestAttribute("userId") Long userId,
            @RequestBody ImageMessageRequest request) {
        ImageMessageResponse response = imageMessageService.sendImageMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取图片消息详情
     * GET /api/image-message/{messageId}
     */
    @GetMapping("/{messageId}")
    public ResponseEntity<ImageMessageResponse> getImageMessage(
            @PathVariable Long messageId) {
        ImageMessageResponse response = imageMessageService.getImageMessage(messageId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取图片消息历史
     * GET /api/image-message/history/{conversationType}/{conversationId}
     */
    @GetMapping("/history/{conversationType}/{conversationId}")
    public ResponseEntity<List<ImageMessageResponse>> getImageMessageHistory(
            @PathVariable String conversationType,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "50") Integer limit) {
        List<ImageMessageResponse> history = imageMessageService.getImageMessageHistory(
            conversationId, conversationType, limit);
        return ResponseEntity.ok(history);
    }

    /**
     * 标记图片消息已读
     * POST /api/image-message/{messageId}/read
     */
    @PostMapping("/{messageId}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(
            @PathVariable Long messageId,
            @RequestAttribute("userId") Long userId) {
        Boolean success = imageMessageService.markAsRead(messageId, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 获取原图URL
     * GET /api/image-message/{messageId}/original
     */
    @GetMapping("/{messageId}/original")
    public ResponseEntity<Map<String, String>> getOriginalImageUrl(
            @PathVariable Long messageId) {
        String url = imageMessageService.getOriginalImageUrl(messageId);
        if (url != null) {
            return ResponseEntity.ok(Map.of("originalUrl", url));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取用户最近图片
     * GET /api/image-message/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ImageMessageResponse>> getRecentImages(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<ImageMessageResponse> images = imageMessageService.getRecentImages(userId, limit);
        return ResponseEntity.ok(images);
    }

    /**
     * 获取图片使用统计
     * GET /api/image-message/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getImageStatistics(
            @RequestAttribute("userId") Long userId) {
        Map<String, Object> stats = imageMessageService.getImageStatistics(userId);
        return ResponseEntity.ok(stats);
    }
}
