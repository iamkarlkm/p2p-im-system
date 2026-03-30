package com.im.message.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.common.dto.ApiResponse;
import com.im.common.dto.PageResult;
import com.im.message.dto.MessageQueryRequest;
import com.im.message.dto.MessageRecallRequest;
import com.im.message.dto.MessageSearchResponse;
import com.im.message.service.MessageSearchService;
import com.im.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息控制器 - 消息存储与检索REST API
 * 
 * 功能: 消息查询、搜索、撤回、已读标记
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Validated
@Tag(name = "消息管理", description = "消息存储、检索、撤回相关接口")
public class MessageController {
    
    private final MessageService messageService;
    private final MessageSearchService messageSearchService;
    
    /**
     * 查询会话历史消息
     */
    @GetMapping("/history/{conversationType}/{conversationId}")
    @Operation(summary = "查询会话历史消息", description = "支持分页、时间范围、发送者等条件查询")
    public ApiResponse<PageResult<MessageSearchResponse>> queryHistoryMessages(
            @Parameter(description = "会话类型: 1-单聊, 2-群聊") 
            @PathVariable Integer conversationType,
            @Parameter(description = "会话ID") 
            @PathVariable Long conversationId,
            @Parameter(description = "游标分页") 
            @RequestParam(required = false) Long cursor,
            @Parameter(description = "每页数量") 
            @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "查询方向: true-向前查询(更新的)") 
            @RequestParam(defaultValue = "false") Boolean forward) {
        
        log.info("查询历史消息: conversationType={}, conversationId={}, cursor={}, pageSize={}", 
                conversationType, conversationId, cursor, pageSize);
        
        MessageQueryRequest request = MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .cursor(cursor)
                .pageSize(pageSize)
                .forward(forward)
                .build();
        
        List<MessageSearchResponse> messages = messageService.queryHistoryMessages(request);
        return ApiResponse.success(PageResult.of(messages, cursor, pageSize));
    }
    
    /**
     * 关键词搜索消息
     */
    @GetMapping("/search")
    @Operation(summary = "关键词搜索消息", description = "全文检索消息内容")
    public ApiResponse<List<MessageSearchResponse>> searchMessages(
            @Parameter(description = "会话类型") @RequestParam Integer conversationType,
            @Parameter(description = "会话ID") @RequestParam Long conversationId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "50") Integer limit) {
        
        log.info("搜索消息: conversationType={}, conversationId={}, keyword={}", 
                conversationType, conversationId, keyword);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.error("搜索关键词不能为空");
        }
        
        MessageQueryRequest request = MessageQueryRequest.builder()
                .conversationId(conversationId)
                .conversationType(conversationType)
                .keyword(keyword.trim())
                .pageSize(limit)
                .build();
        
        List<MessageSearchResponse> results = messageSearchService.searchMessages(request);
        return ApiResponse.success(results);
    }
    
    /**
     * 撤回消息
     */
    @PostMapping("/recall")
    @Operation(summary = "撤回消息", description = "撤回已发送的消息(2分钟内可撤回)")
    public ApiResponse<Boolean> recallMessage(
            @Valid @RequestBody MessageRecallRequest request) {
        
        log.info("撤回消息: messageId={}, operatorId={}", request.getMessageId(), request.getOperatorId());
        
        boolean success = messageService.recallMessage(request);
        if (success) {
            return ApiResponse.success(true, "消息撤回成功");
        } else {
            return ApiResponse.error("消息撤回失败，可能已超过撤回时限");
        }
    }
    
    /**
     * 标记消息已读
     */
    @PostMapping("/read/{messageId}")
    @Operation(summary = "标记消息已读", description = "将指定消息标记为已读状态")
    public ApiResponse<Boolean> markMessageAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        
        log.info("标记消息已读: messageId={}, userId={}", messageId, userId);
        
        boolean success = messageService.markMessageAsRead(messageId, userId);
        return ApiResponse.success(success);
    }
    
    /**
     * 批量标记已读
     */
    @PostMapping("/read/batch")
    @Operation(summary = "批量标记已读", description = "批量将消息标记为已读")
    public ApiResponse<Integer> batchMarkAsRead(
            @RequestParam List<Long> messageIds,
            @RequestParam Long userId) {
        
        log.info("批量标记已读: messageIds count={}, userId={}", messageIds.size(), userId);
        
        int count = messageService.batchMarkAsRead(messageIds, userId);
        return ApiResponse.success(count, "已标记 " + count + " 条消息为已读");
    }
    
    /**
     * 查询@我的消息
     */
    @GetMapping("/mentions/{conversationType}/{conversationId}")
    @Operation(summary = "查询@我的消息", description = "查询会话中@指定用户的消息")
    public ApiResponse<List<MessageSearchResponse>> queryMentionMessages(
            @PathVariable Integer conversationType,
            @PathVariable Long conversationId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        log.info("查询@我的消息: conversationType={}, conversationId={}, userId={}", 
                conversationType, conversationId, userId);
        
        MessageQueryRequest request = MessageQueryRequest.forMentions(conversationId, conversationType, userId);
        request.setPageSize(limit);
        
        List<MessageSearchResponse> messages = messageService.queryMentionMessages(request);
        return ApiResponse.success(messages);
    }
    
    /**
     * 查询带附件的消息
     */
    @GetMapping("/attachments/{conversationType}/{conversationId}")
    @Operation(summary = "查询带附件的消息", description = "查询会话中包含附件的消息")
    public ApiResponse<List<MessageSearchResponse>> queryMessagesWithAttachment(
            @PathVariable Integer conversationType,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        log.info("查询附件消息: conversationType={}, conversationId={}", conversationType, conversationId);
        
        MessageQueryRequest request = MessageQueryRequest.forAttachments(conversationId, conversationType);
        request.setPageSize(limit);
        
        List<MessageSearchResponse> messages = messageService.queryMessagesWithAttachment(request);
        return ApiResponse.success(messages);
    }
    
    /**
     * 获取会话未读数量
     */
    @GetMapping("/unread/count/{conversationType}/{conversationId}")
    @Operation(summary = "获取会话未读数量", description = "获取指定会话的未读消息数量")
    public ApiResponse<Integer> getUnreadCount(
            @PathVariable Integer conversationType,
            @PathVariable Long conversationId) {
        
        int count = messageService.getUnreadCount(conversationType, conversationId);
        return ApiResponse.success(count);
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "软删除指定消息")
    public ApiResponse<Boolean> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        
        log.info("删除消息: messageId={}, userId={}", messageId, userId);
        
        boolean success = messageService.deleteMessage(messageId, userId);
        return ApiResponse.success(success);
    }
    
    /**
     * 重建搜索索引
     */
    @PostMapping("/index/rebuild/{conversationId}")
    @Operation(summary = "重建搜索索引", description = "为指定会话重建全文搜索索引")
    public ApiResponse<Integer> rebuildIndex(
            @PathVariable Long conversationId) {
        
        log.info("重建搜索索引: conversationId={}", conversationId);
        
        int count = messageSearchService.rebuildIndex(conversationId);
        return ApiResponse.success(count, "已重建 " + count + " 条消息的索引");
    }
}
