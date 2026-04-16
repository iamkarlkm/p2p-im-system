package com.im.service.message.controller;

import com.im.common.base.Result;
import com.im.service.message.dto.ReactionRequest;
import com.im.service.message.dto.ReactionResponse;
import com.im.service.message.service.MessageReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 消息反应控制器
 * 提供消息表情反应的 REST API 接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class MessageReactionController {

    private final MessageReactionService reactionService;

    /**
     * 添加消息反应
     * POST /api/reactions/add
     */
    @PostMapping("/add")
    public ResponseEntity<Result<ReactionResponse>> addReaction(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ReactionRequest request) {
        log.info("用户 {} 添加消息反应: 消息 {} 类型 {}", 
                userId, request.getMessageId(), request.getReactionType());
        ReactionResponse response = reactionService.addReaction(userId, request);
        return ResponseEntity.ok(Result.success(response));
    }

    /**
     * 移除消息反应
     * DELETE /api/reactions/remove
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Result<Void>> removeReaction(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam String messageId,
            @RequestParam String reactionType) {
        log.info("用户 {} 移除消息反应: 消息 {} 类型 {}", userId, messageId, reactionType);
        reactionService.removeReaction(userId, messageId, reactionType);
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 获取消息的反应列表
     * GET /api/reactions/message/{messageId}
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<Result<List<ReactionResponse>>> getReactionsByMessageId(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId) {
        log.info("用户 {} 获取消息 {} 的反应列表", userId, messageId);
        List<ReactionResponse> reactions = reactionService.getReactionsByMessageId(messageId, userId);
        return ResponseEntity.ok(Result.success(reactions));
    }

    /**
     * 获取消息反应统计
     * GET /api/reactions/stats/{messageId}
     */
    @GetMapping("/stats/{messageId}")
    public ResponseEntity<Result<Map<String, Object>>> getReactionStats(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String messageId) {
        log.info("用户 {} 获取消息 {} 的反应统计", userId, messageId);
        Map<String, Object> stats = reactionService.getReactionStats(messageId);
        return ResponseEntity.ok(Result.success(stats));
    }
}
