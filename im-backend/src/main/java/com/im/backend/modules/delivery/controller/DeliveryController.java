package com.im.backend.modules.delivery.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.delivery.model.dto.*;
import com.im.backend.modules.delivery.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 配送管理控制器
 */
@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    
    @Autowired
    private DeliveryTaskService taskService;
    
    @Autowired
    private TrajectoryFenceService fenceService;
    
    @Autowired
    private PathOptimizationService pathService;
    
    @Autowired
    private RiderService riderService;
    
    // ========== 配送任务接口 ==========
    
    @PostMapping("/task/create")
    public Result<TaskResponse> createTask(@RequestBody CreateTaskRequest request) {
        return Result.success(taskService.createTask(request));
    }
    
    @GetMapping("/task/{taskId}")
    public Result<TaskResponse> getTask(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskById(taskId));
    }
    
    @PostMapping("/task/{taskId}/assign")
    public Result<Void> assignRider(@PathVariable Long taskId, @RequestParam Long riderId) {
        taskService.assignRider(taskId, riderId);
        return Result.success();
    }
    
    @PostMapping("/task/{taskId}/status")
    public Result<Void> updateTaskStatus(@PathVariable Long taskId, @RequestParam Integer status) {
        taskService.updateTaskStatus(taskId, status);
        return Result.success();
    }
    
    @GetMapping("/task/rider/{riderId}")
    public Result<List<TaskResponse>> getRiderTasks(@PathVariable Long riderId, 
                                                     @RequestParam(required = false) Integer status) {
        return Result.success(taskService.getRiderTasks(riderId, status));
    }
    
    @GetMapping("/task/stats")
    public Result<Object> getTaskStats(@RequestParam(required = false) Long merchantId,
                                        @RequestParam Long startTime,
                                        @RequestParam Long endTime) {
        return Result.success(taskService.getTaskStats(merchantId, startTime, endTime));
    }
    
    // ========== 轨迹围栏接口 ==========
    
    @PostMapping("/trajectory/upload")
    public Result<Void> uploadTrajectory(@RequestBody TrajectoryUploadRequest request) {
        fenceService.uploadTrajectory(request);
        return Result.success();
    }
    
    @PostMapping("/fence/check")
    public Result<FenceCheckResult> checkFence(@RequestBody FenceCheckRequest request) {
        return Result.success(fenceService.checkFence(request));
    }
    
    @GetMapping("/fence/alerts")
    public Result<List<FenceAlertResponse>> getFenceAlerts(@RequestParam(required = false) Long riderId,
                                                            @RequestParam(required = false) Integer status) {
        return Result.success(fenceService.getFenceAlerts(riderId, status));
    }
    
    @PostMapping("/fence/alert/{alertId}/handle")
    public Result<Void> handleFenceAlert(@PathVariable Long alertId, 
                                          @RequestParam String result,
                                          @RequestParam Long handlerId) {
        fenceService.handleFenceAlert(alertId, result, handlerId);
        return Result.success();
    }
    
    // ========== 路径优化接口 ==========
    
    @PostMapping("/route/optimize")
    public Result<RouteResponse> optimizeRoute(@RequestBody RouteOptimizeRequest request) {
        return Result.success(pathService.optimizeRoute(request));
    }
    
    @GetMapping("/route/{routeId}")
    public Result<RouteResponse> getRoute(@PathVariable Long routeId) {
        return Result.success(new RouteResponse());
    }
    
    @PostMapping("/route/{routeId}/recalculate")
    public Result<RouteResponse> recalculateRoute(@PathVariable Long routeId,
                                                   @RequestBody List<Long> newTaskIds) {
        return Result.success(pathService.recalculateRoute(routeId, newTaskIds));
    }
    
    @GetMapping("/route/heatmap")
    public Result<Object> getDeliveryHeatmap(@RequestParam String city,
                                              @RequestParam Long startTime,
                                              @RequestParam Long endTime) {
        return Result.success(pathService.getDeliveryHeatmap(city, startTime, endTime));
    }
    
    // ========== 骑手管理接口 ==========
    
    @GetMapping("/rider/{riderId}")
    public Result<RiderResponse> getRider(@PathVariable Long riderId) {
        return Result.success(riderService.getRiderById(riderId));
    }
    
    @GetMapping("/rider/{riderId}/location")
    public Result<Object> getRiderLocation(@PathVariable Long riderId) {
        return Result.success(riderService.getRiderLocation(riderId));
    }
    
    @GetMapping("/rider/{riderId}/eta")
    public Result<Integer> getRiderETA(@PathVariable Long riderId,
                                        @RequestParam BigDecimal targetLat,
                                        @RequestParam BigDecimal targetLng) {
        return Result.success(riderService.calculateETA(riderId, targetLat, targetLng));
    }
}
