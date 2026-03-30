package com.im.backend.modules.merchant.order.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryTracking;
import com.im.backend.modules.merchant.order.service.IOrderDeliveryTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单配送追踪控制器
 * Feature #308: Local Logistics Delivery Tracking
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/merchant/delivery")
@Tag(name = "本地物流配送追踪", description = "Local Logistics Delivery Tracking APIs")
public class OrderDeliveryTrackingController {

    @Autowired
    private IOrderDeliveryTrackingService deliveryTrackingService;

    /**
     * 创建配送追踪记录
     */
    @PostMapping("/tracking")
    @Operation(summary = "创建配送追踪", description = "创建订单配送追踪记录")
    public Result<Long> createTracking(@RequestBody OrderDeliveryTracking tracking) {
        log.info("Creating delivery tracking for order: {}", tracking.getOrderId());
        Long trackingId = deliveryTrackingService.createTracking(tracking);
        return Result.success(trackingId);
    }

    /**
     * 获取配送追踪详情
     */
    @GetMapping("/tracking/{trackingId}")
    @Operation(summary = "获取配送追踪详情", description = "根据追踪ID获取配送详情")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<OrderDeliveryTracking> getTracking(@PathVariable Long trackingId) {
        log.info("Getting delivery tracking: {}", trackingId);
        OrderDeliveryTracking tracking = deliveryTrackingService.getTrackingById(trackingId);
        return Result.success(tracking);
    }

    /**
     * 根据订单ID获取追踪信息
     */
    @GetMapping("/tracking/order/{orderId}")
    @Operation(summary = "获取订单配送追踪", description = "根据订单ID获取配送追踪信息")
    @Parameter(name = "orderId", description = "订单ID", required = true)
    public Result<OrderDeliveryTracking> getTrackingByOrderId(@PathVariable Long orderId) {
        log.info("Getting delivery tracking for order: {}", orderId);
        OrderDeliveryTracking tracking = deliveryTrackingService.getTrackingByOrderId(orderId);
        return Result.success(tracking);
    }

    /**
     * 更新配送位置
     */
    @PostMapping("/tracking/{trackingId}/location")
    @Operation(summary = "更新配送位置", description = "更新配送员当前位置")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<Boolean> updateLocation(
            @PathVariable Long trackingId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) String locationDesc) {
        log.info("Updating location for tracking: {}, lat: {}, lng: {}", trackingId, latitude, longitude);
        Boolean success = deliveryTrackingService.updateLocation(trackingId, latitude, longitude, locationDesc);
        return Result.success(success);
    }

    /**
     * 更新配送状态
     */
    @PostMapping("/tracking/{trackingId}/status")
    @Operation(summary = "更新配送状态", description = "更新订单配送状态")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<Boolean> updateStatus(
            @PathVariable Long trackingId,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        log.info("Updating status for tracking: {}, status: {}", trackingId, status);
        Boolean success = deliveryTrackingService.updateStatus(trackingId, status, remark);
        return Result.success(success);
    }

    /**
     * 获取骑手当前配送列表
     */
    @GetMapping("/tracking/rider/{riderId}")
    @Operation(summary = "获取骑手配送列表", description = "获取骑手的配送任务列表")
    @Parameter(name = "riderId", description = "骑手ID", required = true)
    public Result<List<OrderDeliveryTracking>> getRiderDeliveries(
            @PathVariable Long riderId,
            @RequestParam(required = false, defaultValue = "0") Integer status) {
        log.info("Getting deliveries for rider: {}, status: {}", riderId, status);
        List<OrderDeliveryTracking> list = deliveryTrackingService.getRiderDeliveries(riderId, status);
        return Result.success(list);
    }

    /**
     * 分配骑手
     */
    @PostMapping("/tracking/{trackingId}/assign")
    @Operation(summary = "分配骑手", description = "为配送任务分配骑手")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<Boolean> assignRider(
            @PathVariable Long trackingId,
            @RequestParam Long riderId) {
        log.info("Assigning rider: {} to tracking: {}", riderId, trackingId);
        Boolean success = deliveryTrackingService.assignRider(trackingId, riderId);
        return Result.success(success);
    }

    /**
     * 确认送达
     */
    @PostMapping("/tracking/{trackingId}/confirm")
    @Operation(summary = "确认送达", description = "确认订单已送达")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<Boolean> confirmDelivery(
            @PathVariable Long trackingId,
            @RequestParam(required = false) String deliveryCode) {
        log.info("Confirming delivery for tracking: {}", trackingId);
        Boolean success = deliveryTrackingService.confirmDelivery(trackingId, deliveryCode);
        return Result.success(success);
    }

    /**
     * 获取配送轨迹
     */
    @GetMapping("/tracking/{trackingId}/path")
    @Operation(summary = "获取配送轨迹", description = "获取配送完整轨迹")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<List<String>> getDeliveryPath(@PathVariable Long trackingId) {
        log.info("Getting delivery path for tracking: {}", trackingId);
        List<String> path = deliveryTrackingService.getDeliveryPath(trackingId);
        return Result.success(path);
    }

    /**
     * 预估配送时间
     */
    @GetMapping("/tracking/{trackingId}/eta")
    @Operation(summary = "获取预估送达时间", description = "获取预计送达时间")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<String> getEstimatedTime(@PathVariable Long trackingId) {
        log.info("Getting ETA for tracking: {}", trackingId);
        String eta = deliveryTrackingService.getEstimatedTime(trackingId);
        return Result.success(eta);
    }

    /**
     * 实时位置追踪(WebSocket订阅端点)
     */
    @GetMapping("/tracking/{trackingId}/subscribe")
    @Operation(summary = "订阅实时位置", description = "订阅配送实时位置更新")
    @Parameter(name = "trackingId", description = "追踪ID", required = true)
    public Result<String> subscribeLocationUpdates(@PathVariable Long trackingId) {
        log.info("Subscribing to location updates for tracking: {}", trackingId);
        String channel = deliveryTrackingService.subscribeLocationUpdates(trackingId);
        return Result.success(channel);
    }
}
