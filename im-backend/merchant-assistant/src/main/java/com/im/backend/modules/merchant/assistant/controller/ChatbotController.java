package com.im.backend.modules.merchant.assistant.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.assistant.dto.*;
import com.im.backend.modules.merchant.assistant.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能客服控制器
 */
@RestController
@RequestMapping("/api/v1/assistant/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    
    private final IChatbotService chatbotService;
    
    /**
     * 创建客服会话
     */
    @PostMapping("/session/create")
    public Result<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        SessionResponse response = chatbotService.createSession(request);
        return Result.success(response);
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/message/send")
    public Result<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        MessageResponse response = chatbotService.sendMessage(request);
        return Result.success(response);
    }
    
    /**
     * 获取智能回复
     */
    @PostMapping("/reply")
    public Result<ChatbotReplyResponse> getReply(@RequestBody ChatbotReplyRequest request) {
        ChatbotReplyResponse response = chatbotService.getReply(request);
        return Result.success(response);
    }
    
    /**
     * 转人工服务
     */
    @PostMapping("/session/{sessionId}/transfer")
    public Result<Void> transferToAgent(@PathVariable String sessionId, @RequestParam Long agentId) {
        chatbotService.transferToAgent(sessionId, agentId);
        return Result.success();
    }
    
    /**
     * 结束会话
     */
    @PostMapping("/session/{sessionId}/end")
    public Result<Void> endSession(@PathVariable String sessionId, 
                                    @RequestParam(required = false) Integer rating,
                                    @RequestParam(required = false) String satisfaction) {
        chatbotService.endSession(sessionId, rating, satisfaction);
        return Result.success();
    }
    
    /**
     * 获取会话消息历史
     */
    @GetMapping("/session/{sessionId}/messages")
    public Result<List<MessageResponse>> getSessionMessages(@PathVariable String sessionId) {
        List<MessageResponse> messages = chatbotService.getSessionMessages(sessionId);
        return Result.success(messages);
    }
    
    /**
     * 获取商户待处理会话列表
     */
    @GetMapping("/merchant/{merchantId}/pending-sessions")
    public Result<List<SessionResponse>> getPendingSessions(@PathVariable Long merchantId) {
        List<SessionResponse> sessions = chatbotService.getPendingSessions(merchantId);
        return Result.success(sessions);
    }
}
