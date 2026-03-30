package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.service.MessageReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息引用回复控制器
 */
@RestController
@RequestMapping("/api/message-reply")
public class MessageReplyController {

    @Autowired
    private MessageReplyService replyService;

    /**
     * 发送引用回复
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ReplyMessageDTO>> sendReply(
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("username") String username,
            @RequestBody ReplyMessageRequest request) {
        
        ReplyMessageDTO reply = replyService.sendReply(userId, username, request);
        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    /**
     * 获取消息的引用回复列表
     */
    @GetMapping("/original/{originalMessageId}")
    public ResponseEntity<ApiResponse<List<ReplyMessageDTO>>> getRepliesByOriginalMessage(
            @PathVariable Long originalMessageId) {
        
        List<ReplyMessageDTO> replies = replyService.getRepliesByOriginalMessage(originalMessageId);
        return ResponseEntity.ok(ApiResponse.success(replies));
    }

    /**
     * 获取会话中的引用回复
     */
    @GetMapping("/conversation/{conversationType}/{conversationId}")
    public ResponseEntity<ApiResponse<Page<ReplyMessageDTO>>> getRepliesByConversation(
            @PathVariable String conversationType,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ReplyMessageDTO> replies = replyService.getRepliesByConversation(
            conversationType, conversationId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(replies));
    }

    /**
     * 获取回复详情（包含嵌套回复）
     */
    @GetMapping("/{replyId}")
    public ResponseEntity<ApiResponse<ReplyMessageDTO>> getReplyDetail(
            @PathVariable Long replyId) {
        
        ReplyMessageDTO reply = replyService.getReplyWithNested(replyId);
        return ResponseEntity.ok(ApiResponse.success(reply));
    }

    /**
     * 获取消息的所有相关回复
     */
    @GetMapping("/related/{messageId}")
    public ResponseEntity<ApiResponse<List<ReplyMessageDTO>>> getAllRelatedReplies(
            @PathVariable Long messageId) {
        
        List<ReplyMessageDTO> replies = replyService.getAllRelatedReplies(messageId);
        return ResponseEntity.ok(ApiResponse.success(replies));
    }

    /**
     * 删除引用回复
     */
    @DeleteMapping("/{replyId}")
    public ResponseEntity<ApiResponse<Void>> deleteReply(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long replyId) {
        
        replyService.deleteReply(replyId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 跳转到原消息
     */
    @GetMapping("/{replyId}/original")
    public ResponseEntity<ApiResponse<Map<String, Object>>> jumpToOriginal(
            @PathVariable Long replyId) {
        
        Long originalMessageId = replyService.getOriginalMessageId(replyId);
        boolean isReply = replyService.isReplyMessage(replyId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalMessageId", originalMessageId);
        result.put("isReplyMessage", isReply);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 获取回复统计
     */
    @GetMapping("/count/{originalMessageId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getReplyCount(
            @PathVariable Long originalMessageId) {
        
        long count = replyService.getReplyCount(originalMessageId);
        Map<String, Long> result = new HashMap<>();
        result.put("replyCount", count);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
