package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.*;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentSession;
import java.util.List;

/**
 * 订单履约会话服务接口
 */
public interface IOrderFulfillmentSessionService {

    /**
     * 创建订单履约会话
     */
    FulfillmentSessionResponse createSession(CreateFulfillmentSessionRequest request);

    /**
     * 根据订单ID获取会话
     */
    FulfillmentSessionResponse getSessionByOrderId(Long orderId);

    /**
     * 根据会话ID获取会话
     */
    FulfillmentSessionResponse getSessionBySessionId(String sessionId);

    /**
     * 分配骑手
     */
    void assignRider(Long orderId, Long riderId);

    /**
     * 结束会话
     */
    void endSession(String sessionId);

    /**
     * 获取用户活跃会话列表
     */
    List<FulfillmentSessionResponse> getActiveSessionsByUser(Long userId);

    /**
     * 获取商户活跃会话列表
     */
    List<FulfillmentSessionResponse> getActiveSessionsByMerchant(Long merchantId);

    /**
     * 获取骑手活跃会话列表
     */
    List<FulfillmentSessionResponse> getActiveSessionsByRider(Long riderId);

    /**
     * 更新预计送达时间
     */
    void updateEstimatedDeliveryTime(Long orderId, Integer estimatedMinutes);

    /**
     * 获取会话实体(内部使用)
     */
    OrderFulfillmentSession getSessionEntity(String sessionId);
}
