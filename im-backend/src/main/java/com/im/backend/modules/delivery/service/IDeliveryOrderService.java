package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.dto.*;
import com.im.backend.modules.delivery.entity.DeliveryOrder;
import java.util.List;

/**
 * 配送订单服务接口
 */
public interface IDeliveryOrderService {
    
    DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request);
    DeliveryOrderResponse getOrderById(Long orderId);
    List<DeliveryOrderResponse> getUserOrders(Long userId);
    List<DeliveryOrderResponse> getRiderActiveOrders(Long riderId);
    
    boolean assignOrder(Long orderId, Long riderId);
    boolean riderAcceptOrder(Long orderId, Long riderId);
    boolean markArrivedPickup(Long orderId, Long riderId);
    boolean markPickedUp(Long orderId, Long riderId);
    boolean markArrivedDelivery(Long orderId, Long riderId);
    boolean markDelivered(Long orderId, Long riderId);
    boolean completeOrder(Long orderId);
    boolean cancelOrder(Long orderId, String reason, Integer cancelType);
    
    List<RiderLocationResponse> getOrderTrajectory(Long orderId);
}
