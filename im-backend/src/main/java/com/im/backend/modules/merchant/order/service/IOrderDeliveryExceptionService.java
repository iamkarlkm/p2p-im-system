package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.ReportDeliveryExceptionRequest;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryException;
import java.util.List;

/**
 * 订单配送异常服务接口
 */
public interface IOrderDeliveryExceptionService {

    /**
     * 上报配送异常
     */
    void reportException(ReportDeliveryExceptionRequest request, Long riderId);

    /**
     * 处理异常
     */
    void handleException(Long exceptionId, Long handlerId, String handleResult, Integer newStatus);

    /**
     * 获取订单异常记录
     */
    List<OrderDeliveryException> getOrderExceptions(Long orderId);

    /**
     * 获取骑手待处理异常
     */
    List<OrderDeliveryException> getRiderPendingExceptions(Long riderId);

    /**
     * 获取待处理异常数
     */
    int getPendingExceptionCount();

    /**
     * 转派订单
     */
    void reassignOrder(Long orderId, Long newRiderId);
}
