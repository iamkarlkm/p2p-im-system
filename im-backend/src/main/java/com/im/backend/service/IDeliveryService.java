package com.im.backend.service;

import com.im.backend.dto.DeliveryOrderRequest;
import com.im.backend.dto.DeliveryOrderResponse;
import com.im.backend.entity.DeliveryOrder;

import java.util.List;

/**
 * 配送服务接口
 */
public interface IDeliveryService {

    /**
     * 创建配送订单
     */
    DeliveryOrderResponse createDeliveryOrder(DeliveryOrderRequest request, Long userId);

    /**
     * 获取配送订单详情
     */
    DeliveryOrderResponse getDeliveryOrderDetail(Long orderId);

    /**
     * 根据订单编号获取详情
     */
    DeliveryOrderResponse getDeliveryOrderByNo(String orderNo);

    /**
     * 分配骑手
     */
    boolean assignRider(Long orderId, Long riderId);

    /**
     * 骑手取货
     */
    boolean pickupOrder(Long orderId, Long riderId);

    /**
     * 开始配送
     */
    boolean startDelivery(Long orderId, Long riderId);

    /**
     * 确认送达
     */
    boolean confirmDelivery(Long orderId, Long riderId);

    /**
     * 取消配送订单
     */
    boolean cancelDeliveryOrder(Long orderId, String reason);

    /**
     * 获取用户配送订单列表
     */
    List<DeliveryOrderResponse> getUserDeliveryOrders(Long userId, Integer limit);

    /**
     * 获取骑手配送订单列表
     */
    List<DeliveryOrderResponse> getRiderDeliveryOrders(Long riderId, Integer limit);

    /**
     * 获取骑手当前配送中订单
     */
    List<DeliveryOrderResponse> getRiderCurrentOrders(Long riderId);

    /**
     * 更新骑手位置
     */
    boolean updateRiderLocation(Long riderId, Double longitude, Double latitude);

    /**
     * 智能派单
     */
    boolean smartDispatch(Long orderId);

    /**
     * 计算配送费
     */
    Double calculateDeliveryFee(Double pickupLng, Double pickupLat, Double deliveryLng, Double deliveryLat, Double weight);
}
