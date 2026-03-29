package com.im.backend.modules.logistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.common.util.GeoUtils;
import com.im.backend.common.util.OrderNoGenerator;
import com.im.backend.modules.logistics.dto.*;
import com.im.backend.modules.logistics.entity.DeliveryOrder;
import com.im.backend.modules.logistics.entity.DeliveryRider;
import com.im.backend.modules.logistics.repository.DeliveryOrderMapper;
import com.im.backend.modules.logistics.repository.DeliveryRiderMapper;
import com.im.backend.modules.logistics.service.ISmartDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能配送调度服务实现
 * 基于地理位置的智能派单引擎
 */
@Slf4j
@Service
public class SmartDispatchServiceImpl implements ISmartDispatchService {

    @Autowired
    private DeliveryOrderMapper orderMapper;

    @Autowired
    private DeliveryRiderMapper riderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String ORDER_LOCK_KEY = "delivery:order:lock:";
    private static final String RIDER_LOCATION_KEY = "rider:location:";

    @Override
    @Transactional
    public DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request) {
        DeliveryOrder order = new DeliveryOrder();
        BeanUtils.copyProperties(request, order);
        
        order.setOrderNo(OrderNoGenerator.generateDeliveryOrderNo());
        order.setStatus(1);
        
        BigDecimal merchantLng = request.getMerchantLongitude();
        BigDecimal merchantLat = request.getMerchantLatitude();
        BigDecimal deliveryLng = request.getDeliveryLongitude();
        BigDecimal deliveryLat = request.getDeliveryLatitude();
        
        double distance = GeoUtils.calculateDistance(
            merchantLng.doubleValue(), merchantLat.doubleValue(),
            deliveryLng.doubleValue(), deliveryLat.doubleValue()
        );
        order.setDeliveryDistance((int) distance);
        
        int estimatedMinutes = calculateEstimatedDeliveryTime((int) distance);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(estimatedMinutes));
        
        if (request.getDeliveryFee() == null) {
            order.setDeliveryFee(calculateDeliveryFee((int) distance));
        }
        
        orderMapper.insert(order);
        
        log.info("创建配送订单成功: orderNo={}, distance={}m", order.getOrderNo(), (int) distance);
        
        return convertToResponse(order);
    }

    @Override
    @Transactional
    public boolean dispatchOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 1) {
            log.warn("订单不存在或状态不正确: orderId={}", orderId);
            return false;
        }

        List<DeliveryRider> onlineRiders = riderMapper.selectOnlineIdleRiders();
        if (onlineRiders.isEmpty()) {
            log.warn("当前没有空闲骑手: orderId={}", orderId);
            return false;
        }

        DeliveryRider bestRider = findBestRider(order, onlineRiders);
        if (bestRider == null) {
            log.warn("未找到合适骑手: orderId={}", orderId);
            return false;
        }

        boolean assigned = assignOrderToRider(order, bestRider);
        if (assigned) {
            riderMapper.updateWorkStatus(bestRider.getId(), 2);
            log.info("派单成功: orderId={}, riderId={}", orderId, bestRider.getId());
        }

        return assigned;
    }

    @Override
    @Transactional
    public int batchDispatchOrders() {
        List<DeliveryOrder> pendingOrders = orderMapper.selectPendingOrders(50);
        int successCount = 0;

        for (DeliveryOrder order : pendingOrders) {
            try {
                if (dispatchOrder(order.getId())) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("派单失败: orderId={}", order.getId(), e);
            }
        }

        log.info("批量派单完成: 总订单={}, 成功={}", pendingOrders.size(), successCount);
        return successCount;
    }

    @Override
    @Transactional
    public boolean acceptOrder(Long riderId, Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 2) {
            return false;
        }

        if (!order.getRiderId().equals(riderId)) {
            return false;
        }

        orderMapper.updateStatus(orderId, 3);
        return true;
    }

    @Override
    @Transactional
    public boolean pickupOrder(Long riderId, Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return false;
        }

        order.setStatus(4);
        order.setPickupTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return true;
    }

    @Override
    @Transactional
    public boolean deliverOrder(Long riderId, Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return false;
        }

        order.setStatus(5);
        order.setDeliverTime(LocalDateTime.now());
        
        int actualDuration = (int) ChronoUnit.MINUTES.between(order.getCreateTime(), order.getDeliverTime());
        order.setDeliveryDuration(actualDuration);
        
        orderMapper.updateById(order);
        return true;
    }

    @Override
    @Transactional
    public boolean completeOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 5) {
            return false;
        }

        order.setStatus(6);
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        riderMapper.incrementOrderCount(order.getRiderId());
        riderMapper.updateWorkStatus(order.getRiderId(), 1);
        
        return true;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, String reason) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() > 4) {
            return false;
        }

        order.setStatus(7);
        order.setCancelReason(reason);
        orderMapper.updateById(order);
        
        if (order.getRiderId() != null) {
            riderMapper.updateWorkStatus(order.getRiderId(), 1);
        }
        
        return true;
    }

    @Override
    public DeliveryOrderResponse getOrderDetail(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        return order != null ? convertToResponse(order) : null;
    }

    @Override
    public List<DeliveryOrderResponse> getOrderList(Integer status, Long merchantId, Long userId) {
        LambdaQueryWrapper<DeliveryOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(DeliveryOrder::getStatus, status);
        }
        if (merchantId != null) {
            wrapper.eq(DeliveryOrder::getMerchantId, merchantId);
        }
        if (userId != null) {
            wrapper.eq(DeliveryOrder::getUserId, userId);
        }
        
        wrapper.orderByDesc(DeliveryOrder::getCreateTime);
        List<DeliveryOrder> orders = orderMapper.selectList(wrapper);
        
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean reassignOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 2) {
            return false;
        }

        if (order.getRiderId() != null) {
            riderMapper.updateWorkStatus(order.getRiderId(), 1);
        }

        order.setRiderId(null);
        order.setRiderName(null);
        order.setRiderPhone(null);
        order.setStatus(1);
        orderMapper.updateById(order);
        
        return dispatchOrder(orderId);
    }

    private DeliveryRider findBestRider(DeliveryOrder order, List<DeliveryRider> riders) {
        BigDecimal merchantLng = order.getMerchantLongitude();
        BigDecimal merchantLat = order.getMerchantLatitude();

        return riders.stream()
            .filter(r -> r.getCurrentLongitude() != null && r.getCurrentLatitude() != null)
            .min(Comparator.comparingDouble(r -> {
                double distance = GeoUtils.calculateDistance(
                    r.getCurrentLongitude().doubleValue(),
                    r.getCurrentLatitude().doubleValue(),
                    merchantLng.doubleValue(),
                    merchantLat.doubleValue()
                );
                double score = distance;
                if (r.getRating() != null && r.getRating().doubleValue() > 4.5) {
                    score *= 0.9;
                }
                return score;
            }))
            .orElse(null);
    }

    private boolean assignOrderToRider(DeliveryOrder order, DeliveryRider rider) {
        int result = orderMapper.assignRider(order.getId(), rider.getId(), 
            rider.getRealName(), rider.getPhone(), 2);
        return result > 0;
    }

    private int calculateEstimatedDeliveryTime(int distanceMeters) {
        int baseTime = 15;
        int distanceTime = distanceMeters / 200;
        return baseTime + distanceTime;
    }

    private BigDecimal calculateDeliveryFee(int distanceMeters) {
        BigDecimal baseFee = new BigDecimal("5.00");
        BigDecimal distanceKm = new BigDecimal(distanceMeters).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP);
        BigDecimal extraFee = distanceKm.multiply(new BigDecimal("2.00"));
        return baseFee.add(extraFee).setScale(2, RoundingMode.HALF_UP);
    }

    private DeliveryOrderResponse convertToResponse(DeliveryOrder order) {
        DeliveryOrderResponse response = new DeliveryOrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setStatusDesc(getStatusDesc(order.getStatus()));
        return response;
    }

    private String getStatusDesc(Integer status) {
        switch (status) {
            case 1: return "待分配";
            case 2: return "已分配";
            case 3: return "已取货";
            case 4: return "配送中";
            case 5: return "已送达";
            case 6: return "已完成";
            case 7: return "已取消";
            default: return "未知";
        }
    }
}
