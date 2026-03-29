package com.im.local.delivery.controller;

import com.im.local.delivery.dto.*;
import com.im.local.delivery.service.IDeliveryOrderService;
import com.im.local.delivery.service.IRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流配送控制器
 * 本地生活物流配送与实时追踪API
 */
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    
    private final IDeliveryOrderService orderService;
    private final IRiderService riderService;
    
    /**
     * 创建配送订单
     */
    @PostMapping("/order/create")
    public ResponseEntity<DeliveryOrderResponse> createOrder(@RequestBody CreateDeliveryOrderRequest request) {
        DeliveryOrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryOrderResponse> getOrderDetail(@PathVariable Long orderId) {
        DeliveryOrderResponse response = orderService.getOrderDetail(orderId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/order/user/{userId}")
    public ResponseEntity<List<DeliveryOrderResponse>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        List<DeliveryOrderResponse> orders = orderService.getUserOrders(userId, limit);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 获取骑手当前订单
     */
    @GetMapping("/order/rider/{riderId}/active")
    public ResponseEntity<List<DeliveryOrderResponse>> getRiderActiveOrders(@PathVariable Long riderId) {
        List<DeliveryOrderResponse> orders = orderService.getRiderActiveOrders(riderId);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 标记取货
     */
    @PostMapping("/order/{orderId}/pickup")
    public ResponseEntity<Map<String, Object>> markPickedUp(
            @PathVariable Long orderId,
            @RequestParam Long riderId) {
        boolean success = orderService.markPickedUp(orderId, riderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "取货成功" : "取货失败");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 标记送达
     */
    @PostMapping("/order/{orderId}/deliver")
    public ResponseEntity<Map<String, Object>> markDelivered(
            @PathVariable Long orderId,
            @RequestParam Long riderId,
            @RequestParam(required = false) String signImageUrl) {
        boolean success = orderService.markDelivered(orderId, riderId, signImageUrl);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "送达成功" : "送达失败");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) String reason) {
        boolean success = orderService.cancelOrder(orderId, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "取消成功" : "取消失败");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取订单轨迹
     */
    @GetMapping("/order/{orderId}/trajectory")
    public ResponseEntity<List<RiderLocationResponse>> getOrderTrajectory(@PathVariable Long orderId) {
        List<RiderLocationResponse> trajectory = orderService.getOrderTrajectory(orderId);
        return ResponseEntity.ok(trajectory);
    }
    
    /**
     * 骑手上报位置
     */
    @PostMapping("/rider/location/upload")
    public ResponseEntity<Map<String, Object>> uploadLocation(@RequestBody RiderLocationUploadRequest request) {
        boolean success = riderService.uploadLocation(request);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取骑手信息
     */
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<RiderResponse> getRiderInfo(@PathVariable Long riderId) {
        RiderResponse rider = riderService.getRiderInfo(riderId);
        return rider != null ? ResponseEntity.ok(rider) : ResponseEntity.notFound().build();
    }
    
    /**
     * 更新骑手状态
     */
    @PostMapping("/rider/{riderId}/status")
    public ResponseEntity<Map<String, Object>> updateRiderStatus(
            @PathVariable Long riderId,
            @RequestParam Integer status) {
        boolean success = riderService.updateStatus(riderId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 骑手上线
     */
    @PostMapping("/rider/{riderId}/online")
    public ResponseEntity<Map<String, Object>> goOnline(@PathVariable Long riderId) {
        boolean success = riderService.goOnline(riderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", "上线成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 骑手下线
     */
    @PostMapping("/rider/{riderId}/offline")
    public ResponseEntity<Map<String, Object>> goOffline(@PathVariable Long riderId) {
        boolean success = riderService.goOffline(riderId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", "下线成功");
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取骑手轨迹
     */
    @GetMapping("/rider/{riderId}/trajectory")
    public ResponseEntity<List<RiderLocationResponse>> getRiderTrajectory(
            @PathVariable Long riderId,
            @RequestParam(defaultValue = "2") Integer hours) {
        List<RiderLocationResponse> trajectory = riderService.getRiderTrajectory(riderId, hours);
        return ResponseEntity.ok(trajectory);
    }
    
    /**
     * 获取附近可用骑手
     */
    @GetMapping("/rider/nearby")
    public ResponseEntity<List<RiderResponse>> getNearbyRiders(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5000") Double radius) {
        List<RiderResponse> riders = riderService.getNearbyAvailableRiders(lat, lng, radius);
        return ResponseEntity.ok(riders);
    }
    
    /**
     * 获取骑手今日统计
     */
    @GetMapping("/rider/{riderId}/stats/today")
    public ResponseEntity<RiderTodayStatsResponse> getRiderTodayStats(@PathVariable Long riderId) {
        RiderTodayStatsResponse stats = riderService.getTodayStats(riderId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 计算配送费
     */
    @GetMapping("/fee/calculate")
    public ResponseEntity<Map<String, Object>> calculateDeliveryFee(
            @RequestParam Double distance,
            @RequestParam(defaultValue = "1.0") Double weight) {
        java.math.BigDecimal fee = orderService.calculateDeliveryFee(distance, weight);
        Map<String, Object> result = new HashMap<>();
        result.put("distance", distance);
        result.put("weight", weight);
        result.put("deliveryFee", fee);
        return ResponseEntity.ok(result);
    }
}
