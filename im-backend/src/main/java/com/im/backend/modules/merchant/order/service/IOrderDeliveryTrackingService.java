package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.*;
import java.util.List;

/**
 * 订单配送追踪服务接口
 */
public interface IOrderDeliveryTrackingService {

    /**
     * 更新骑手位置
     */
    void updateRiderLocation(RiderLocationUpdateRequest request);

    /**
     * 获取骑手最新位置
     */
    RiderLocationResponse getRiderLatestLocation(Long orderId);

    /**
     * 获取订单配送轨迹
     */
    List<RiderLocationResponse> getDeliveryTrack(Long orderId);

    /**
     * 更新配送状态
     */
    void updateDeliveryStatus(Long orderId, Integer status);

    /**
     * 计算预计送达时间
     */
    void calculateEstimatedArrivalTime(Long orderId);

    /**
     * 骑手接单
     */
    void riderAcceptOrder(Long orderId, Long riderId);

    /**
     * 骑手到达商家
     */
    void riderArrivedMerchant(Long orderId);

    /**
     * 骑手取餐
     */
    void riderPickedUpMeal(Long orderId);

    /**
     * 骑手开始配送
     */
    void riderStartDelivery(Long orderId);

    /**
     * 骑手送达
     */
    void riderArrivedUser(Long orderId);
}
