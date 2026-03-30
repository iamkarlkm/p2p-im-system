package com.im.backend.modules.merchant.dispatch.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.dispatch.entity.DeliveryCapacityResource;
import com.im.backend.modules.merchant.dispatch.entity.DispatchTask;
import com.im.backend.modules.merchant.dispatch.service.IDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 运力调度控制器
 * Feature #309: Instant Delivery Capacity Dispatch
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/merchant/dispatch")
@Tag(name = "即时配送运力调度", description = "Instant Delivery Capacity Dispatch APIs")
public class DispatchController {

    @Autowired
    private IDispatchService dispatchService;

    /**
     * 创建调度任务(智能派单)
     */
    @PostMapping("/task")
    @Operation(summary = "创建调度任务", description = "创建配送调度任务并智能分配骑手")
    public Result<Long> createDispatchTask(@RequestBody DispatchTask task) {
        log.info("Creating dispatch task for order: {}", task.getOrderId());
        Long riderId = dispatchService.smartDispatch(task);
        return Result.success(riderId);
    }

    /**
     * 骑手抢单
     */
    @PostMapping("/task/{taskId}/grab")
    @Operation(summary = "骑手抢单", description = "骑手抢单接口")
    @Parameter(name = "taskId", description = "任务ID", required = true)
    public Result<Boolean> grabOrder(
            @PathVariable Long taskId,
            @RequestParam Long riderId) {
        log.info("Rider: {} grabbing order: {}", riderId, taskId);
        Boolean success = dispatchService.grabOrder(taskId, riderId);
        return Result.success(success);
    }

    /**
     * 骑手接单
     */
    @PostMapping("/task/{taskId}/accept")
    @Operation(summary = "骑手接单", description = "骑手确认接单")
    @Parameter(name = "taskId", description = "任务ID", required = true)
    public Result<Boolean> acceptOrder(
            @PathVariable Long taskId,
            @RequestParam Long riderId) {
        log.info("Rider: {} accepting order: {}", riderId, taskId);
        Boolean success = dispatchService.acceptOrder(taskId, riderId);
        return Result.success(success);
    }

    /**
     * 完成配送
     */
    @PostMapping("/task/{taskId}/complete")
    @Operation(summary = "完成配送", description = "确认订单配送完成")
    @Parameter(name = "taskId", description = "任务ID", required = true)
    public Result<Boolean> completeOrder(@PathVariable Long taskId) {
        log.info("Completing order: {}", taskId);
        Boolean success = dispatchService.completeOrder(taskId);
        return Result.success(success);
    }

    /**
     * 获取附近可用骑手
     */
    @GetMapping("/riders/nearby")
    @Operation(summary = "获取附近骑手", description = "获取指定位置附近的可用骑手")
    public Result<List<DeliveryCapacityResource>> getNearbyRiders(
            @RequestParam Double lng,
            @RequestParam Double lat,
            @RequestParam(required = false, defaultValue = "5000") Double radius) {
        log.info("Getting nearby riders at: {}, {} within {} meters", lng, lat, radius);
        List<DeliveryCapacityResource> riders = dispatchService.getNearbyAvailableRiders(lng, lat, radius);
        return Result.success(riders);
    }

    /**
     * 更新骑手位置
     */
    @PostMapping("/rider/{riderId}/location")
    @Operation(summary = "更新骑手位置", description = "更新骑手当前位置")
    @Parameter(name = "riderId", description = "骑手ID", required = true)
    public Result<Boolean> updateRiderLocation(
            @PathVariable Long riderId,
            @RequestParam Double lng,
            @RequestParam Double lat) {
        log.info("Updating location for rider: {}, lat: {}, lng: {}", riderId, lat, lng);
        Boolean success = dispatchService.updateRiderLocation(riderId, lng, lat);
        return Result.success(success);
    }

    /**
     * 更新骑手状态
     */
    @PostMapping("/rider/{riderId}/status")
    @Operation(summary = "更新骑手状态", description = "更新骑手在线状态")
    @Parameter(name = "riderId", description = "骑手ID", required = true)
    public Result<Boolean> updateRiderStatus(
            @PathVariable Long riderId,
            @RequestParam Integer status) {
        log.info("Updating status for rider: {} to {}", riderId, status);
        Boolean success = dispatchService.updateRiderStatus(riderId, status);
        return Result.success(success);
    }

    /**
     * 获取待分配任务
     */
    @GetMapping("/tasks/pending")
    @Operation(summary = "获取待分配任务", description = "获取所有待分配的调度任务")
    public Result<List<DispatchTask>> getPendingTasks() {
        log.info("Getting pending dispatch tasks");
        List<DispatchTask> tasks = dispatchService.getPendingDispatchTasks();
        return Result.success(tasks);
    }

    /**
     * 获取骑手当前任务
     */
    @GetMapping("/rider/{riderId}/tasks")
    @Operation(summary = "获取骑手任务", description = "获取骑手的当前配送任务")
    @Parameter(name = "riderId", description = "骑手ID", required = true)
    public Result<List<DispatchTask>> getRiderTasks(@PathVariable Long riderId) {
        log.info("Getting tasks for rider: {}", riderId);
        List<DispatchTask> tasks = dispatchService.getRiderCurrentTasks(riderId);
        return Result.success(tasks);
    }

    /**
     * 触发批量派单
     */
    @PostMapping("/batch-dispatch")
    @Operation(summary = "批量派单", description = "触发批量智能派单任务")
    public Result<Void> batchDispatch() {
        log.info("Triggering batch dispatch");
        dispatchService.batchDispatch();
        return Result.success(null);
    }

    /**
     * 重新分配任务
     */
    @PostMapping("/task/{taskId}/reassign")
    @Operation(summary = "重新派单", description = "重新分配配送任务给其他骑手")
    @Parameter(name = "taskId", description = "任务ID", required = true)
    public Result<Long> reassignTask(@PathVariable Long taskId) {
        log.info("Reassigning task: {}", taskId);
        Long newRiderId = dispatchService.reassignTask(taskId);
        return Result.success(newRiderId);
    }
}
