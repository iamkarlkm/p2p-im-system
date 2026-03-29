package com.im.backend.controller;

import com.im.backend.dto.MessageRecallDTO;
import com.im.backend.dto.ApiResponse;
import com.im.backend.model.MessageRecallLog;
import com.im.backend.service.MessageRecallService;
import com.im.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息撤回API控制器
 * 提供消息撤回、批量撤回、撤回历史查询等接口
 */
@RestController
@RequestMapping("/api/v1/messages/recall")
@Tag(name = "消息撤回", description = "消息撤回相关接口")
@SecurityRequirement(name = "bearerAuth")
public class MessageRecallController {

    private static final Logger logger = LoggerFactory.getLogger(MessageRecallController.class);

    @Autowired
    private MessageRecallService recallService;

    @Autowired
    private AuthService authService;

    /**
     * 撤回单条消息
     */
    @PostMapping("/{messageId}")
    @Operation(summary = "撤回消息", description = "撤回指定ID的消息，需满足时间限制和权限要求")
    public ResponseEntity<ApiResponse<MessageRecallDTO>> recallMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "撤回类型(USER/ADMIN)") @RequestParam(defaultValue = "USER") String recallType,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        logger.info("User {} requesting to recall message {}", userId, messageId);

        MessageRecallDTO result = recallService.recallMessage(messageId, userId, recallType);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result, "消息撤回成功"));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(result.getErrorCode(), result.getErrorMessage()));
        }
    }

    /**
     * 批量撤回消息
     */
    @PostMapping("/batch")
    @Operation(summary = "批量撤回消息", description = "批量撤回多条消息")
    public ResponseEntity<ApiResponse<List<MessageRecallDTO>>> batchRecallMessages(
            @Parameter(description = "消息ID列表") @RequestBody @Valid BatchRecallRequest request,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        logger.info("User {} requesting to batch recall {} messages", userId, request.getMessageIds().size());

        List<MessageRecallDTO> results = recallService.batchRecallMessages(
            request.getMessageIds(), userId, request.getRecallType());

        long successCount = results.stream().filter(MessageRecallDTO::isSuccess).count();
        long failCount = results.size() - successCount;

        String message = String.format("批量撤回完成：成功 %d 条，失败 %d 条", successCount, failCount);

        return ResponseEntity.ok(ApiResponse.success(results, message));
    }

    /**
     * 检查消息是否可撤回
     */
    @GetMapping("/{messageId}/can-recall")
    @Operation(summary = "检查可撤回状态", description = "检查指定消息是否可以撤回")
    public ResponseEntity<ApiResponse<Map<String, Object>>> canRecallMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        boolean canRecall = recallService.canRecall(messageId, userId);

        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);
        data.put("canRecall", canRecall);
        data.put("timeLimit", recallService.getRecallTimeLimit());

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * 获取消息的撤回历史
     */
    @GetMapping("/{messageId}/history")
    @Operation(summary = "获取消息撤回历史", description = "获取指定消息的撤回记录")
    public ResponseEntity<ApiResponse<List<MessageRecallLog>>> getMessageRecallHistory(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        List<MessageRecallLog> history = recallService.getRecallHistory(messageId);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取用户的撤回记录
     */
    @GetMapping("/history/user")
    @Operation(summary = "获取用户撤回记录", description = "获取当前用户的所有撤回记录")
    public ResponseEntity<ApiResponse<Page<MessageRecallLog>>> getUserRecallHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        Page<MessageRecallLog> history = recallService.getUserRecallHistory(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取会话的撤回记录
     */
    @GetMapping("/history/conversation/{conversationId}")
    @Operation(summary = "获取会话撤回记录", description = "获取指定会话的撤回记录")
    public ResponseEntity<ApiResponse<Page<MessageRecallLog>>> getConversationRecallHistory(
            @Parameter(description = "会话ID") @PathVariable Long conversationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);
        Page<MessageRecallLog> history = recallService.getConversationRecallHistory(conversationId, page, size);

        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取撤回时间限制配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取撤回配置", description = "获取当前撤回时间限制配置")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecallConfig(
            @RequestHeader("Authorization") String token) {

        authService.validateToken(token);

        Map<String, Object> config = new HashMap<>();
        config.put("timeLimitSeconds", recallService.getRecallTimeLimit());
        config.put("timeLimitMinutes", recallService.getRecallTimeLimit() / 60);

        return ResponseEntity.ok(ApiResponse.success(config));
    }

    /**
     * 更新撤回时间限制（仅管理员）
     */
    @PutMapping("/config/time-limit")
    @Operation(summary = "更新撤回时间限制", description = "更新撤回时间限制（仅管理员可用）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateRecallTimeLimit(
            @Parameter(description = "新的时间限制(秒)") @RequestParam int seconds,
            @RequestHeader("Authorization") String token) {

        // 验证管理员权限
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "需要管理员权限"));
        }

        try {
            recallService.updateRecallTimeLimit(seconds);

            Map<String, Object> config = new HashMap<>();
            config.put("timeLimitSeconds", seconds);
            config.put("timeLimitMinutes", seconds / 60);

            return ResponseEntity.ok(ApiResponse.success(config, "撤回时间限制已更新"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_PARAMETER", e.getMessage()));
        }
    }

    /**
     * 撤销撤回（恢复消息）- 仅管理员
     */
    @PostMapping("/{messageId}/undo")
    @Operation(summary = "撤销撤回", description = "撤销之前的撤回操作，恢复消息（仅管理员可用）")
    public ResponseEntity<ApiResponse<MessageRecallDTO>> undoRecall(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @RequestHeader("Authorization") String token) {

        // 验证管理员权限
        if (!authService.isAdmin(token)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.error("FORBIDDEN", "需要管理员权限"));
        }

        Long adminId = authService.getUserIdFromToken(token);
        logger.info("Admin {} requesting to undo recall for message {}", adminId, messageId);

        MessageRecallDTO result = recallService.undoRecall(messageId, adminId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result, "撤回已撤销，消息已恢复"));
        } else {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(result.getErrorCode(), result.getErrorMessage()));
        }
    }

    /**
     * 获取可撤回消息列表
     */
    @GetMapping("/recallable/{conversationId}")
    @Operation(summary = "获取可撤回消息", description = "获取会话中当前用户可撤回的消息列表")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecallableMessages(
            @Parameter(description = "会话ID") @PathVariable Long conversationId,
            @RequestHeader("Authorization") String token) {

        Long userId = authService.getUserIdFromToken(token);
        var messages = recallService.getRecallableMessages(conversationId, userId);

        List<Map<String, Object>> result = messages.stream()
            .map(msg -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", msg.getId());
                map.put("content", msg.getContent());
                map.put("sentTime", msg.getSentTime());
                map.put("messageType", msg.getMessageType());
                return map;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ========== 内部请求类 ==========

    public static class BatchRecallRequest {
        @NotNull(message = "消息ID列表不能为空")
        private List<Long> messageIds;

        private String recallType = "USER";

        public List<Long> getMessageIds() {
            return messageIds;
        }

        public void setMessageIds(List<Long> messageIds) {
            this.messageIds = messageIds;
        }

        public String getRecallType() {
            return recallType;
        }

        public void setRecallType(String recallType) {
            this.recallType = recallType;
        }
    }
}
