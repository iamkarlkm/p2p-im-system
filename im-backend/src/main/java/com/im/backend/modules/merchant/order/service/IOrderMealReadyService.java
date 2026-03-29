package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.ConfirmMealReadyRequest;
import com.im.backend.modules.merchant.order.entity.OrderMealReadyRecord;

/**
 * 订单出餐服务接口
 */
public interface IOrderMealReadyService {

    /**
     * 确认出餐
     */
    void confirmMealReady(ConfirmMealReadyRequest request);

    /**
     * 获取出餐记录
     */
    OrderMealReadyRecord getMealReadyRecord(Long orderId);

    /**
     * 预计出餐时间
     */
    void estimateMealReadyTime(Long orderId, Integer estimatedMinutes);

    /**
     * 检查是否已出餐
     */
    boolean isMealReady(Long orderId);
}
