package com.im.backend.modules.merchant.logistics.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.logistics.dto.DeliveryOrderCreateRequest;
import com.im.backend.modules.merchant.logistics.entity.LogisticsDeliveryOrder;
import com.im.backend.modules.merchant.logistics.entity.LogisticsRider;
import com.im.backend.modules.merchant.logistics.repository.LogisticsDeliveryOrderMapper;
import com.im.backend.modules.merchant.logistics.repository.LogisticsRiderMapper;
import com.im.backend.modules.merchant.logistics.service.ILogisticsDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 物流配送服务实现 - 功能#311: 本地物流配送调度
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogisticsDeliveryServiceImpl extends ServiceImpl<LogisticsDeliveryOrderMapper, LogisticsDeliveryOrder> implements ILogisticsDeliveryService {

    private final LogisticsDeliveryOrderMapper orderMapper;
    private final LogisticsRiderMapper riderMapper;

    @Override
    @Transactional
    public Result<LogisticsDeliveryOrder> createOrder(Long userId, DeliveryOrderCreateRequest request) {
        LogisticsDeliveryOrder order = new LogisticsDeliveryOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(request.getMerchantId());
        order.setStatus(1); // 待分配
        order.setPickupAddress(request.getPickupAddress());
        order.setPickupLng(request.getPickupLng());
        order.setPickupLat(request.getPickupLat());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLng(request.getDeliveryLng());
        order.setDeliveryLat(request.getDeliveryLat());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setRemark(request.getRemark());
        
        // 计算距离和配送费 (简化计算)
        int distance = calculateDistance(request.getPickupLat(), request.getPickupLng(), 
                                          request.getDeliveryLat(), request.getDeliveryLng());
        order.setDistance(distance);
        order.setDeliveryFee(calculateDeliveryFee(distance));
        order.setEstimatedArrivalTime(LocalDateTime.now().plusMinutes(45));

        orderMapper.insert(order);
        log.info("创建配送订单: {}", order.getOrderNo());
        return Result.success(order);
    }

    @Override
    @Transactional
    public Result<Void> acceptOrder(Long riderId, Long orderId) {
        LogisticsRider rider = riderMapper.selectById(riderId);
        if (rider == null || rider.getWorkStatus() != 1) {
            return Result.error("骑手状态不可用");
        }
        
        LogisticsDeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 1) {
            return Result.error("订单状态不正确");
        }
        
        orderMapper.assignRider(orderId, riderId);
        riderMapper.updateWorkStatus(riderId, 2); // 忙碌
        
        log.info("骑手{}接单: {}", riderId, orderId);
        return Result.success();
    }

    @Override
    public Result<Void> updateStatus(Long riderId, Long orderId, Integer status) {
        LogisticsDeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return Result.error("订单不存在或无权限");
        }
        orderMapper.updateStatus(orderId, status);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> completeDelivery(Long riderId, Long orderId) {
        LogisticsDeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return Result.error("订单不存在或无权限");
        }
        orderMapper.completeDelivery(orderId);
        riderMapper.updateWorkStatus(riderId, 1); // 空闲
        riderMapper.incrementOrderCount(riderId);
        return Result.success();
    }

    @Override
    public IPage<LogisticsDeliveryOrder> getRiderOrders(Long riderId, Integer status, Page<LogisticsDeliveryOrder> page) {
        return orderMapper.selectByRiderId(page, riderId, status);
    }

    @Override
    public IPage<LogisticsDeliveryOrder> getMerchantOrders(Long merchantId, Integer status, Page<LogisticsDeliveryOrder> page) {
        return orderMapper.selectByMerchantId(page, merchantId, status);
    }

    @Override
    public Result<Void> cancelOrder(Long userId, Long orderId, String reason) {
        LogisticsDeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在或无权限");
        }
        if (order.getStatus() > 3) {
            return Result.error("订单状态不可取消");
        }
        order.setStatus(6); // 异常/取消
        order.setCancelReason(reason);
        orderMapper.updateById(order);
        return Result.success();
    }

    private String generateOrderNo() {
        return "DL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
               + String.format("%04d", new Random().nextInt(10000));
    }

    private int calculateDistance(java.math.BigDecimal lat1, java.math.BigDecimal lng1, 
                                   java.math.BigDecimal lat2, java.math.BigDecimal lng2) {
        // 简化距离计算，实际应使用高德/百度地图API
        double R = 6371000;
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue())) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (int) (R * c);
    }

    private java.math.BigDecimal calculateDeliveryFee(int distance) {
        // 基础配送费5元，每公里加1元
        return new java.math.BigDecimal(5 + (distance / 1000)).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
