package com.im.controller;

import com.im.dto.*;
import com.im.service.MessageForwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息转发控制器
 * 功能#22: 消息转发
 */
@RestController
@RequestMapping("/api/message-forward")
public class MessageForwardController {

    @Autowired
    private MessageForwardService forwardService;

    /**
     * 转发消息
     * POST /api/message-forward
     */
    @PostMapping
    public ResponseEntity<ForwardMessageResponse> forwardMessage(
            @RequestAttribute("userId") Long userId,
            @RequestBody ForwardMessageRequest request) {
        ForwardMessageResponse response = forwardService.forwardMessage(userId, request);
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 批量转发消息
     * POST /api/message-forward/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<List<ForwardMessageResponse>> batchForwardMessage(
            @RequestAttribute("userId") Long userId,
            @RequestBody ForwardMessageRequest request) {
        List<ForwardMessageResponse> responses = forwardService.batchForwardMessage(userId, request);
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取转发记录
     * GET /api/message-forward/records
     */
    @GetMapping("/records")
    public ResponseEntity<List<ForwardRecordDTO>> getForwardRecords(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "50") Integer limit) {
        List<ForwardRecordDTO> records = forwardService.getForwardRecords(userId, limit);
        return ResponseEntity.ok(records);
    }

    /**
     * 获取消息转发统计
     * GET /api/message-forward/{messageId}/count
     */
    @GetMapping("/{messageId}/count")
    public ResponseEntity<Map<String, Long>> getForwardCount(
            @PathVariable Long messageId) {
        Long count = forwardService.getForwardCount(messageId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(result);
    }

    /**
     * 检查是否可以转发
     * GET /api/message-forward/{messageId}/can-forward
     */
    @GetMapping("/{messageId}/can-forward")
    public ResponseEntity<Map<String, Boolean>> canForward(
            @PathVariable Long messageId,
            @RequestAttribute("userId") Long userId) {
        Boolean canForward = forwardService.canForward(messageId, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("canForward", canForward);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取合并转发内容
     * GET /api/message-forward/{forwardId}/content
     */
    @GetMapping("/{forwardId}/content")
    public ResponseEntity<List<MessageDTO>> getMergedForwardContent(
            @PathVariable Long forwardId) {
        List<MessageDTO> content = forwardService.getMergedForwardContent(forwardId);
        return ResponseEntity.ok(content);
    }
}
