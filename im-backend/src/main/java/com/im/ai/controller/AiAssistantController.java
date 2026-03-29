package com.im.ai.controller;

import com.im.ai.model.*;
import com.im.ai.service.AiAssistantService;
import com.im.common.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * AI助手控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    @Autowired
    private AiAssistantService aiService;

    /**
     * 发送消息给AI助手
     */
    @PostMapping("/chat")
    public ApiResponse<AiResponse> chat(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody ChatRequest request) {
        
        log.info("AI聊天请求 - userId: {}, sessionId: {}", userId, sessionId);
        
        AiResponse response = aiService.processMessage(userId, sessionId, request.getMessage());
        
        return ApiResponse.success(response);
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    public ApiResponse<Page<AiConversation>> getHistory(
            @RequestHeader("X-User-Id") String userId,
            Pageable pageable) {
        
        Page<AiConversation> history = aiService.getConversationHistory(userId, pageable);
        
        return ApiResponse.success(history);
    }

    /**
     * 清除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public ApiResponse<Void> clearSession(
            @PathVariable String sessionId) {
        
        aiService.clearSession(sessionId);
        
        return ApiResponse.success(null);
    }

    /**
     * 获取会话统计
     */
    @GetMapping("/session/{sessionId}/stats")
    public ApiResponse<SessionStats> getSessionStats(
            @PathVariable String sessionId) {
        
        SessionStats stats = aiService.getSessionStats(sessionId);
        
        return ApiResponse.success(stats);
    }
}
