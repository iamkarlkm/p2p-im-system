package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.RecallMessageDTO;
import com.im.backend.service.MessageRecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息撤回控制器
 */
@RestController
@RequestMapping("/api/message-recall")
public class MessageRecallController {

    @Autowired
    private MessageRecallService recallService;

    /**
     * 撤回消息
     */
    @PostMapping("/{messageId}")
    public ResponseEntity<ApiResponse<RecallMessageDTO>> recallMessage(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long messageId,
            @RequestParam(required = false) String reason) {
        
        RecallMessageDTO result = recallService.recallMessage(messageId, userId, reason);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 检查是否可以撤回
     */
    @GetMapping("/{messageId}/can-recall")
    public ResponseEntity<ApiResponse<Map<String, Object>>> canRecall(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long messageId) {
        
        boolean canRecall = recallService.canRecall(messageId, userId);
        long todayCount = recallService.getTodayRecallCount(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("canRecall", canRecall);
        result.put("todayRecallCount", todayCount);
        result.put("dailyLimit", 50);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 检查消息是否已撤回
     */
    @GetMapping("/{messageId}/is-recalled")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isRecalled(
            @PathVariable Long messageId) {
        
        boolean isRecalled = recallService.isRecalled(messageId);
        Map<String, Object> result = new HashMap<>();
        result.put("isRecalled", isRecalled);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取撤回记录
     */
    @GetMapping("/{messageId}/record")
    public ResponseEntity<ApiResponse<RecallMessageDTO>> getRecallRecord(
            @PathVariable Long messageId) {
        
        RecallMessageDTO record = recallService.getRecallRecord(messageId);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    /**
     * 获取我的撤回记录
     */
    @GetMapping("/my-records")
    public ResponseEntity<ApiResponse<List<RecallMessageDTO>>> getMyRecallRecords(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<RecallMessageDTO> records = recallService.getUserRecallRecords(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * 获取会话中的撤回记录
     */
    @GetMapping("/conversation/{conversationType}/{conversationId}")
    public ResponseEntity<ApiResponse<List<RecallMessageDTO>>> getConversationRecallRecords(
            @PathVariable String conversationType,
            @PathVariable Long conversationId) {
        
        List<RecallMessageDTO> records = recallService.getConversationRecallRecords(conversationType, conversationId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
