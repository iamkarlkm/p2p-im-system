package com.im.backend.modules.appointment.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排队叫号控制器
 * 处理远程取号、叫号、排队查询等操作
 */
@RestController
@RequestMapping("/api/v1/queue")
@Tag(name = "排队叫号", description = "排队叫号系统")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/take")
    @Operation(summary = "远程取号")
    public ApiResponse<QueueTicketDetailDTO> takeQueue(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TakeQueueRequestDTO request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        QueueTicketDetailDTO result = queueService.takeQueue(userId, request);
        return ApiResponse.success(result);
    }

    @PostMapping("/{ticketId}/cancel")
    @Operation(summary = "取消排队")
    public ApiResponse<QueueTicketDetailDTO> cancelQueue(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long ticketId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        QueueTicketDetailDTO result = queueService.cancelQueue(userId, ticketId);
        return ApiResponse.success(result);
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "获取排队详情")
    public ApiResponse<QueueTicketDetailDTO> getQueueDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long ticketId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        QueueTicketDetailDTO result = queueService.getQueueDetail(userId, ticketId);
        return ApiResponse.success(result);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的排队列表")
    public ApiResponse<List<QueueTicketDetailDTO>> getMyQueues(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<QueueTicketDetailDTO> result = queueService.getMyQueues(userId, status);
        return ApiResponse.success(result);
    }

    @PostMapping("/merchant/call-next")
    @Operation(summary = "商家叫号")
    public ApiResponse<QueueTicketDetailDTO> callNext(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String queueType) {
        Long merchantId = Long.parseLong(userDetails.getUsername());
        QueueTicketDetailDTO result = queueService.callNext(merchantId, queueType);
        return ApiResponse.success(result);
    }

    @PostMapping("/{ticketId}/arrive")
    @Operation(summary = "用户确认到达")
    public ApiResponse<QueueTicketDetailDTO> confirmArrive(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long ticketId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        QueueTicketDetailDTO result = queueService.confirmArrive(userId, ticketId);
        return ApiResponse.success(result);
    }

    @GetMapping("/merchant/{merchantId}/status")
    @Operation(summary = "获取商户排队状态")
    public ApiResponse<List<QueueStatusDTO>> getMerchantQueueStatus(
            @PathVariable Long merchantId) {
        List<QueueStatusDTO> result = queueService.getMerchantQueueStatus(merchantId);
        return ApiResponse.success(result);
    }

    @GetMapping("/merchant/{merchantId}/queue-types")
    @Operation(summary = "获取商户各队列等待人数")
    public ApiResponse<List<QueueTypeStatusDTO>> getQueueTypeStatus(
            @PathVariable Long merchantId) {
        List<QueueTypeStatusDTO> result = queueService.getQueueTypeStatus(merchantId);
        return ApiResponse.success(result);
    }
}
