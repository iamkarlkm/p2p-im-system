package com.im.backend.modules.poi.customer_service.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.common.security.CurrentUser;
import com.im.backend.common.security.UserPrincipal;
import com.im.backend.modules.poi.customer_service.dto.*;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerService;
import com.im.backend.modules.poi.customer_service.service.IPoiCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * POI智能客服控制器
 */
@Tag(name = "POI智能客服", description = "POI商家客服IM接入与智能客服机器人")
@RestController
@RequestMapping("/api/v1/poi/cs")
@RequiredArgsConstructor
public class PoiCustomerServiceController {

    private final IPoiCustomerService customerService;

    /**
     * 创建客服会话
     */
    @Operation(summary = "创建客服会话")
    @PostMapping("/sessions")
    public ApiResponse<SessionResponse> createSession(
            @CurrentUser UserPrincipal user,
            @RequestBody @Validated CreateSessionRequest request) {
        SessionResponse response = customerService.createSession(user.getId(), request);
        return ApiResponse.success(response);
    }

    /**
     * 发送消息
     */
    @Operation(summary = "发送消息")
    @PostMapping("/messages")
    public ApiResponse<MessageResponse> sendMessage(
            @CurrentUser UserPrincipal user,
            @RequestBody @Validated SendMessageRequest request) {
        MessageResponse response = customerService.sendMessage(user.getId(), "USER", request);
        return ApiResponse.success(response);
    }

    /**
     * 客服发送消息
     */
    @Operation(summary = "客服发送消息")
    @PostMapping("/agent/messages")
    public ApiResponse<MessageResponse> agentSendMessage(
            @CurrentUser UserPrincipal user,
            @RequestBody @Validated SendMessageRequest request) {
        MessageResponse response = customerService.sendMessage(user.getId(), "AGENT", request);
        return ApiResponse.success(response);
    }

    /**
     * 获取用户会话列表
     */
    @Operation(summary = "获取用户会话列表")
    @GetMapping("/sessions")
    public ApiResponse<List<SessionResponse>> getUserSessions(@CurrentUser UserPrincipal user) {
        List<SessionResponse> sessions = customerService.getUserSessions(user.getId());
        return ApiResponse.success(sessions);
    }

    /**
     * 获取客服会话列表
     */
    @Operation(summary = "获取客服会话列表")
    @GetMapping("/agent/sessions")
    public ApiResponse<List<SessionResponse>> getAgentSessions(@CurrentUser UserPrincipal user) {
        List<SessionResponse> sessions = customerService.getAgentSessions(user.getId());
        return ApiResponse.success(sessions);
    }

    /**
     * 获取会话消息历史
     */
    @Operation(summary = "获取会话消息历史")
    @GetMapping("/sessions/{sessionId}/messages")
    public ApiResponse<List<MessageResponse>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<MessageResponse> messages = customerService.getSessionMessages(sessionId, page, size);
        return ApiResponse.success(messages);
    }

    /**
     * 标记消息已读
     */
    @Operation(summary = "标记消息已读")
    @PostMapping("/sessions/{sessionId}/read")
    public ApiResponse<Void> markMessagesAsRead(
            @PathVariable String sessionId,
            @RequestParam String readerType) {
        customerService.markMessagesAsRead(sessionId, readerType);
        return ApiResponse.success();
    }

    /**
     * 关闭会话
     */
    @Operation(summary = "关闭会话")
    @PostMapping("/sessions/{sessionId}/close")
    public ApiResponse<Void> closeSession(
            @PathVariable String sessionId,
            @RequestParam String closeReason) {
        customerService.closeSession(sessionId, closeReason);
        return ApiResponse.success();
    }

    /**
     * 撤回消息
     */
    @Operation(summary = "撤回消息")
    @PostMapping("/messages/{messageId}/recall")
    public ApiResponse<Void> recallMessage(
            @CurrentUser UserPrincipal user,
            @PathVariable String messageId) {
        customerService.recallMessage(messageId, user.getId());
        return ApiResponse.success();
    }

    /**
     * 评价会话
     */
    @Operation(summary = "评价会话")
    @PostMapping("/sessions/{sessionId}/rate")
    public ApiResponse<Void> rateSession(
            @PathVariable String sessionId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        customerService.rateSession(sessionId, rating, comment);
        return ApiResponse.success();
    }

    /**
     * 转接人工客服
     */
    @Operation(summary = "转接人工客服")
    @PostMapping("/sessions/{sessionId}/transfer")
    public ApiResponse<Void> transferToHuman(
            @PathVariable String sessionId,
            @RequestParam String reason) {
        customerService.transferToHuman(sessionId, reason);
        return ApiResponse.success();
    }

    /**
     * 获取POI客服列表
     */
    @Operation(summary = "获取POI客服列表")
    @GetMapping("/poi/{poiId}/agents")
    public ApiResponse<List<PoiCustomerService>> getPoiAgents(@PathVariable Long poiId) {
        List<PoiCustomerService> agents = customerService.getPoiAgents(poiId);
        return ApiResponse.success(agents);
    }

    /**
     * 更新客服状态
     */
    @Operation(summary = "更新客服状态")
    @PostMapping("/agent/status")
    public ApiResponse<Void> updateAgentStatus(
            @CurrentUser UserPrincipal user,
            @RequestParam String status) {
        customerService.updateAgentStatus(user.getId(), status);
        return ApiResponse.success();
    }

    /**
     * 客服心跳
     */
    @Operation(summary = "客服心跳")
    @PostMapping("/agent/heartbeat")
    public ApiResponse<Void> agentHeartbeat(@CurrentUser UserPrincipal user) {
        customerService.agentHeartbeat(user.getId());
        return ApiResponse.success();
    }
}
