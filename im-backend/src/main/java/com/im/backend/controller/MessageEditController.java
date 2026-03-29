package com.im.backend.controller;

import com.im.backend.dto.ApiResponse;
import com.im.backend.dto.MessageEditDTO;
import com.im.backend.dto.MessageEditHistoryDTO;
import com.im.backend.service.MessageEditService;
import com.im.backend.service.MessageEditService.CanEditResult;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息编辑控制器
 * 提供消息编辑相关的REST API
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
@RestController
@RequestMapping("/api/v1/messages/edit")
@Tag(name = "消息编辑", description = "消息编辑历史管理相关接口")
public class MessageEditController {

    private static final Logger logger = LoggerFactory.getLogger(MessageEditController.class);

    @Autowired
    private MessageEditService messageEditService;

    /**
     * 编辑消息
     */
    @PostMapping
    @Operation(summary = "编辑消息", description = "编辑已发送的消息内容")
    public ResponseEntity<ApiResponse<MessageEditDTO>> editMessage(
            @Valid @RequestBody MessageEditDTO editDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserId(userDetails);
        logger.debug("User {} editing message {}", userId, editDTO.getMessageId());

        try {
            MessageEditDTO result = messageEditService.editMessage(editDTO, userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取消息的编辑历史
     */
    @GetMapping("/history/{messageId}")
    @Operation(summary = "获取编辑历史", description = "获取指定消息的完整编辑历史")
    public ResponseEntity<ApiResponse<MessageEditHistoryDTO>> getEditHistory(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        
        logger.debug("Getting edit history for message {}", messageId);
        MessageEditHistoryDTO history = messageEditService.getEditHistory(messageId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 分页获取编辑历史
     */
    @GetMapping("/history/{messageId}/page")
    @Operation(summary = "分页获取编辑历史", description = "分页获取消息的编辑历史")
    public ResponseEntity<ApiResponse<Page<MessageEditHistoryDTO.EditHistoryItem>>> getEditHistoryPage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("editSequence").descending());
        Page<MessageEditHistoryDTO.EditHistoryItem> result = messageEditService.getEditHistoryPage(messageId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 检查是否可以编辑
     */
    @GetMapping("/can-edit/{messageId}")
    @Operation(summary = "检查是否可以编辑", description = "检查当前用户是否可以编辑指定消息")
    public ResponseEntity<ApiResponse<CanEditResult>> canEditMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserId(userDetails);
        CanEditResult result = messageEditService.canEditMessage(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取消息的编辑次数
     */
    @GetMapping("/count/{messageId}")
    @Operation(summary = "获取编辑次数", description = "获取指定消息的编辑次数")
    public ResponseEntity<ApiResponse<Integer>> getEditCount(
            @Parameter(description = "消息ID") @PathVariable Long messageId) {
        
        int count = messageEditService.getEditCount(messageId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * 批量获取编辑次数
     */
    @PostMapping("/counts")
    @Operation(summary = "批量获取编辑次数", description = "批量获取多个消息的编辑次数")
    public ResponseEntity<ApiResponse<Map<Long, Integer>>> getEditCounts(
            @RequestBody List<Long> messageIds) {
        
        Map<Long, Integer> counts = messageEditService.getEditCounts(messageIds);
        return ResponseEntity.ok(ApiResponse.success(counts));
    }

    /**
     * 回滚到指定版本
     */
    @PostMapping("/revert/{messageId}")
    @Operation(summary = "回滚到指定版本", description = "将消息回滚到指定的编辑版本")
    public ResponseEntity<ApiResponse<MessageEditDTO>> revertToVersion(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "编辑序号") @RequestParam Integer sequence,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserId(userDetails);
        logger.debug("User {} reverting message {} to version {}", userId, messageId, sequence);

        try {
            MessageEditDTO result = messageEditService.revertToVersion(messageId, sequence, userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, e.getMessage()));
        }
    }

    /**
     * 获取当前用户的编辑历史
     */
    @GetMapping("/my-edits")
    @Operation(summary = "获取我的编辑历史", description = "获取当前用户的所有编辑记录")
    public ResponseEntity<ApiResponse<Page<MessageEditDTO>>> getMyEditHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long userId = extractUserId(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("editedAt").descending());
        Page<MessageEditDTO> result = messageEditService.getUserEditHistory(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ==================== 私有方法 ====================

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("用户未登录");
        }
        // 简化处理，实际应该从UserDetails中提取
        return Long.valueOf(userDetails.getUsername());
    }
}
