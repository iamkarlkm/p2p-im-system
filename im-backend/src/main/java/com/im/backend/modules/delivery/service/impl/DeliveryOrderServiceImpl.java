package com.im.backend.modules.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.delivery.dto.*;
import com.im.backend.modules.delivery.entity.DeliveryOrder;
import com.im.backend.modules.delivery.entity.RiderLocation;
import com.im.backend.modules.delivery.enums.DeliveryOrderStatus;
import com.im.backend.modules.delivery.repository.DeliveryOrderMapper;
import com.im.backend.modules.delivery.repository.RiderLocationMapper;
import com.im.backend.modules.delivery.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl implements IDeliveryOrderService {
    
    private final DeliveryOrderMapper orderMapper;
    private final RiderLocationMapper locationMapper;
    
    @Override
    @Transactional
    public DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request) {
        DeliveryOrder order = new DeliveryOrder();
        BeanUtils.copyProperties(request, order);
        order.setOrderNo(generateOrderNo());
        order.setStatus(DeliveryOrderStatus.PENDING_ASSIGN.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.insert(order);
        log.info("配送订单创建成功: orderNo={}", order.getOrderNo());
        
        return convertToResponse(order);
    }
    
    @Override
    public DeliveryOrderResponse getOrderById(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        return order != null ? convertToResponse(order) : null;
    }
    
    @Override
    public List<DeliveryOrderResponse> getUserOrders(Long userId) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryOrder::getUserId, userId)
               .orderByDesc(DeliveryOrder::getCreateTime);
        return orderMapper.selectList(wrapper).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DeliveryOrderResponse> getRiderActiveOrders(Long riderId) {
        return orderMapper.selectActiveOrdersByRider(riderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean assignOrder(Long orderId, Long riderId) {
        int rows = orderMapper.assignOrder(orderId, riderId);
        if (rows > 0) {
            log.info("订单分配成功: orderId={}, riderId={}", orderId, riderId);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean riderAcceptOrder(Long orderId, Long riderId) {
        return updateOrderStatus(orderId, DeliveryOrderStatus.RIDER_ACCEPTED.getCode());
    }
    
    @Override
    @Transactional
    public boolean markArrivedPickup(Long orderId, Long riderId) {
        return updateOrderStatus(orderId, DeliveryOrderStatus.ARRIVED_PICKUP.getCode());
    }
    
    @Override
    @Transactional
    public boolean markPickedUp(Long orderId, Long riderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(DeliveryOrderStatus.PICKED_UP.getCode());
            order.setPickupTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean markArrivedDelivery(Long orderId, Long riderId) {
        return updateOrderStatus(orderId, DeliveryOrderStatus.ARRIVED_DELIVERY.getCode());
    }
    
    @Override
    @Transactional
    public boolean markDelivered(Long orderId, Long riderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(DeliveryOrderStatus.DELIVERED.getCode());
            order.setDeliveryTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean completeOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(DeliveryOrderStatus.COMPLETED.getCode());
            order.setCompletionTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, String reason, Integer cancelType) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(DeliveryOrderStatus.CANCELLED.getCode());
            order.setCancelReason(reason);
            order.setCancelType(cancelType);
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            log.info("订单取消: orderId={}, reason={}", orderId, reason);
            return true;
        }
        return false;
    }
    
    @Override
    public List<RiderLocationResponse> getOrderTrajectory(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getRiderId() == null) {
            return List.of();
        }
        
        List<RiderLocation> locations = locationMapper.selectRecentLocations(order.getRiderId());
        return locations.stream().map(loc -> {
            RiderLocationResponse resp = new RiderLocationResponse();
            resp.setRiderId(loc.getRiderId());
            resp.setLatitude(loc.getLatitude());
            resp.setLongitude(loc.getLongitude());
            resp.setSpeed(loc.getSpeed());
            resp.setLocation(loc.getLocation());
            return resp;
        }).collect(Collectors.toList());
    }
    
    private boolean updateOrderStatus(Long orderId, Integer status) {
        int rows = orderMapper.updateStatus(orderId, status);
        return rows > 0;
    }
    
    private String generateOrderNo() {
        return "DEL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private DeliveryOrderResponse convertToResponse(DeliveryOrder order) {
        DeliveryOrderResponse resp = new DeliveryOrderResponse();
        BeanUtils.copyProperties(order, resp);
        resp.setStatusDesc(getStatusDesc(order.getStatus()));
        return resp;
    }
    
    private String getStatusDesc(Integer status) {
        for (DeliveryOrderStatus s : DeliveryOrderStatus.values()) {
            if (s.getCode().equals(status)) {
                return s.getDesc();
            }
        }
        return "未知状态";
    }
}
