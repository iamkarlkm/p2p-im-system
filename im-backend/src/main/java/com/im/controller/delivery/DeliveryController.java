package com.im.controller.delivery;

import com.im.entity.delivery.DeliveryOrder;
import com.im.service.delivery.DeliveryOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配送订单控制器 - 即时配送运力调度系统
 * 提供订单创建、分配、追踪、状态流转等API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryOrderService deliveryOrderService;

    /**
     * 创建配送订单
     */
    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody DeliveryOrder order) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder created = deliveryOrderService.createOrder(order);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", created);
            log.info("创建配送订单成功, 订单号: {}", created.getOrderNo());
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "创建失败: " + e.getMessage());
            log.error("创建配送订单失败", e);
        }
        return result;
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/orders/{orderId}")
    public Map<String, Object> getOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        DeliveryOrder order = deliveryOrderService.getOrderById(orderId);
        if (order != null) {
            result.put("success", true);
            result.put("code", 200);
            result.put("data", order);
        } else {
            result.put("success", false);
            result.put("code", 404);
            result.put("message", "订单不存在");
        }
        return result;
    }

    /**
     * 智能分配订单
     */
    @PostMapping("/orders/{orderId}/assign")
    public Map<String, Object> assignOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.assignOrderToRider(orderId);
            if (order.getRiderId() != null) {
                result.put("success", true);
                result.put("code", 200);
                result.put("message", "分配成功");
                result.put("data", order);
            } else {
                result.put("success", false);
                result.put("code", 400);
                result.put("message", "暂无可用骑手");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "分配失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 骑手接单
     */
    @PostMapping("/orders/{orderId}/accept")
    public Map<String, Object> acceptOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.riderAcceptOrder(orderId, riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "接单成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "接单失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 骑手到店
     */
    @PostMapping("/orders/{orderId}/arrive")
    public Map<String, Object> arriveAtMerchant(@PathVariable Long orderId, @RequestParam Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.riderArriveAtMerchant(orderId, riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "到店确认成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "到店确认失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 骑手取货
     */
    @PostMapping("/orders/{orderId}/pickup")
    public Map<String, Object> pickUpOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.riderPickUpOrder(orderId, riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "取货成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "取货失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 订单送达
     */
    @PostMapping("/orders/{orderId}/deliver")
    public Map<String, Object> deliverOrder(@PathVariable Long orderId, @RequestParam Long riderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.riderDeliverOrder(orderId, riderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "送达确认成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "送达确认失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 完成订单
     */
    @PostMapping("/orders/{orderId}/complete")
    public Map<String, Object> completeOrder(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.completeOrder(orderId);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "订单完成");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "完成失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 取消订单
     */
    @PostMapping("/orders/{orderId}/cancel")
    public Map<String, Object> cancelOrder(@PathVariable Long orderId, 
                                            @RequestParam String reason,
                                            @RequestParam String cancelledBy) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.cancelOrder(orderId, reason, cancelledBy);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "订单已取消");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "取消失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取骑手订单列表
     */
    @GetMapping("/riders/{riderId}/orders")
    public Map<String, Object> getRiderOrders(@PathVariable Long riderId,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryOrder> orders = deliveryOrderService.getRiderOrders(riderId, status, page, size);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", orders);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    /**
     * 获取顾客订单列表
     */
    @GetMapping("/customers/{customerId}/orders")
    public Map<String, Object> getCustomerOrders(@PathVariable Long customerId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryOrder> orders = deliveryOrderService.getCustomerOrders(customerId, status, page, size);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", orders);
        return result;
    }

    /**
     * 获取待分配订单列表
     */
    @GetMapping("/orders/pending")
    public Map<String, Object> getPendingOrders(@RequestParam(required = false) Long zoneId,
                                                 @RequestParam(defaultValue = "1") Integer page,
                                                 @RequestParam(defaultValue = "20") Integer size) {
        Map<String, Object> result = new HashMap<>();
        List<DeliveryOrder> orders = deliveryOrderService.getPendingOrders(zoneId, page, size);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", orders);
        return result;
    }

    /**
     * 重新分配订单
     */
    @PostMapping("/orders/{orderId}/reassign")
    public Map<String, Object> reassignOrder(@PathVariable Long orderId, @RequestParam String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.reassignOrder(orderId, reason);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "重新分配成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "重新分配失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 订单评价
     */
    @PostMapping("/orders/{orderId}/rate")
    public Map<String, Object> rateOrder(@PathVariable Long orderId,
                                          @RequestParam Integer rating,
                                          @RequestParam(required = false) String comment) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.rateOrder(orderId, rating, comment);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "评价成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "评价失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 计算配送费
     */
    @GetMapping("/orders/calculate-fee")
    public Map<String, Object> calculateFee(@RequestParam Long merchantId,
                                             @RequestParam BigDecimal deliveryLng,
                                             @RequestParam BigDecimal deliveryLat,
                                             @RequestParam(required = false) BigDecimal weight) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal fee = deliveryOrderService.calculateDeliveryFee(merchantId, deliveryLng, deliveryLat, weight);
        result.put("success", true);
        result.put("code", 200);
        result.put("data", fee);
        return result;
    }

    /**
     * 获取订单配送进度
     */
    @GetMapping("/orders/{orderId}/progress")
    public Map<String, Object> getOrderProgress(@PathVariable Long orderId) {
        Map<String, Object> result = new HashMap<>();
        DeliveryOrder order = deliveryOrderService.getOrderProgress(orderId);
        if (order != null) {
            Map<String, Object> progress = new HashMap<>();
            progress.put("orderId", order.getId());
            progress.put("status", order.getStatus());
            progress.put("progress", order.getProgressPercentage());
            progress.put("riderId", order.getRiderId());
            progress.put("riderName", order.getRiderName());
            progress.put("riderPhone", order.getRiderPhone());
            progress.put("estimatedDeliveryTime", order.getEstimatedDeliveryTime());
            progress.put("isDelayed", order.isDelayed());
            
            result.put("success", true);
            result.put("code", 200);
            result.put("data", progress);
        } else {
            result.put("success", false);
            result.put("code", 404);
            result.put("message", "订单不存在");
        }
        return result;
    }

    /**
     * 标记订单异常
     */
    @PostMapping("/orders/{orderId}/exception")
    public Map<String, Object> markException(@PathVariable Long orderId,
                                              @RequestParam String exceptionType,
                                              @RequestParam String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            DeliveryOrder order = deliveryOrderService.markOrderException(orderId, exceptionType, reason);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "异常标记成功");
            result.put("data", order);
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "标记失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 批量分配订单(智能调度)
     */
    @PostMapping("/orders/batch-assign")
    public Map<String, Object> batchAssignOrders(@RequestBody List<Long> orderIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            deliveryOrderService.batchAssignOrders(orderIds);
            result.put("success", true);
            result.put("code", 200);
            result.put("message", "批量分配已启动");
        } catch (Exception e) {
            result.put("success", false);
            result.put("code", 500);
            result.put("message", "批量分配失败: " + e.getMessage());
        }
        return result;
    }
}
