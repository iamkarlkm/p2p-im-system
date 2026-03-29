package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.*;
import com.im.backend.modules.merchant.order.entity.OrderFulfillmentSession;
import com.im.backend.modules.merchant.order.enums.FulfillmentSessionStatus;
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
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单履约会话服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFulfillmentSessionServiceImpl extends ServiceImpl<OrderFulfillmentSessionMapper, OrderFulfillmentSession>
        implements IOrderFulfillmentSessionService {

    private final OrderFulfillmentSessionMapper sessionMapper;
    private final IOrderFulfillmentMessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FulfillmentSessionResponse createSession(CreateFulfillmentSessionRequest request) {
        // 生成会话ID: order_{merchantId}_{orderId}_{timestamp}
        String sessionId = String.format("order_%d_%d_%d", 
                request.getMerchantId(), request.getOrderId(), System.currentTimeMillis());

        OrderFulfillmentSession session = new OrderFulfillmentSession();
        session.setSessionId(sessionId);
        session.setOrderId(request.getOrderId());
        session.setMerchantId(request.getMerchantId());
        session.setUserId(request.getUserId());
        session.setRiderId(request.getRiderId());
        session.setStatus(FulfillmentSessionStatus.ACTIVE.getCode());
        session.setCreateTime(LocalDateTime.now());
        
        if (request.getEstimatedDeliveryMinutes() != null) {
            session.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(request.getEstimatedDeliveryMinutes()));
        }

        sessionMapper.insert(session);

        // 发送系统消息: 会话创建
        messageService.sendSystemMessage(request.getOrderId(), 101, "订单履约会话已创建");

        log.info("创建订单履约会话成功: sessionId={}, orderId={}", sessionId, request.getOrderId());
        return convertToResponse(session);
    }

    @Override
    public FulfillmentSessionResponse getSessionByOrderId(Long orderId) {
        OrderFulfillmentSession session = sessionMapper.selectByOrderId(orderId);
        return session != null ? convertToResponse(session) : null;
    }

    @Override
    public FulfillmentSessionResponse getSessionBySessionId(String sessionId) {
        OrderFulfillmentSession session = sessionMapper.selectBySessionId(sessionId);
        return session != null ? convertToResponse(session) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRider(Long orderId, Long riderId) {
        sessionMapper.updateRiderId(orderId, riderId);
        
        // 发送系统消息: 骑手已分配
        messageService.sendSystemMessage(orderId, 105, "骑手已接单,即将前往商家");
        
        log.info("订单分配骑手成功: orderId={}, riderId={}", orderId, riderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endSession(String sessionId) {
        sessionMapper.endSession(sessionId);
        
        // 获取订单ID
        OrderFulfillmentSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null) {
            messageService.sendSystemMessage(session.getOrderId(), 110, "订单已完成,会话已结束");
        }
        
        log.info("结束订单履约会话: sessionId={}", sessionId);
    }

    @Override
    public List<FulfillmentSessionResponse> getActiveSessionsByUser(Long userId) {
        List<OrderFulfillmentSession> sessions = sessionMapper.selectActiveByUserId(userId);
        return sessions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FulfillmentSessionResponse> getActiveSessionsByMerchant(Long merchantId) {
        List<OrderFulfillmentSession> sessions = sessionMapper.selectActiveByMerchantId(merchantId);
        return sessions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FulfillmentSessionResponse> getActiveSessionsByRider(Long riderId) {
        List<OrderFulfillmentSession> sessions = sessionMapper.selectActiveByRiderId(riderId);
        return sessions.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public void updateEstimatedDeliveryTime(Long orderId, Integer estimatedMinutes) {
        LocalDateTime estimatedTime = LocalDateTime.now().plusMinutes(estimatedMinutes);
        sessionMapper.updateEstimatedDeliveryTime(orderId, estimatedTime);
        
        messageService.sendSystemMessage(orderId, 113, "预计送达时间已更新:" + estimatedMinutes + "分钟");
        log.info("更新预计送达时间: orderId={}, minutes={}", orderId, estimatedMinutes);
    }

    @Override
    public OrderFulfillmentSession getSessionEntity(String sessionId) {
        return sessionMapper.selectBySessionId(sessionId);
    }

    private FulfillmentSessionResponse convertToResponse(OrderFulfillmentSession session) {
        FulfillmentSessionResponse response = new FulfillmentSessionResponse();
        BeanUtils.copyProperties(session, response);
        
        FulfillmentSessionStatus status = FulfillmentSessionStatus.fromCode(session.getStatus());
        if (status != null) {
            response.setStatusDesc(status.getDesc());
        }
        
        return response;
    }
}
