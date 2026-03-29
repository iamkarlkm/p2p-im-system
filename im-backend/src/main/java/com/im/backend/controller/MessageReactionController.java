package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.MessageReactionDTO;
import com.im.backend.dto.ReactionSummaryDTO;
import com.im.backend.service.MessageReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息表情回应控制器
 * 提供消息表情回应的REST API接口
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/v1/reactions")
@Tag(name = "消息表情回应", description = "消息表情回应管理相关接口")
public class MessageReactionController {

    private static final Logger logger = LoggerFactory.getLogger(MessageReactionController.class);

    @Autowired
    private MessageReactionService reactionService;

    /**
     * 添加表情回应
     */
    @PostMapping
    @Operation(summary = "添加表情回应", description = "为消息添加表情符号回应")
    public ResponseEntity<ApiResponse<MessageReactionDTO>> addReaction(
            @Valid @RequestBody MessageReactionDTO reactionDTO) {
        logger.info("Adding reaction for message: {}", reactionDTO.getMessageId());
        MessageReactionDTO result = reactionService.addReaction(reactionDTO);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 切换表情回应
     */
    @PostMapping("/toggle")
    @Operation(summary = "切换表情回应", description = "切换表情回应状态（有则删，无则加）")
    public ResponseEntity<ApiResponse<MessageReactionDTO>> toggleReaction(
            @Valid @RequestBody MessageReactionDTO reactionDTO) {
        logger.info("Toggling reaction for message: {}", reactionDTO.getMessageId());
        MessageReactionDTO result = reactionService.toggleReaction(reactionDTO);
        if (result == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "Reaction removed"));
        }
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 移除表情回应
     */
    @DeleteMapping("/message/{messageId}/user/{userId}/emoji/{emojiCode}")
    @Operation(summary = "移除表情回应", description = "移除用户对消息的特定表情回应")
    public ResponseEntity<ApiResponse<Void>> removeReaction(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "表情代码") @PathVariable String emojiCode) {
        logger.info("Removing reaction: messageId={}, userId={}", messageId, userId);
        reactionService.removeReaction(messageId, userId, emojiCode);
        return ResponseEntity.ok(ApiResponse.success(null, "Reaction removed successfully"));
    }

    /**
     * 获取消息的表情回应汇总
     */
    @GetMapping("/message/{messageId}/summary")
    @Operation(summary = "获取表情回应汇总", description = "获取消息的所有表情回应统计信息")
    public ResponseEntity<ApiResponse<ReactionSummaryDTO>> getReactionSummary(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "当前用户ID") @RequestParam Long currentUserId) {
        logger.debug("Getting reaction summary for message: {}", messageId);
        ReactionSummaryDTO summary = reactionService.getReactionSummary(messageId, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * 获取消息的表情回应列表
     */
    @GetMapping("/message/{messageId}")
    @Operation(summary = "获取表情回应列表", description = "获取消息的所有表情回应")
    public ResponseEntity<ApiResponse<List<MessageReactionDTO>>> getReactionsByMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        logger.debug("Getting reactions for message: {}", messageId);
        List<MessageReactionDTO> reactions = reactionService.getReactionsByMessage(messageId);
        return ResponseEntity.ok(ApiResponse.success(reactions));
    }

    /**
     * 分页获取消息的表情回应
     */
    @GetMapping("/message/{messageId}/paged")
    @Operation(summary = "分页获取表情回应", description = "分页获取消息的表情回应")
    public ResponseEntity<ApiResponse<Page<MessageReactionDTO>>> getReactionsByMessagePaged(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        logger.debug("Getting paged reactions for message: {}", messageId);
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageReactionDTO> reactions = reactionService.getReactionsByMessage(messageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reactions));
    }

    /**
     * 获取用户对消息的回应
     */
    @GetMapping("/message/{messageId}/user/{userId}")
    @Operation(summary = "获取用户回应", description = "获取用户对消息的所有表情回应")
    public ResponseEntity<ApiResponse<List<MessageReactionDTO>>> getReactionsByMessageAndUser(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        logger.debug("Getting reactions for message: {} and user: {}", messageId, userId);
        List<MessageReactionDTO> reactions = reactionService.getReactionsByMessageAndUser(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(reactions));
    }

    /**
     * 删除用户对消息的所有回应
     */
    @DeleteMapping("/message/{messageId}/user/{userId}")
    @Operation(summary = "删除所有回应", description = "删除用户对消息的所有表情回应")
    public ResponseEntity<ApiResponse<Void>> removeAllReactionsByUser(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        logger.info("Removing all reactions for message: {} and user: {}", messageId, userId);
        reactionService.removeAllReactionsByUser(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "All reactions removed"));
    }

    /**
     * 批量获取消息的表情回应汇总
     */
    @PostMapping("/summaries")
    @Operation(summary = "批量获取汇总", description = "批量获取多条消息的表情回应汇总")
    public ResponseEntity<ApiResponse<List<ReactionSummaryDTO>>> getReactionSummaries(
            @RequestBody List<Long> messageIds,
            @RequestParam Long currentUserId) {
        logger.debug("Getting reaction summaries for {} messages", messageIds.size());
        List<ReactionSummaryDTO> summaries = reactionService.getReactionSummaries(messageIds, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(summaries));
    }

    /**
     * 获取会话中的热门表情
     */
    @GetMapping("/conversation/{conversationId}/popular")
    @Operation(summary = "获取热门表情", description = "获取会话中最热门的表情")
    public ResponseEntity<ApiResponse<List<ReactionSummaryDTO.EmojiCountDTO>>> getPopularEmojis(
            @Parameter(description = "会话ID") @PathVariable Long conversationId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") int limit) {
        logger.debug("Getting popular emojis for conversation: {}", conversationId);
        List<ReactionSummaryDTO.EmojiCountDTO> emojis = reactionService.getPopularEmojis(conversationId, limit);
        return ResponseEntity.ok(ApiResponse.success(emojis));
    }

    /**
     * 检查用户是否回应了消息
     */
    @GetMapping("/message/{messageId}/user/{userId}/has-reacted")
    @Operation(summary = "检查是否回应", description = "检查用户是否回应了消息")
    public ResponseEntity<ApiResponse<Boolean>> hasUserReacted(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        boolean hasReacted = reactionService.hasUserReacted(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(hasReacted));
    }
}
