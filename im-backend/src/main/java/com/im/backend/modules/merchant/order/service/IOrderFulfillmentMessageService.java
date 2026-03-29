package com.im.backend.modules.merchant.order.service;

import com.im.backend.modules.merchant.order.dto.*;
import java.util.List;

/**
 * 订单履约消息服务接口
 */
public interface IOrderFulfillmentMessageService {

    /**
     * 发送系统消息
     */
    void sendSystemMessage(Long orderId, Integer messageSubType, String content);

    /**
     * 发送文本消息
     */
    void sendTextMessage(SendFulfillmentMessageRequest request, Long senderId, Integer senderType);

    /**
     * 发送位置卡片消息(骑手位置)
     */
    void sendLocationCardMessage(Long orderId, RiderLocationCardDTO locationCard);

    /**
     * 批量发送消息(给会话所有参与者)
     */
    void broadcastMessage(String sessionId, Long orderId, Integer messageType, String content, String extraData);

    /**
     * 获取会话消息列表
     */
    List<FulfillmentMessageResponse> getSessionMessages(String sessionId);

    /**
     * 获取订单消息列表
     */
    List<FulfillmentMessageResponse> getOrderMessages(Long orderId);

    /**
     * 标记消息已读
     */
    void markMessagesAsRead(String sessionId, Long userId);

    /**
     * 获取未读消息数
     */
    int getUnreadMessageCount(String sessionId, Long userId);

    /**
     * 发送订单状态变更消息
     */
    void sendOrderStatusChangeMessage(Long orderId, Integer oldStatus, Integer newStatus);
}
