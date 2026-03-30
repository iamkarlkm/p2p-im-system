package com.im.backend.modules.merchant.logistics.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.logistics.dto.DeliveryOrderCreateRequest;
import com.im.backend.modules.merchant.logistics.entity.LogisticsDeliveryOrder;

/**
 * 物流配送服务接口 - 功能#311: 本地物流配送调度
 */
public interface ILogisticsDeliveryService {

    /**
     * 创建配送订单
     */
    Result<LogisticsDeliveryOrder> createOrder(Long userId, DeliveryOrderCreateRequest request);

    /**
     * 骑手接单
     */
    Result<Void> acceptOrder(Long riderId, Long orderId);

    /**
     * 更新配送状态
     */
    Result<Void> updateStatus(Long riderId, Long orderId, Integer status);

    /**
     * 完成配送
     */
    Result<Void> completeDelivery(Long riderId, Long orderId);

    /**
     * 获取骑手订单列表
     */
    IPage<LogisticsDeliveryOrder> getRiderOrders(Long riderId, Integer status, Page<LogisticsDeliveryOrder> page);

    /**
     * 获取商户订单列表
     */
    IPage<LogisticsDeliveryOrder> getMerchantOrders(Long merchantId, Integer status, Page<LogisticsDeliveryOrder> page);

    /**
     * 取消订单
     */
    Result<Void> cancelOrder(Long userId, Long orderId, String reason);
}
