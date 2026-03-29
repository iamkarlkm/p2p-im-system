package com.im.modules.merchant.automation.controller;

import com.im.common.dto.Result;
import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 智能客服机器人控制器
 */
@RestController
@RequestMapping("/api/v1/merchant/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    
    private final IChatbotService chatbotService;
    
    /**
     * 发送消息给AI客服
     */
    @PostMapping("/message")
    public Result<ChatbotMessageResponse> sendMessage(@Valid @RequestBody ChatbotMessageRequest request) {
        ChatbotMessageResponse response = chatbotService.processMessage(request);
        return Result.success(response);
    }
    
    /**
     * 智能转人工
     */
    @PostMapping("/transfer")
    public Result<TransferToHumanResponse> transferToHuman(@Valid @RequestBody TransferToHumanRequest request) {
        TransferToHumanResponse response = chatbotService.transferToHuman(request);
        return Result.success(response);
    }
    
    /**
     * 获取会话历史
     */
    @GetMapping("/session/{sessionId}/history")
    public Result<ChatSessionHistoryResponse> getSessionHistory(@PathVariable String sessionId) {
        ChatSessionHistoryResponse response = chatbotService.getSessionHistory(sessionId);
        return Result.success(response);
    }
    
    /**
     * 获取商户活跃会话列表
     */
    @GetMapping("/merchant/{merchantId}/active-sessions")
    public Result<List<ChatSessionHistoryResponse>> getActiveSessions(@PathVariable String merchantId) {
        List<ChatSessionHistoryResponse> sessions = chatbotService.getActiveSessions(merchantId);
        return Result.success(sessions);
    }
    
    /**
     * 关闭会话
     */
    @PostMapping("/session/{sessionId}/close")
    public Result<Void> closeSession(@PathVariable String sessionId) {
        chatbotService.closeSession(sessionId);
        return Result.success();
    }
    
    /**
     * 获取待转人工列表
     */
    @GetMapping("/merchant/{merchantId}/pending-transfers")
    public Result<List<TransferToHumanResponse>> getPendingTransfers(@PathVariable String merchantId) {
        List<TransferToHumanResponse> transfers = chatbotService.getPendingTransfers(merchantId);
        return Result.success(transfers);
    }
    
    /**
     * 人工客服接入
     */
    @PostMapping("/transfer/{transferId}/accept")
    public Result<Void> acceptTransfer(@PathVariable String transferId, @RequestParam String agentId) {
        chatbotService.acceptTransfer(transferId, agentId);
        return Result.success();
    }
}
