package com.im.backend.modules.merchant.logistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.logistics.dto.DeliveryOrderCreateRequest;
import com.im.backend.modules.merchant.logistics.entity.LogisticsDeliveryOrder;
import com.im.backend.modules.merchant.logistics.service.ILogisticsDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 物流配送控制器 - 功能#311: 本地物流配送调度
 */
@Tag(name = "物流配送", description = "本地物流配送调度相关接口")
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsDeliveryController {

    private final ILogisticsDeliveryService deliveryService;

    @Operation(summary = "创建配送订单")
    @PostMapping("/order/create")
    public Result<LogisticsDeliveryOrder> createOrder(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated DeliveryOrderCreateRequest request) {
        return deliveryService.createOrder(userId, request);
    }

    @Operation(summary = "骑手接单")
    @PostMapping("/order/accept/{orderId}")
    public Result<Void> acceptOrder(
            @RequestAttribute("riderId") Long riderId,
            @PathVariable Long orderId) {
        return deliveryService.acceptOrder(riderId, orderId);
    }

    @Operation(summary = "更新配送状态")
    @PostMapping("/order/status/{orderId}")
    public Result<Void> updateStatus(
            @RequestAttribute("riderId") Long riderId,
            @PathVariable Long orderId,
            @RequestParam Integer status) {
        return deliveryService.updateStatus(riderId, orderId, status);
    }

    @Operation(summary = "完成配送")
    @PostMapping("/order/complete/{orderId}")
    public Result<Void> completeDelivery(
            @RequestAttribute("riderId") Long riderId,
            @PathVariable Long orderId) {
        return deliveryService.completeDelivery(riderId, orderId);
    }

    @Operation(summary = "获取骑手订单列表")
    @GetMapping("/rider/orders")
    public Result<IPage<LogisticsDeliveryOrder>> getRiderOrders(
            @RequestAttribute("riderId") Long riderId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deliveryService.getRiderOrders(riderId, status, new Page<>(page, size)));
    }

    @Operation(summary = "获取商户订单列表")
    @GetMapping("/merchant/orders/{merchantId}")
    public Result<IPage<LogisticsDeliveryOrder>> getMerchantOrders(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(deliveryService.getMerchantOrders(merchantId, status, new Page<>(page, size)));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/order/cancel/{orderId}")
    public Result<Void> cancelOrder(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long orderId,
            @RequestParam String reason) {
        return deliveryService.cancelOrder(userId, orderId, reason);
    }
}
