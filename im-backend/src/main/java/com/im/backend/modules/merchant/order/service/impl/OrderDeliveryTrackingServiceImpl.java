package com.im.backend.modules.merchant.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.merchant.order.dto.*;
import com.im.backend.modules.merchant.order.entity.OrderDeliveryTracking;
import com.im.backend.modules.merchant.order.enums.DeliveryStatus;
import com.im.backend.modules.merchant.order.enums.SystemMessageSubType;
import com.im.backend.modules.merchant.order.repository.OrderDeliveryTrackingMapper;
import com.im.backend.modules.merchant.order.service.IOrderDeliveryTrackingService;
import com.im.backend.modules.merchant.order.service.IOrderFulfillmentMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单配送追踪服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDeliveryTrackingServiceImpl extends ServiceImpl<OrderDeliveryTrackingMapper, OrderDeliveryTracking>
        implements IOrderDeliveryTrackingService {

    private final OrderDeliveryTrackingMapper trackingMapper;
    private final IOrderFulfillmentMessageService messageService;
    private final StringRedisTemplate redisTemplate;

    private static final String RIDER_LOCATION_KEY = "rider:location:";

    @Override
    public void updateRiderLocation(RiderLocationUpdateRequest request) {
        OrderDeliveryTracking tracking = new OrderDeliveryTracking();
        BeanUtils.copyProperties(request, tracking);
        tracking.setLocationTime(LocalDateTime.now());
        tracking.setCreateTime(LocalDateTime.now());
        tracking.setUpdateTime(LocalDateTime.now());

        trackingMapper.insert(tracking);

        // 更新Redis骑手位置索引
        String key = RIDER_LOCATION_KEY + request.getOrderId();
        redisTemplate.opsForGeo().add(key, 
            new org.springframework.data.geo.Point(request.getLongitude().doubleValue(), request.getLatitude().doubleValue()),
            request.getRiderId().toString());

        // 发送位置更新消息到IM会话
        RiderLocationCardDTO card = new RiderLocationCardDTO();
        card.setRiderId(request.getRiderId());
        card.setLongitude(request.getLongitude());
        card.setLatitude(request.getLatitude());
        card.setDeliveryStatus(request.getDeliveryStatus());
        
        DeliveryStatus status = DeliveryStatus.fromCode(request.getDeliveryStatus());
        if (status != null) {
            card.setDeliveryStatusDesc(status.getDesc());
        }

        // 计算距离和预计时间(简化计算)
        if (request.getDeliveryStatus() != null && request.getDeliveryStatus() >= 4) {
            // 配送中,计算到用户距离
            Double distance = tracking.getDistanceToUser();
            if (distance != null) {
                card.setDistanceToDestination(distance);
                // 假设平均速度 5m/s = 300m/min
                card.setEstimatedMinutes((int) (distance / 300));
            }
        }

        messageService.sendLocationCardMessage(request.getOrderId(), card);

        log.debug("更新骑手位置: orderId={}, riderId={}, lat={}, lng={}", 
                request.getOrderId(), request.getRiderId(), request.getLatitude(), request.getLongitude());
    }

    @Override
    public RiderLocationResponse getRiderLatestLocation(Long orderId) {
        OrderDeliveryTracking tracking = trackingMapper.selectLatestByOrderId(orderId);
        return tracking != null ? convertToResponse(tracking) : null;
    }

    @Override
    public List<RiderLocationResponse> getDeliveryTrack(Long orderId) {
        List<OrderDeliveryTracking> tracks = trackingMapper.selectByOrderId(orderId);
        return tracks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public void updateDeliveryStatus(Long orderId, Integer status) {
        OrderDeliveryTracking tracking = new OrderDeliveryTracking();
        tracking.setOrderId(orderId);
        tracking.setDeliveryStatus(status);
        tracking.setLocationTime(LocalDateTime.now());
        trackingMapper.insert(tracking);

        log.info("更新配送状态: orderId={}, status={}", orderId, status);
    }

    @Override
    public void calculateEstimatedArrivalTime(Long orderId) {
        // 基于历史数据和当前位置计算预计送达时间
        // 简化实现:根据距离和平均速度计算
        OrderDeliveryTracking latest = trackingMapper.selectLatestByOrderId(orderId);
        if (latest != null && latest.getDistanceToUser() != null) {
            // 平均配送速度 5m/s
            int seconds = (int) (latest.getDistanceToUser() / 5);
            LocalDateTime eta = LocalDateTime.now().plusSeconds(seconds);
            
            log.info("计算预计送达时间: orderId={}, eta={}", orderId, eta);
        }
    }

    @Override
    public void riderAcceptOrder(Long orderId, Long riderId) {
        updateDeliveryStatus(orderId, DeliveryStatus.ACCEPTED.getCode());
        messageService.sendSystemMessage(orderId, SystemMessageSubType.ORDER_ACCEPTED.getCode(), "骑手已接单");
        log.info("骑手接单: orderId={}, riderId={}", orderId, riderId);
    }

    @Override
    public void riderArrivedMerchant(Long orderId) {
        updateDeliveryStatus(orderId, DeliveryStatus.ARRIVED_MERCHANT.getCode());
        messageService.sendSystemMessage(orderId, SystemMessageSubType.RIDER_ARRIVED_MERCHANT.getCode(), "骑手已到达商家");
        log.info("骑手到达商家: orderId={}", orderId);
    }

    @Override
    public void riderPickedUpMeal(Long orderId) {
        updateDeliveryStatus(orderId, DeliveryStatus.MEAL_PICKED_UP.getCode());
        messageService.sendSystemMessage(orderId, SystemMessageSubType.MEAL_PICKED_UP.getCode(), "骑手已取餐,正在配送中");
        log.info("骑手取餐: orderId={}", orderId);
    }

    @Override
    public void riderStartDelivery(Long orderId) {
        updateDeliveryStatus(orderId, DeliveryStatus.DELIVERING.getCode());
        messageService.sendSystemMessage(orderId, SystemMessageSubType.DELIVERING.getCode(), "开始配送");
        log.info("开始配送: orderId={}", orderId);
    }

    @Override
    public void riderArrivedUser(Long orderId) {
        updateDeliveryStatus(orderId, DeliveryStatus.ARRIVED_USER.getCode());
        messageService.sendSystemMessage(orderId, SystemMessageSubType.ARRIVED_USER.getCode(), "骑手已送达");
        log.info("骑手送达: orderId={}", orderId);
    }

    private RiderLocationResponse convertToResponse(OrderDeliveryTracking tracking) {
        RiderLocationResponse response = new RiderLocationResponse();
        BeanUtils.copyProperties(tracking, response);
        
        DeliveryStatus status = DeliveryStatus.fromCode(tracking.getDeliveryStatus());
        if (status != null) {
            response.setDeliveryStatusDesc(status.getDesc());
        }
        
        return response;
    }
}
