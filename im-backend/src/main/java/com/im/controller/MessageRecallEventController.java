package com.im.controller;

import com.im.entity.MessageRecallEventEntity;
import com.im.service.MessageRecallEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息撤回与编辑通知 REST API
 * 
 * POST  /api/recalls                     - 撤回消息
 * POST  /api/recalls/batch               - 批量撤回
 * GET   /api/recalls/conversation/{id}    - 获取会话撤回历史
 * GET   /api/recalls/user                - 获取我的撤回历史
 * GET   /api/recalls/stats/{convId}       - 获取撤回统计
 * GET   /api/recalls/message/{msgId}      - 获取某消息的撤回记录
 * POST  /api/edits                        - 记录消息编辑
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageRecallEventController {

    private final MessageRecallEventService recallService;

    // ==================== 撤回接口 ====================

    /**
     * POST /api/recalls
     * 撤回单条消息
     */
    @PostMapping("/api/recalls")
    public ResponseEntity<Map<String, Object>> recallMessage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> body) {

        Long messageId = ((Number) body.get("messageId")).longValue();
        Long conversationId = ((Number) body.get("conversationId")).longValue();
        String reason = (String) body.getOrDefault("reason", "USER_RECALL");
        String recallRole = (String) bodyOrDefault(body, "recallRole", "SENDER");

        var result = recallService.recallMessage(
            messageId, conversationId, userId, userId, recallRole, reason, "SINGLE");

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", result.success);
        resp.put("messageId", result.messageId);
        if (!result.success) resp.put("error", result.errorMessage);
        return ResponseEntity.ok(resp);
    }

    /**
     * POST /api/recalls/batch
     * 批量撤回消息
     */
    @PostMapping("/api/recalls/batch")
    public ResponseEntity<Map<String, Object>> batchRecall(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> body) {

        @SuppressWarnings("unchecked")
        List<Number> msgIds = (List<Number>) body.get("messageIds");
        Long conversationId = ((Number) body.get("conversationId")).longValue();
        String reason = (String) bodyOrDefault(body, "reason", "USER_RECALL");

        List<Long> ids = msgIds.stream().map(Number::longValue).toList();
        var results = recallService.batchRecall(ids, conversationId, userId, reason);

        long successCount = results.stream().filter(r -> r.success).count();

        Map<String, Object> resp = new HashMap<>();
        resp.put("total", ids.size());
        resp.put("success", successCount);
        resp.put("failed", ids.size() - successCount);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /api/recalls/conversation/{conversationId}
     * 获取会话撤回历史
     */
    @GetMapping("/api/recalls/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getRecallHistory(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<MessageRecallEventEntity> events =
            recallService.getRecallHistory(conversationId, page, size);

        Map<String, Object> resp = new HashMap<>();
        resp.put("events", events);
        resp.put("page", page);
        resp.put("size", size);
        return ResponseEntity.ok(resp);
    }

    /**
     * GET /api/recalls/user
     * 获取当前用户的撤回历史
     */
    @GetMapping("/api/recalls/user")
    public ResponseEntity<List<MessageRecallEventEntity>> getUserRecallHistory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(recallService.getUserRecallHistory(userId, page, size));
    }

    /**
     * GET /api/recalls/stats/{conversationId}
     * 获取撤回统计
     */
    @GetMapping("/api/recalls/stats/{conversationId}")
    public ResponseEntity<MessageRecallEventService.RecallStats> getStats(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(recallService.getStats(conversationId, days));
    }

    /**
     * GET /api/recalls/message/{messageId}
     * 获取某消息的撤回记录
     */
    @GetMapping("/api/recalls/message/{messageId}")
    public ResponseEntity<Map<String, Object>> getRecallByMessage(
            @PathVariable Long messageId,
            com.im.repository.MessageRecallEventRepository repo) {

        var opt = repo.findByMessageId(messageId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("found", opt.isPresent());
        opt.ifPresent(e -> resp.put("event", e));
        return ResponseEntity.ok(resp);
    }

    // ==================== 编辑接口 ====================

    /**
     * POST /api/edits
     * 记录消息编辑事件
     */
    @PostMapping("/api/edits")
    public ResponseEntity<Map<String, Object>> recordEdit(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> body) {

        Long messageId = ((Number) body.get("messageId")).longValue();
        Long conversationId = ((Number) body.get("conversationId")).longValue();
        String oldContent = (String) body.get("oldContent");
        String newContent = (String) body.get("newContent");
        int version = ((Number) body.getOrDefault("version", 1)).intValue();

        recallService.recordEditVersion(messageId, conversationId, userId,
            oldContent, newContent, version);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("messageId", messageId);
        resp.put("version", version);
        return ResponseEntity.ok(resp);
    }

    private String bodyOrDefault(Map<String, Object> body, String key, String defaultVal) {
        Object val = body.get(key);
        return val != null ? val.toString() : defaultVal;
    }
}
