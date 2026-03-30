package com.im.backend.controller;

import com.im.backend.common.Result;
import com.im.backend.dto.DeliveryOrderRequest;
import com.im.backend.dto.DeliveryOrderResponse;
import com.im.backend.service.IDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送订单控制器
 * 即时配送全流程管理
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Tag(name = "本地物流配送", description = "即时配送订单管理、骑手调度、配送追踪相关接口")
public class DeliveryController {

    private final IDeliveryService deliveryService;

    /**
     * 创建配送订单
     */
    @PostMapping("/orders")
    @Operation(summary = "创建配送订单", description = "商户创建即时配送订单")
    public Result<DeliveryOrderResponse> createDeliveryOrder(
            @RequestBody @Validated DeliveryOrderRequest request,
            @RequestAttribute("userId") Long userId) {
        log.info("Create delivery order for merchant: {}, user: {}", request.getMerchantId(), userId);
        DeliveryOrderResponse response = deliveryService.createDeliveryOrder(request, userId);
        return Result.success(response);
    }

    /**
     * 获取配送订单详情
     */
    @GetMapping("/orders/{orderId}")
    @Operation(summary = "获取配送订单详情", description = "根据配送订单ID获取详情")
    public Result<DeliveryOrderResponse> getDeliveryOrderDetail(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId) {
        DeliveryOrderResponse response = deliveryService.getDeliveryOrderDetail(orderId);
        return Result.success(response);
    }

    /**
     * 根据订单编号获取详情
     */
    @GetMapping("/orders/by-no/{orderNo}")
    @Operation(summary = "根据订单编号获取详情", description = "通过订单编号查询配送详情")
    public Result<DeliveryOrderResponse> getDeliveryOrderByNo(
            @PathVariable @Parameter(description = "订单编号") String orderNo) {
        DeliveryOrderResponse response = deliveryService.getDeliveryOrderByNo(orderNo);
        return Result.success(response);
    }

    /**
     * 分配骑手(管理员/调度员)
     */
    @PostMapping("/orders/{orderId}/assign")
    @Operation(summary = "分配骑手", description = "为配送订单分配骑手")
    public Result<Void> assignRider(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId,
            @RequestParam @Parameter(description = "骑手ID") Long riderId) {
        boolean success = deliveryService.assignRider(orderId, riderId);
        return success ? Result.success() : Result.error("分配骑手失败");
    }

    /**
     * 骑手取货
     */
    @PostMapping("/orders/{orderId}/pickup")
    @Operation(summary = "骑手取货", description = "骑手确认已取货")
    public Result<Void> pickupOrder(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId,
            @RequestAttribute("riderId") Long riderId) {
        boolean success = deliveryService.pickupOrder(orderId, riderId);
        return success ? Result.success() : Result.error("取货确认失败");
    }

    /**
     * 开始配送
     */
    @PostMapping("/orders/{orderId}/start")
    @Operation(summary = "开始配送", description = "骑手开始配送")
    public Result<Void> startDelivery(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId,
            @RequestAttribute("riderId") Long riderId) {
        boolean success = deliveryService.startDelivery(orderId, riderId);
        return success ? Result.success() : Result.error("开始配送失败");
    }

    /**
     * 确认送达
     */
    @PostMapping("/orders/{orderId}/complete")
    @Operation(summary = "确认送达", description = "骑手确认订单已送达")
    public Result<Void> confirmDelivery(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId,
            @RequestAttribute("riderId") Long riderId) {
        boolean success = deliveryService.confirmDelivery(orderId, riderId);
        return success ? Result.success() : Result.error("确认送达失败");
    }

    /**
     * 取消配送订单
     */
    @PostMapping("/orders/{orderId}/cancel")
    @Operation(summary = "取消配送订单", description = "取消配送订单")
    public Result<Void> cancelDeliveryOrder(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId,
            @RequestParam @Parameter(description = "取消原因") String reason) {
        boolean success = deliveryService.cancelDeliveryOrder(orderId, reason);
        return success ? Result.success() : Result.error("取消订单失败");
    }

    /**
     * 获取用户配送订单列表
     */
    @GetMapping("/users/orders")
    @Operation(summary = "获取用户配送订单列表", description = "获取当前用户的所有配送订单")
    public Result<List<DeliveryOrderResponse>> getUserDeliveryOrders(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") @Parameter(description = "返回数量") Integer limit) {
        List<DeliveryOrderResponse> orders = deliveryService.getUserDeliveryOrders(userId, limit);
        return Result.success(orders);
    }

    /**
     * 获取骑手配送订单列表
     */
    @GetMapping("/riders/orders")
    @Operation(summary = "获取骑手配送订单列表", description = "获取骑手的所有配送订单")
    public Result<List<DeliveryOrderResponse>> getRiderDeliveryOrders(
            @RequestAttribute("riderId") Long riderId,
            @RequestParam(defaultValue = "20") @Parameter(description = "返回数量") Integer limit) {
        List<DeliveryOrderResponse> orders = deliveryService.getRiderDeliveryOrders(riderId, limit);
        return Result.success(orders);
    }

    /**
     * 获取骑手当前配送中订单
     */
    @GetMapping("/riders/current-orders")
    @Operation(summary = "获取骑手当前配送中订单", description = "获取骑手正在配送的订单")
    public Result<List<DeliveryOrderResponse>> getRiderCurrentOrders(
            @RequestAttribute("riderId") Long riderId) {
        List<DeliveryOrderResponse> orders = deliveryService.getRiderCurrentOrders(riderId);
        return Result.success(orders);
    }

    /**
     * 更新骑手位置
     */
    @PostMapping("/riders/location")
    @Operation(summary = "更新骑手位置", description = "骑手实时上报位置")
    public Result<Void> updateRiderLocation(
            @RequestAttribute("riderId") Long riderId,
            @RequestParam @Parameter(description = "经度") Double longitude,
            @RequestParam @Parameter(description = "纬度") Double latitude) {
        boolean success = deliveryService.updateRiderLocation(riderId, longitude, latitude);
        return success ? Result.success() : Result.error("位置更新失败");
    }

    /**
     * 计算配送费
     */
    @GetMapping("/fee/calculate")
    @Operation(summary = "计算配送费", description = "根据取货和送货地址计算配送费用")
    public Result<Double> calculateDeliveryFee(
            @RequestParam @Parameter(description = "取货经度") Double pickupLng,
            @RequestParam @Parameter(description = "取货纬度") Double pickupLat,
            @RequestParam @Parameter(description = "送货经度") Double deliveryLng,
            @RequestParam @Parameter(description = "送货纬度") Double deliveryLat,
            @RequestParam(required = false) @Parameter(description = "物品重量kg") Double weight) {
        Double fee = deliveryService.calculateDeliveryFee(pickupLng, pickupLat, deliveryLng, deliveryLat, weight);
        return Result.success(fee);
    }

    /**
     * 触发智能派单
     */
    @PostMapping("/orders/{orderId}/dispatch")
    @Operation(summary = "智能派单", description = "为指定订单触发智能派单")
    public Result<Void> smartDispatch(
            @PathVariable @Parameter(description = "配送订单ID") Long orderId) {
        boolean success = deliveryService.smartDispatch(orderId);
        return success ? Result.success() : Result.error("派单失败，暂无可用骑手");
    }
}
