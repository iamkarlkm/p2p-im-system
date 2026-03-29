package com.im.controller.customer_service;

import com.im.dto.customer_service.*;
import com.im.service.customer_service.CustomerServiceBotService;
import com.im.service.customer_service.CustomerServiceSessionService;
import com.im.service.customer_service.CustomerServiceTicketService;
import com.im.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客服工单管理控制器
 * 功能 #319 - 智能客服与工单管理系统
 */
@RestController
@RequestMapping("/api/v1/cs")
@RequiredArgsConstructor
public class CustomerServiceController {
    
    private final CustomerServiceTicketService ticketService;
    private final CustomerServiceSessionService sessionService;
    private final CustomerServiceBotService botService;
    
    // ==================== 工单管理接口 ====================
    
    @PostMapping("/ticket/create")
    public Result<TicketResponse> createTicket(@RequestBody CreateTicketRequest request) {
        return Result.success(ticketService.createTicket(request));
    }
    
    @GetMapping("/ticket/{ticketId}")
    public Result<TicketResponse> getTicket(@PathVariable Long ticketId) {
        return Result.success(ticketService.getTicketById(ticketId));
    }
    
    @PutMapping("/ticket/{ticketId}")
    public Result<TicketResponse> updateTicket(@PathVariable Long ticketId, 
                                                @RequestBody CreateTicketRequest request) {
        return Result.success(ticketService.updateTicket(ticketId, request));
    }
    
    @PostMapping("/ticket/{ticketId}/assign")
    public Result<TicketResponse> assignTicket(@PathVariable Long ticketId, 
                                                @RequestParam Long agentId) {
        return Result.success(ticketService.assignTicket(ticketId, agentId));
    }
    
    @PostMapping("/ticket/{ticketId}/process")
    public Result<TicketResponse> processTicket(@PathVariable Long ticketId,
                                                 @RequestParam String content,
                                                 @RequestParam Long agentId) {
        return Result.success(ticketService.processTicket(ticketId, content, agentId));
    }
    
    @PostMapping("/ticket/{ticketId}/close")
    public Result<TicketResponse> closeTicket(@PathVariable Long ticketId,
                                               @RequestParam String reason,
                                               @RequestParam Long operatorId) {
        return Result.success(ticketService.closeTicket(ticketId, reason, operatorId));
    }
    
    @PostMapping("/ticket/{ticketId}/escalate")
    public Result<TicketResponse> escalateTicket(@PathVariable Long ticketId,
                                                  @RequestParam Integer newPriority,
                                                  @RequestParam String reason) {
        return Result.success(ticketService.escalateTicket(ticketId, newPriority, reason));
    }
    
    @GetMapping("/ticket/list")
    public Result<List<TicketResponse>> getTicketList(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(ticketService.getTicketList(userId, status, page, size));
    }
    
    @GetMapping("/ticket/stats")
    public Result<TicketStatisticsResponse> getTicketStats(@RequestParam(required = false) Long agentId,
                                                            @RequestParam(required = false) String dateRange) {
        return Result.success(ticketService.getTicketStatistics(agentId, dateRange));
    }
    
    // ==================== 会话管理接口 ====================
    
    @PostMapping("/session/start")
    public Result<SessionResponse> startSession(@RequestBody StartSessionRequest request) {
        return Result.success(sessionService.startSession(request));
    }
    
    @PostMapping("/session/{sessionId}/end")
    public Result<SessionResponse> endSession(@PathVariable Long sessionId,
                                               @RequestParam Long operatorId) {
        return Result.success(sessionService.endSession(sessionId, operatorId));
    }
    
    @GetMapping("/session/{sessionId}")
    public Result<SessionResponse> getSession(@PathVariable Long sessionId) {
        return Result.success(sessionService.getSessionById(sessionId));
    }
    
    @GetMapping("/session/user/{userId}")
    public Result<List<SessionResponse>> getUserSessions(@PathVariable Long userId,
                                                          @RequestParam(required = false) Integer status) {
        return Result.success(sessionService.getUserSessions(userId, status));
    }
    
    @GetMapping("/session/agent/{agentId}")
    public Result<List<SessionResponse>> getAgentSessions(@PathVariable Long agentId,
                                                           @RequestParam(required = false) Integer status) {
        return Result.success(sessionService.getAgentSessions(agentId, status));
    }
    
    @PostMapping("/session/{sessionId}/transfer")
    public Result<SessionResponse> transferToHuman(@PathVariable Long sessionId,
                                                    @RequestParam Long agentId,
                                                    @RequestParam String reason) {
        return Result.success(sessionService.transferToHuman(sessionId, agentId, reason));
    }
    
    @PostMapping("/message/send")
    public Result<MessageResponse> sendMessage(@RequestBody SendMessageRequest request) {
        return Result.success(sessionService.sendMessage(request));
    }
    
    @GetMapping("/message/list")
    public Result<List<MessageResponse>> getMessages(@RequestParam Long sessionId,
                                                      @RequestParam(required = false) Long lastMessageId,
                                                      @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(sessionService.getSessionMessages(sessionId, lastMessageId, size));
    }
    
    @PostMapping("/satisfaction/submit")
    public Result<Void> submitSatisfaction(@RequestParam Long sessionId,
                                            @RequestParam Integer score,
                                            @RequestParam(required = false) String comment) {
        sessionService.submitSatisfaction(sessionId, score, comment);
        return Result.success(null);
    }
    
    // ==================== 智能客服机器人接口 ====================
    
    @PostMapping("/bot/chat")
    public Result<BotChatResponse> botChat(@RequestBody BotChatRequest request) {
        return Result.success(botService.chat(request));
    }
    
    @PostMapping("/bot/intent")
    public Result<String> recognizeIntent(@RequestParam String message) {
        return Result.success(botService.recognizeIntent(message));
    }
    
    @GetMapping("/bot/knowledge")
    public Result<List<BotChatResponse.KnowledgeItem>> searchKnowledge(@RequestParam String query,
                                                                        @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(botService.searchKnowledge(query, limit));
    }
    
    @PostMapping("/bot/feedback")
    public Result<Void> feedbackKnowledge(@RequestParam Long knowledgeId,
                                           @RequestParam Boolean isHelpful,
                                           @RequestParam Long userId) {
        botService.feedbackKnowledge(knowledgeId, isHelpful, userId);
        return Result.success(null);
    }
}
