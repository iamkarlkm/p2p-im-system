package com.im.backend.modules.logistics.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.logistics.dto.*;
import com.im.backend.modules.logistics.entity.RiderLocationTrace;
import com.im.backend.modules.logistics.service.IRiderLocationService;
import com.im.backend.modules.logistics.service.ISmartDispatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能配送调度控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@Tag(name = "智能配送调度", description = "即时配送订单管理与智能调度")
public class SmartDispatchController {

    @Autowired
    private ISmartDispatchService dispatchService;

    @Autowired
    private IRiderLocationService riderLocationService;

    @PostMapping("/orders")
    @Operation(summary = "创建配送订单")
    public ApiResponse<DeliveryOrderResponse> createOrder(@RequestBody CreateDeliveryOrderRequest request) {
        DeliveryOrderResponse response = dispatchService.createOrder(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/orders/{orderId}/dispatch")
    @Operation(summary = "手动触发派单")
    public ApiResponse<Boolean> dispatchOrder(@PathVariable Long orderId) {
        boolean result = dispatchService.dispatchOrder(orderId);
        return ApiResponse.success(result);
    }

    @PostMapping("/orders/batch-dispatch")
    @Operation(summary = "批量派单")
    public ApiResponse<Integer> batchDispatch() {
        int count = dispatchService.batchDispatchOrders();
        return ApiResponse.success(count);
    }

    @PostMapping("/orders/{orderId}/accept")
    @Operation(summary = "骑手接单")
    public ApiResponse<Boolean> acceptOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        boolean result = dispatchService.acceptOrder(riderId, orderId);
        return ApiResponse.success(result);
    }

    @PostMapping("/orders/{orderId}/pickup")
    @Operation(summary = "骑手取货")
    public ApiResponse<Boolean> pickupOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        boolean result = dispatchService.pickupOrder(riderId, orderId);
        return ApiResponse.success(result);
    }

    @PostMapping("/orders/{orderId}/deliver")
    @Operation(summary = "订单送达")
    public ApiResponse<Boolean> deliverOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        boolean result = dispatchService.deliverOrder(riderId, orderId);
        return ApiResponse.success(result);
    }

    @PostMapping("/orders/{orderId}/complete")
    @Operation(summary = "完成订单")
    public ApiResponse<Boolean> completeOrder(@PathVariable Long orderId) {
        boolean result = dispatchService.completeOrder(orderId);
        return ApiResponse.success(result);
    }

    @PostMapping("/orders/{orderId}/cancel")
    @Operation(summary = "取消订单")
    public ApiResponse<Boolean> cancelOrder(@PathVariable Long orderId, @RequestParam String reason) {
        boolean result = dispatchService.cancelOrder(orderId, reason);
        return ApiResponse.success(result);
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "获取订单详情")
    public ApiResponse<DeliveryOrderResponse> getOrderDetail(@PathVariable Long orderId) {
        DeliveryOrderResponse response = dispatchService.getOrderDetail(orderId);
        return ApiResponse.success(response);
    }

    @GetMapping("/orders")
    @Operation(summary = "获取订单列表")
    public ApiResponse<List<DeliveryOrderResponse>> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Long userId) {
        List<DeliveryOrderResponse> list = dispatchService.getOrderList(status, merchantId, userId);
        return ApiResponse.success(list);
    }

    @PostMapping("/riders/location/report")
    @Operation(summary = "上报骑手位置")
    public ApiResponse<Boolean> reportLocation(@RequestBody RiderLocationReportRequest request) {
        boolean result = riderLocationService.reportLocation(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/riders/{riderId}/location")
    @Operation(summary = "获取骑手位置")
    public ApiResponse<RiderLocationResponse> getRiderLocation(@PathVariable Long riderId) {
        RiderLocationResponse response = riderLocationService.getRiderCurrentLocation(riderId);
        return ApiResponse.success(response);
    }

    @GetMapping("/orders/{orderId}/trace")
    @Operation(summary = "获取订单配送轨迹")
    public ApiResponse<List<RiderLocationTrace>> getOrderTrace(@PathVariable Long orderId) {
        List<RiderLocationTrace> trace = riderLocationService.getOrderDeliveryTrace(orderId);
        return ApiResponse.success(trace);
    }

    @GetMapping("/riders/nearby")
    @Operation(summary = "获取附近骑手")
    public ApiResponse<List<RiderLocationResponse>> getNearbyRiders(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5.0") Double radius) {
        List<RiderLocationResponse> riders = riderLocationService.getNearbyRiders(longitude, latitude, radius);
        return ApiResponse.success(riders);
    }
}
