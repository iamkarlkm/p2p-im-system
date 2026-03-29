package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.*;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentMessage;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentSession;
import com.im.backend.modules.merchant.order.enums.FulfillmentMessageType;
import com.im.backend.modules.merchant.order.enums.SenderType;
import com.im.backend.modules.merchant.order.enums.SystemMessageSubType;
import com.im.backend.modules.merchant.order.repository.OrderFulfillmentMessageMapper;
import com.im.backend.modules.merchant.order.repository.OrderFulfillmentSessionMapper;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentMessageService;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单履约消息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFulfillmentMessageServiceImpl extends ServiceImpl<OrderFulfillmentMessageMapper, OrderFulfillmentMessage>
        implements IOrderFulfillmentMessageService {

    private final OrderFulfillmentMessageMapper messageMapper;
    private final OrderFulfillmentSessionMapper sessionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemMessage(Long orderId, Integer messageSubType, String content) {
        OrderFulfillmentSession session = sessionMapper.selectByOrderId(orderId);
        if (session == null) {
            log.warn("订单会话不存在,无法发送系统消息: orderId={}", orderId);
            return;
        }

        OrderFulfillmentMessage message = new OrderFulfillmentMessage();
        message.setSessionId(session.getSessionId());
        message.setOrderId(orderId);
        message.setMessageType(FulfillmentMessageType.SYSTEM.getCode());
        message.setMessageSubType(messageSubType);
        message.setSenderId(0L); // 系统发送者
        message.setSenderType(SenderType.SYSTEM.getCode());
        message.setContent(content);
        message.setReadStatus(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);

        // TODO: 通过WebSocket推送给会话参与者
        
        log.debug("发送系统消息: orderId={}, subType={}, content={}", orderId, messageSubType, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTextMessage(SendFulfillmentMessageRequest request, Long senderId, Integer senderType) {
        OrderFulfillmentMessage message = new OrderFulfillmentMessage();
        message.setSessionId(request.getSessionId());
        message.setOrderId(request.getOrderId());
        message.setMessageType(FulfillmentMessageType.TEXT.getCode());
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setContent(request.getContent());
        message.setExtraData(request.getExtraData());
        message.setReadStatus(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);

        log.debug("发送文本消息: sessionId={}, senderId={}", request.getSessionId(), senderId);
    }

    @Override
    public void sendLocationCardMessage(Long orderId, RiderLocationCardDTO locationCard) {
        OrderFulfillmentSession session = sessionMapper.selectByOrderId(orderId);
        if (session == null) {
            return;
        }

        // 将位置卡片数据转为JSON作为extraData
        String extraData = String.format("{\"riderId\":%d,\"lat\":%s,\"lng\":%s,\"status\":%d,\"distance\":%s,\"eta\":%d}",
                locationCard.getRiderId(), locationCard.getLatitude(), locationCard.getLongitude(),
                locationCard.getDeliveryStatus(), locationCard.getDistanceToDestination(), locationCard.getEstimatedMinutes());

        OrderFulfillmentMessage message = new OrderFulfillmentMessage();
        message.setSessionId(session.getSessionId());
        message.setOrderId(orderId);
        message.setMessageType(FulfillmentMessageType.CARD.getCode());
        message.setMessageSubType(SystemMessageSubType.LOCATION_SHARED.getCode());
        message.setSenderId(locationCard.getRiderId());
        message.setSenderType(SenderType.RIDER.getCode());
        message.setContent("骑手位置更新");
        message.setExtraData(extraData);
        message.setReadStatus(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);
    }

    @Override
    public void broadcastMessage(String sessionId, Long orderId, Integer messageType, String content, String extraData) {
        OrderFulfillmentMessage message = new OrderFulfillmentMessage();
        message.setSessionId(sessionId);
        message.setOrderId(orderId);
        message.setMessageType(messageType);
        message.setSenderId(0L);
        message.setSenderType(SenderType.SYSTEM.getCode());
        message.setContent(content);
        message.setExtraData(extraData);
        message.setReadStatus(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);
    }

    @Override
    public List<FulfillmentMessageResponse> getSessionMessages(String sessionId) {
        List<OrderFulfillmentMessage> messages = messageMapper.selectBySessionId(sessionId);
        return messages.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FulfillmentMessageResponse> getOrderMessages(Long orderId) {
        List<OrderFulfillmentMessage> messages = messageMapper.selectByOrderId(orderId);
        return messages.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(String sessionId, Long userId) {
        messageMapper.markAsRead(sessionId, userId);
    }

    @Override
    public int getUnreadMessageCount(String sessionId, Long userId) {
        List<OrderFulfillmentMessage> unread = messageMapper.selectUnreadMessages(sessionId, userId);
        return unread != null ? unread.size() : 0;
    }

    @Override
    public void sendOrderStatusChangeMessage(Long orderId, Integer oldStatus, Integer newStatus) {
        String content = String.format("订单状态变更: %d -> %d", oldStatus, newStatus);
        sendSystemMessage(orderId, 0, content);
    }

    private FulfillmentMessageResponse convertToResponse(OrderFulfillmentMessage message) {
        FulfillmentMessageResponse response = new FulfillmentMessageResponse();
        BeanUtils.copyProperties(message, response);

        FulfillmentMessageType msgType = FulfillmentMessageType.fromCode(message.getMessageType());
        if (msgType != null) {
            response.setMessageTypeDesc(msgType.getDesc());
        }

        SystemMessageSubType subType = SystemMessageSubType.fromCode(message.getMessageSubType());
        if (subType != null) {
            response.setMessageSubTypeDesc(subType.getDesc());
        }

        SenderType senderType = SenderType.fromCode(message.getSenderType());
        if (senderType != null) {
            response.setSenderTypeDesc(senderType.getDesc());
        }

        return response;
    }
}
