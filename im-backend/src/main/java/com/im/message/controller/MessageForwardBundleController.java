package com.im.message.controller;

import com.im.message.entity.MessageForwardBundleEntity;
import com.im.message.service.MessageForwardBundleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息合并转发 REST API 控制器
 */
@RestController
@RequestMapping("/api/message/forward")
@RequiredArgsConstructor
@Tag(name = "消息转发", description = "消息合并转发、选择转发功能")
public class MessageForwardBundleController {

    private final MessageForwardBundleService forwardService;

    /**
     * 创建转发草稿
     */
    @PostMapping("/draft")
    @Operation(summary = "创建转发草稿", description = "创建一个新的消息转发草稿")
    public ResponseEntity<Map<String, Object>> createDraft(
            @AuthenticationPrincipal Long userId,
            @RequestParam Long sourceConversationId,
            @RequestParam(defaultValue = "MERGE") String forwardType,
            @RequestBody List<Long> messageIds) {
        
        MessageForwardBundleEntity bundle = forwardService.createForwardDraft(
            userId, sourceConversationId, 
            MessageForwardBundleEntity.ForwardType.valueOf(forwardType),
            messageIds);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bundleId", bundle.getBundleId());
        response.put("id", bundle.getId());
        response.put("messageCount", bundle.getMessageCount());
        response.put("createdAt", bundle.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 添加消息到草稿
     */
    @PostMapping("/draft/{bundleId}/add")
    @Operation(summary = "添加消息到草稿", description = "添加一条消息到转发草稿")
    public ResponseEntity<Map<String, Object>> addMessage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bundleId,
            @RequestParam Long messageId) {
        
        MessageForwardBundleEntity bundle = forwardService.addMessageToDraft(bundleId, userId, messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bundleId", bundle.getBundleId());
        response.put("messageCount", bundle.getMessageCount());
        response.put("updatedAt", bundle.getUpdatedAt());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 从草稿移除消息
     */
    @PostMapping("/draft/{bundleId}/remove")
    @Operation(summary = "从草稿移除消息", description = "从转发草稿中移除一条消息")
    public ResponseEntity<Map<String, Object>> removeMessage(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bundleId,
            @RequestParam Long messageId) {
        
        MessageForwardBundleEntity bundle = forwardService.removeMessageFromDraft(bundleId, userId, messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bundleId", bundle.getBundleId());
        response.put("messageCount", bundle.getMessageCount());
        response.put("updatedAt", bundle.getUpdatedAt());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 更新转发配置
     */
    @PutMapping("/draft/{bundleId}/config")
    @Operation(summary = "更新转发配置", description = "更新转发配置（发送模式/发送者信息等）")
    public ResponseEntity<Map<String, Object>> updateConfig(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bundleId,
            @RequestBody Map<String, Object> config) {
        
        MessageForwardBundleEntity bundle = forwardService.updateForwardConfig(bundleId, userId, config);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bundleId", bundle.getBundleId());
        response.put("sendMode", bundle.getSendMode().name());
        response.put("includeSenderInfo", bundle.getIncludeSenderInfo());
        response.put("includeTimestamp", bundle.getIncludeTimestamp());
        response.put("anonymizeSenders", bundle.getAnonymizeSenders());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 执行转发
     */
    @PostMapping("/draft/{bundleId}/send")
    @Operation(summary = "执行转发", description = "将草稿中的消息转发到目标会话")
    public ResponseEntity<Map<String, Object>> executeForward(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bundleId,
            @RequestParam Long targetConversationId) {
        
        try {
            MessageForwardBundleEntity bundle = forwardService.executeForward(bundleId, userId, targetConversationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", bundle.getStatus() == MessageForwardBundleEntity.ForwardStatus.SENT);
            response.put("bundleId", bundle.getBundleId());
            response.put("status", bundle.getStatus().name());
            response.put("forwardedAt", bundle.getForwardedAt());
            response.put("targetConversationId", targetConversationId);
            
            if (bundle.getStatus() == MessageForwardBundleEntity.ForwardStatus.FAILED) {
                response.put("error", bundle.getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * 取消草稿
     */
    @PostMapping("/draft/{bundleId}/cancel")
    @Operation(summary = "取消草稿", description = "取消一个转发草稿")
    public ResponseEntity<Map<String, Object>> cancelDraft(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long bundleId) {
        
        forwardService.cancelDraft(bundleId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("bundleId", bundleId);
        response.put("cancelledAt", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取草稿列表
     */
    @GetMapping("/drafts")
    @Operation(summary = "获取草稿列表", description = "获取当前用户的所有转发草稿")
    public ResponseEntity<Map<String, Object>> getDrafts(@AuthenticationPrincipal Long userId) {
        
        List<MessageForwardBundleEntity> drafts = forwardService.getUserDrafts(userId);
        
        List<Map<String, Object>> draftList = drafts.stream()
            .map(bundle -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", bundle.getId());
                item.put("bundleId", bundle.getBundleId());
                item.put("sourceConversationId", bundle.getSourceConversationId());
                item.put("messageCount", bundle.getMessageCount());
                item.put("forwardType", bundle.getForwardType().name());
                item.put("sendMode", bundle.getSendMode().name());
                item.put("title", bundle.getTitle());
                item.put("customComment", bundle.getCustomComment());
                item.put("createdAt", bundle.getCreatedAt());
                item.put("updatedAt", bundle.getUpdatedAt());
                return item;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", draftList.size());
        response.put("drafts", draftList);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取转发历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取转发历史", description = "获取当前用户的转发历史记录")
    public ResponseEntity<Map<String, Object>> getHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<MessageForwardBundleEntity> history = forwardService.getForwardHistory(userId, limit);
        
        List<Map<String, Object>> historyList = history.stream()
            .map(bundle -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id", bundle.getId());
                item.put("bundleId", bundle.getBundleId());
                item.put("sourceConversationId", bundle.getSourceConversationId());
                item.put("targetConversationId", bundle.getTargetConversationId());
                item.put("messageCount", bundle.getMessageCount());
                item.put("forwardType", bundle.getForwardType().name());
                item.put("title", bundle.getTitle());
                item.put("forwardedAt", bundle.getForwardedAt());
                return item;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", historyList.size());
        response.put("history", historyList);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取转发统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取转发统计", description = "获取当前用户的转发统计信息")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal Long userId) {
        
        Map<String, Object> stats = forwardService.getForwardStats(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("stats", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查消息转发服务状态")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "message-forward");
        response.put("status", "UP");
        response.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}