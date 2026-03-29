package com.im.backend.modules.logistics.service;

import com.im.backend.modules.logistics.dto.*;
import com.im.backend.modules.logistics.entity.DeliveryOrder;

import java.util.List;

/**
 * 智能配送调度服务接口
 */
public interface ISmartDispatchService {

    /**
     * 创建配送订单
     */
    DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request);

    /**
     * 智能派单
     * 基于地理位置匹配最近的骑手
     */
    boolean dispatchOrder(Long orderId);

    /**
     * 批量智能派单
     */
    int batchDispatchOrders();

    /**
     * 骑手接单
     */
    boolean acceptOrder(Long riderId, Long orderId);

    /**
     * 骑手取货
     */
    boolean pickupOrder(Long riderId, Long orderId);

    /**
     * 订单送达
     */
    boolean deliverOrder(Long riderId, Long orderId);

    /**
     * 完成订单
     */
    boolean completeOrder(Long orderId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, String reason);

    /**
     * 获取订单详情
     */
    DeliveryOrderResponse getOrderDetail(Long orderId);

    /**
     * 获取订单列表
     */
    List<DeliveryOrderResponse> getOrderList(Integer status, Long merchantId, Long userId);

    /**
     * 重新派单
     */
    boolean reassignOrder(Long orderId);
}
