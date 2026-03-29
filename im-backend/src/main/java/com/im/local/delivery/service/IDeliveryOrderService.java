package com.im.local.delivery.service;

import com.im.local.delivery.dto.*;
import com.im.local.delivery.entity.DeliveryOrder;
import java.util.List;

/**
 * 配送订单服务接口
 */
public interface IDeliveryOrderService {
    
    /**
     * 创建配送订单
     */
    DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request);
    
    /**
     * 智能分配骑手
     */
    boolean assignRider(Long orderId);
    
    /**
     * 获取订单详情
     */
    DeliveryOrderResponse getOrderDetail(Long orderId);
    
    /**
     * 获取用户订单列表
     */
    List<DeliveryOrderResponse> getUserOrders(Long userId, Integer limit);
    
    /**
     * 获取骑手当前订单
     */
    List<DeliveryOrderResponse> getRiderActiveOrders(Long riderId);
    
    /**
     * 标记取货
     */
    boolean markPickedUp(Long orderId, Long riderId);
    
    /**
     * 标记送达
     */
    boolean markDelivered(Long orderId, Long riderId, String signImageUrl);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, String reason);
    
    /**
     * 计算配送费
     */
    java.math.BigDecimal calculateDeliveryFee(Double distance, Double weight);
    
    /**
     * 获取订单轨迹
     */
    List<RiderLocationResponse> getOrderTrajectory(Long orderId);
    
    /**
     * 智能调度算法 - 为订单选择最优骑手
     */
    Long findOptimalRider(DeliveryOrder order);
}
