package com.im.backend.modules.appointment.controller;

import com.im.backend.common.core.result.Result;
import com.im.backend.modules.appointment.dto.*;
import com.im.backend.modules.appointment.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排队叫号Controller
 * 本地生活服务预约与排班管理系统
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/queues")
@RequiredArgsConstructor
@Tag(name = "排队叫号", description = "排队叫号相关接口")
public class QueueController {

    private final QueueService queueService;

    @GetMapping("/list")
    @Operation(summary = "获取队列列表", description = "获取门店的排队队列列表")
    public Result<List<QueueInfoResponse>> getQueueList(
            @RequestParam Long merchantId,
            @RequestParam Long storeId) {
        return Result.success(queueService.getQueueList(merchantId, storeId));
    }

    @PostMapping
    @Operation(summary = "创建队列", description = "创建新的排队队列")
    public Result<QueueInfoResponse> createQueue(
            @RequestBody @Validated CreateQueueRequest request) {
        return Result.success(queueService.createQueue(request));
    }

    @PutMapping("/{queueId}")
    @Operation(summary = "更新队列", description = "更新队列信息")
    public Result<QueueInfoResponse> updateQueue(
            @PathVariable Long queueId,
            @RequestBody @Validated UpdateQueueRequest request) {
        return Result.success(queueService.updateQueue(queueId, request));
    }

    @DeleteMapping("/{queueId}")
    @Operation(summary = "删除队列", description = "删除排队队列")
    public Result<Boolean> deleteQueue(@PathVariable Long queueId) {
        return Result.success(queueService.deleteQueue(queueId));
    }

    @PostMapping("/take-number/onsite")
    @Operation(summary = "现场取号", description = "现场扫码或设备取号")
    public Result<TakeNumberResponse> takeNumberOnsite(
            @RequestBody @Validated TakeNumberRequest request) {
        return Result.success(queueService.takeNumberOnsite(request));
    }

    @PostMapping("/take-number/online")
    @Operation(summary = "在线取号", description = "用户在线远程取号")
    public Result<TakeNumberResponse> takeNumberOnline(
            @RequestBody @Validated TakeNumberRequest request) {
        return Result.success(queueService.takeNumberOnline(request));
    }

    @PostMapping("/take-number/appointment/{appointmentId}")
    @Operation(summary = "预约取号", description = "根据预约自动取号")
    public Result<TakeNumberResponse> takeNumberByAppointment(
            @PathVariable Long appointmentId) {
        return Result.success(queueService.takeNumberByAppointment(appointmentId));
    }

    @GetMapping("/records/{recordId}")
    @Operation(summary = "获取取号详情", description = "获取排队记录详情")
    public Result<QueueRecordDetailResponse> getQueueRecord(@PathVariable Long recordId) {
        return Result.success(queueService.getQueueRecord(recordId));
    }

    @GetMapping("/user/records")
    @Operation(summary = "获取用户排队记录", description = "获取当前用户的排队记录")
    public Result<List<QueueRecordResponse>> getUserQueueRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        return Result.success(queueService.getUserQueueRecords(userId, status));
    }

    @GetMapping("/{queueId}/status")
    @Operation(summary = "获取队列状态", description = "获取队列当前排队情况")
    public Result<QueueStatusResponse> getQueueStatus(@PathVariable Long queueId) {
        return Result.success(queueService.getQueueStatus(queueId));
    }

    @PostMapping("/{queueId}/call")
    @Operation(summary = "叫号", description = "呼叫下一个或多个号码")
    public Result<List<QueueRecordResponse>> callNumbers(
            @PathVariable Long queueId,
            @RequestParam(defaultValue = "1") Integer count) {
        return Result.success(queueService.callNumbers(queueId, count));
    }

    @PostMapping("/records/{recordId}/confirm")
    @Operation(summary = "确认到店", description = "用户确认到店")
    public Result<Boolean> confirmArrival(@PathVariable Long recordId) {
        return Result.success(queueService.confirmArrival(recordId));
    }

    @PostMapping("/records/{recordId}/start")
    @Operation(summary = "开始服务", description = "标记开始服务")
    public Result<Boolean> startService(
            @PathVariable Long recordId,
            @RequestParam Integer window) {
        return Result.success(queueService.startService(recordId, window));
    }

    @PostMapping("/records/{recordId}/complete")
    @Operation(summary = "完成服务", description = "标记服务完成")
    public Result<Boolean> completeService(@PathVariable Long recordId) {
        return Result.success(queueService.completeService(recordId));
    }

    @PostMapping("/records/{recordId}/pass")
    @Operation(summary = "标记过号", description = "标记号码已过")
    public Result<Boolean> markPassed(
            @PathVariable Long recordId,
            @RequestParam(required = false) String reason) {
        return Result.success(queueService.markPassed(recordId, reason));
    }

    @PostMapping("/records/{recordId}/cancel")
    @Operation(summary = "取消排队", description = "取消排队取号")
    public Result<Boolean> cancelQueue(
            @PathVariable Long recordId,
            @RequestParam(required = false) String reason) {
        return Result.success(queueService.cancelQueue(recordId, reason));
    }

    @PostMapping("/records/{recordId}/requeue")
    @Operation(summary = "重新取号", description = "过号后重新取号")
    public Result<TakeNumberResponse> requeue(@PathVariable Long recordId) {
        return Result.success(queueService.requeue(recordId));
    }

    @PostMapping("/{queueId}/clear")
    @Operation(summary = "清空队列", description = "清空当前队列所有号码")
    public Result<Boolean> clearQueue(@PathVariable Long queueId) {
        return Result.success(queueService.clearQueue(queueId));
    }

    @PostMapping("/{queueId}/pause")
    @Operation(summary = "暂停/恢复队列", description = "暂停或恢复队列取号")
    public Result<Boolean> pauseQueue(
            @PathVariable Long queueId,
            @RequestParam Boolean paused) {
        return Result.success(queueService.pauseQueue(queueId, paused));
    }

    @GetMapping("/merchant/statistics")
    @Operation(summary = "获取排队统计", description = "获取商户排队统计数据")
    public Result<QueueStatisticsResponse> getQueueStatistics(@RequestParam Long merchantId) {
        return Result.success(queueService.getQueueStatistics(merchantId));
    }

    @GetMapping("/records/{recordId}/progress")
    @Operation(summary = "获取排队进度", description = "获取实时排队进度")
    public Result<QueueProgressResponse> getQueueProgress(@PathVariable Long recordId) {
        return Result.success(queueService.getQueueProgress(recordId));
    }

    @GetMapping("/{queueId}/current-call")
    @Operation(summary = "获取当前叫号", description = "获取队列当前叫号信息")
    public Result<CurrentCallResponse> getCurrentCall(@PathVariable Long queueId) {
        return Result.success(queueService.getCurrentCall(queueId));
    }
}
