package com.im.backend.modules.delivery.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.delivery.dto.*;
import com.im.backend.modules.delivery.service.IDeliveryOrderService;
import com.im.backend.modules.delivery.service.IRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送控制器
 */
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    
    private final IDeliveryOrderService orderService;
    private final IRiderService riderService;
    
    @PostMapping("/order/create")
    public Result<DeliveryOrderResponse> createOrder(@RequestBody CreateDeliveryOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }
    
    @GetMapping("/order/{orderId}")
    public Result<DeliveryOrderResponse> getOrder(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderById(orderId));
    }
    
    @GetMapping("/order/user/{userId}")
    public Result<List<DeliveryOrderResponse>> getUserOrders(@PathVariable Long userId) {
        return Result.success(orderService.getUserOrders(userId));
    }
    
    @GetMapping("/order/rider/{riderId}/active")
    public Result<List<DeliveryOrderResponse>> getRiderActiveOrders(@PathVariable Long riderId) {
        return Result.success(orderService.getRiderActiveOrders(riderId));
    }
    
    @PostMapping("/order/{orderId}/assign")
    public Result<Boolean> assignOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.assignOrder(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/accept")
    public Result<Boolean> acceptOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.riderAcceptOrder(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/pickup/arrive")
    public Result<Boolean> arrivePickup(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.markArrivedPickup(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/pickup")
    public Result<Boolean> markPickedUp(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.markPickedUp(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/deliver/arrive")
    public Result<Boolean> arriveDelivery(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.markArrivedDelivery(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/deliver")
    public Result<Boolean> markDelivered(@PathVariable Long orderId, @RequestParam Long riderId) {
        return Result.success(orderService.markDelivered(orderId, riderId));
    }
    
    @PostMapping("/order/{orderId}/complete")
    public Result<Boolean> completeOrder(@PathVariable Long orderId) {
        return Result.success(orderService.completeOrder(orderId));
    }
    
    @PostMapping("/order/{orderId}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long orderId, 
                                       @RequestParam String reason,
                                       @RequestParam Integer cancelType) {
        return Result.success(orderService.cancelOrder(orderId, reason, cancelType));
    }
    
    @GetMapping("/order/{orderId}/trajectory")
    public Result<List<RiderLocationResponse>> getOrderTrajectory(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderTrajectory(orderId));
    }
    
    @PostMapping("/rider/location/upload")
    public Result<Boolean> uploadLocation(@RequestBody RiderLocationUploadRequest request) {
        return Result.success(riderService.uploadLocation(request));
    }
    
    @GetMapping("/rider/{riderId}")
    public Result<RiderResponse> getRider(@PathVariable Long riderId) {
        return Result.success(riderService.getRiderById(riderId));
    }
    
    @PostMapping("/rider/{riderId}/online")
    public Result<Boolean> riderOnline(@PathVariable Long riderId) {
        return Result.success(riderService.riderGoOnline(riderId));
    }
    
    @PostMapping("/rider/{riderId}/offline")
    public Result<Boolean> riderOffline(@PathVariable Long riderId) {
        return Result.success(riderService.riderGoOffline(riderId));
    }
    
    @GetMapping("/rider/nearby")
    public Result<List<RiderLocationResponse>> getNearbyRiders(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius) {
        return Result.success(riderService.getNearbyAvailableRiders(lat, lng, radius));
    }
    
    @GetMapping("/rider/{riderId}/stats/today")
    public Result<RiderTodayStatsResponse> getRiderTodayStats(@PathVariable Long riderId) {
        return Result.success(riderService.getRiderTodayStats(riderId));
    }
}
