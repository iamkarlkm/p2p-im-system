package com.im.backend.modules.merchant.order.controller;

import com.im.backend.common.core.result.R;
import com.im.backend.modules.merchant.order.dto.*;
import com.im.backend.modules.merchant.order.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 订单履约与配送追踪控制器
 */
@Api(tags = "订单履约与配送管理")
@RestController
@RequestMapping("/api/v1/order-fulfillment")
@RequiredArgsConstructor
public class OrderFulfillmentController {

    private final IOrderFulfillmentSessionService sessionService;
    private final IOrderDeliveryTrackingService trackingService;
    private final IOrderFulfillmentMessageService messageService;
    private final IOrderDeliveryReceiptService receiptService;
    private final IOrderMealReadyService mealReadyService;
    private final IOrderDeliveryExceptionService exceptionService;

    // ==================== 会话管理 ====================

    @ApiOperation("创建订单履约会话")
    @PostMapping("/sessions")
    public R<FulfillmentSessionResponse> createSession(@Valid @RequestBody CreateFulfillmentSessionRequest request) {
        return R.ok(sessionService.createSession(request));
    }

    @ApiOperation("根据订单ID获取会话")
    @GetMapping("/sessions/order/{orderId}")
    public R<FulfillmentSessionResponse> getSessionByOrderId(@PathVariable Long orderId) {
        return R.ok(sessionService.getSessionByOrderId(orderId));
    }

    @ApiOperation("根据会话ID获取会话")
    @GetMapping("/sessions/{sessionId}")
    public R<FulfillmentSessionResponse> getSessionBySessionId(@PathVariable String sessionId) {
        return R.ok(sessionService.getSessionBySessionId(sessionId));
    }

    @ApiOperation("分配骑手")
    @PostMapping("/orders/{orderId}/assign-rider")
    public R<Void> assignRider(@PathVariable Long orderId, @RequestParam Long riderId) {
        sessionService.assignRider(orderId, riderId);
        return R.ok();
    }

    @ApiOperation("结束会话")
    @PostMapping("/sessions/{sessionId}/end")
    public R<Void> endSession(@PathVariable String sessionId) {
        sessionService.endSession(sessionId);
        return R.ok();
    }

    @ApiOperation("获取用户活跃会话")
    @GetMapping("/sessions/user/{userId}/active")
    public R<List<FulfillmentSessionResponse>> getUserActiveSessions(@PathVariable Long userId) {
        return R.ok(sessionService.getActiveSessionsByUser(userId));
    }

    @ApiOperation("获取商户活跃会话")
    @GetMapping("/sessions/merchant/{merchantId}/active")
    public R<List<FulfillmentSessionResponse>> getMerchantActiveSessions(@PathVariable Long merchantId) {
        return R.ok(sessionService.getActiveSessionsByMerchant(merchantId));
    }

    @ApiOperation("获取骑手活跃会话")
    @GetMapping("/sessions/rider/{riderId}/active")
    public R<List<FulfillmentSessionResponse>> getRiderActiveSessions(@PathVariable Long riderId) {
        return R.ok(sessionService.getActiveSessionsByRider(riderId));
    }

    // ==================== 配送追踪 ====================

    @ApiOperation("更新骑手位置")
    @PostMapping("/tracking/location")
    public R<Void> updateRiderLocation(@Valid @RequestBody RiderLocationUpdateRequest request) {
        trackingService.updateRiderLocation(request);
        return R.ok();
    }

    @ApiOperation("获取骑手最新位置")
    @GetMapping("/tracking/order/{orderId}/location")
    public R<RiderLocationResponse> getRiderLatestLocation(@PathVariable Long orderId) {
        return R.ok(trackingService.getRiderLatestLocation(orderId));
    }

    @ApiOperation("获取订单配送轨迹")
    @GetMapping("/tracking/order/{orderId}/track")
    public R<List<RiderLocationResponse>> getDeliveryTrack(@PathVariable Long orderId) {
        return R.ok(trackingService.getDeliveryTrack(orderId));
    }

    @ApiOperation("骑手接单")
    @PostMapping("/orders/{orderId}/accept")
    public R<Void> riderAcceptOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        trackingService.riderAcceptOrder(orderId, riderId);
        return R.ok();
    }

    @ApiOperation("骑手到达商家")
    @PostMapping("/orders/{orderId}/arrive-merchant")
    public R<Void> riderArrivedMerchant(@PathVariable Long orderId) {
        trackingService.riderArrivedMerchant(orderId);
        return R.ok();
    }

    @ApiOperation("骑手取餐")
    @PostMapping("/orders/{orderId}/pickup")
    public R<Void> riderPickedUpMeal(@PathVariable Long orderId) {
        trackingService.riderPickedUpMeal(orderId);
        return R.ok();
    }

    @ApiOperation("开始配送")
    @PostMapping("/orders/{orderId}/start-delivery")
    public R<Void> riderStartDelivery(@PathVariable Long orderId) {
        trackingService.riderStartDelivery(orderId);
        return R.ok();
    }

    @ApiOperation("确认送达")
    @PostMapping("/orders/{orderId}/arrive-user")
    public R<Void> riderArrivedUser(@PathVariable Long orderId) {
        trackingService.riderArrivedUser(orderId);
        return R.ok();
    }

    // ==================== 消息管理 ====================

    @ApiOperation("获取会话消息列表")
    @GetMapping("/sessions/{sessionId}/messages")
    public R<List<FulfillmentMessageResponse>> getSessionMessages(@PathVariable String sessionId) {
        return R.ok(messageService.getSessionMessages(sessionId));
    }

    @ApiOperation("发送文本消息")
    @PostMapping("/messages/text")
    public R<Void> sendTextMessage(@Valid @RequestBody SendFulfillmentMessageRequest request,
                                    @RequestParam Long senderId,
                                    @RequestParam Integer senderType) {
        messageService.sendTextMessage(request, senderId, senderType);
        return R.ok();
    }

    @ApiOperation("标记消息已读")
    @PostMapping("/sessions/{sessionId}/read")
    public R<Void> markMessagesAsRead(@PathVariable String sessionId, @RequestParam Long userId) {
        messageService.markMessagesAsRead(sessionId, userId);
        return R.ok();
    }

    @ApiOperation("获取未读消息数")
    @GetMapping("/sessions/{sessionId}/unread-count")
    public R<Integer> getUnreadMessageCount(@PathVariable String sessionId, @RequestParam Long userId) {
        return R.ok(messageService.getUnreadMessageCount(sessionId, userId));
    }

    // ==================== 签收管理 ====================

    @ApiOperation("生成签收码")
    @PostMapping("/orders/{orderId}/receipt-code")
    public R<String> generateReceiptCode(@PathVariable Long orderId) {
        return R.ok(receiptService.generateReceiptCode(orderId));
    }

    @ApiOperation("验证签收码")
    @PostMapping("/orders/{orderId}/verify-code")
    public R<Boolean> verifyReceiptCode(@PathVariable Long orderId, @RequestParam String code) {
        return R.ok(receiptService.verifyReceiptCode(orderId, code));
    }

    @ApiOperation("确认签收")
    @PostMapping("/orders/{orderId}/confirm-receipt")
    public R<DeliveryReceiptResponse> confirmReceipt(@PathVariable Long orderId,
                                                      @Valid @RequestBody ConfirmDeliveryReceiptRequest request,
                                                      @RequestParam Long userId) {
        request.setOrderId(orderId);
        return R.ok(receiptService.confirmReceipt(request, userId));
    }

    @ApiOperation("获取签收信息")
    @GetMapping("/orders/{orderId}/receipt")
    public R<DeliveryReceiptResponse> getReceiptInfo(@PathVariable Long orderId) {
        return R.ok(receiptService.getReceiptInfo(orderId));
    }

    // ==================== 出餐管理 ====================

    @ApiOperation("确认出餐")
    @PostMapping("/orders/{orderId}/meal-ready")
    public R<Void> confirmMealReady(@PathVariable Long orderId, @Valid @RequestBody ConfirmMealReadyRequest request) {
        request.setOrderId(orderId);
        mealReadyService.confirmMealReady(request);
        return R.ok();
    }

    @ApiOperation("预计出餐时间")
    @PostMapping("/orders/{orderId}/estimate-meal-time")
    public R<Void> estimateMealReadyTime(@PathVariable Long orderId, @RequestParam Integer minutes) {
        mealReadyService.estimateMealReadyTime(orderId, minutes);
        return R.ok();
    }

    // ==================== 异常管理 ====================

    @ApiOperation("上报配送异常")
    @PostMapping("/orders/{orderId}/report-exception")
    public R<Void> reportException(@PathVariable Long orderId,
                                    @Valid @RequestBody ReportDeliveryExceptionRequest request,
                                    @RequestParam Long riderId) {
        request.setOrderId(orderId);
        exceptionService.reportException(request, riderId);
        return R.ok();
    }

    @ApiOperation("处理异常")
    @PostMapping("/exceptions/{exceptionId}/handle")
    public R<Void> handleException(@PathVariable Long exceptionId,
                                    @RequestParam Long handlerId,
                                    @RequestParam String result,
                                    @RequestParam Integer status) {
        exceptionService.handleException(exceptionId, handlerId, result, status);
        return R.ok();
    }

    @ApiOperation("获取订单异常记录")
    @GetMapping("/orders/{orderId}/exceptions")
    public R<List<com.im.backend.modules.merchant.order.entity.OrderDeliveryException>> getOrderExceptions(@PathVariable Long orderId) {
        return R.ok(exceptionService.getOrderExceptions(orderId));
    }

    @ApiOperation("转派订单")
    @PostMapping("/orders/{orderId}/reassign")
    public R<Void> reassignOrder(@PathVariable Long orderId, @RequestParam Long newRiderId) {
        exceptionService.reassignOrder(orderId, newRiderId);
        return R.ok();
    }
}
