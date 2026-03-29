package com.im.backend.modules.local.controller;

import com.im.backend.common.model.Result;
import com.im.backend.modules.local.dto.*;
import com.im.backend.modules.local.service.ISmartDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能调度控制器
 */
@RestController
@RequestMapping("/api/v1/dispatch")
@Tag(name = "智能调度", description = "本地生活智能调度与资源优化")
public class SmartDispatchController {
    
    @Autowired
    private ISmartDispatchService dispatchService;
    
    @PostMapping("/task/create")
    @Operation(summary = "创建调度任务")
    public Result<DispatchTaskResponse> createTask(@RequestBody CreateDispatchTaskRequest request) {
        return Result.success(dispatchService.createDispatchTask(request));
    }
    
    @GetMapping("/task/{taskId}")
    @Operation(summary = "获取任务详情")
    public Result<DispatchTaskResponse> getTask(@PathVariable String taskId) {
        return Result.success(dispatchService.getTaskById(taskId));
    }
    
    @PostMapping("/task/{taskId}/assign")
    @Operation(summary = "智能分配任务")
    public Result<DispatchTaskResponse> assignTask(@PathVariable String taskId) {
        return Result.success(dispatchService.assignTask(taskId));
    }
    
    @PostMapping("/task/{taskId}/pickup")
    @Operation(summary = "标记取货")
    public Result<DispatchTaskResponse> markPickup(@PathVariable String taskId) {
        return Result.success(dispatchService.markPickup(taskId));
    }
    
    @PostMapping("/task/{taskId}/deliver")
    @Operation(summary = "标记送达")
    public Result<DispatchTaskResponse> markDelivered(@PathVariable String taskId) {
        return Result.success(dispatchService.markDelivered(taskId));
    }
    
    @PostMapping("/task/{taskId}/cancel")
    @Operation(summary = "取消任务")
    public Result<DispatchTaskResponse> cancelTask(@PathVariable String taskId, @RequestParam String reason) {
        return Result.success(dispatchService.cancelTask(taskId, reason));
    }
    
    @GetMapping("/staff/{staffId}/active-tasks")
    @Operation(summary = "获取服务人员活跃任务")
    public Result<List<DispatchTaskResponse>> getActiveTasks(@PathVariable String staffId) {
        return Result.success(dispatchService.getActiveTasksByStaff(staffId));
    }
    
    @PostMapping("/path/plan")
    @Operation(summary = "路径规划")
    public Result<PathPlanningResponse> planPath(@RequestBody PathPlanningRequest request) {
        return Result.success(dispatchService.planPath(request));
    }
    
    @PostMapping("/path/optimize")
    @Operation(summary = "多订单路径优化")
    public Result<PathPlanningResponse> optimizePath(
            @RequestParam String staffId,
            @RequestParam List<String> taskIds) {
        return Result.success(dispatchService.optimizeMultiOrderPath(staffId, taskIds));
    }
    
    @GetMapping("/fee/calculate")
    @Operation(summary = "计算配送费")
    public Result<java.math.BigDecimal> calculateFee(
            @RequestParam java.math.BigDecimal distance,
            @RequestParam java.math.BigDecimal weight) {
        return Result.success(dispatchService.calculateDeliveryFee(distance, weight));
    }
}
