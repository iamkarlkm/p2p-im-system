package com.im.local.delivery.service.impl;

import com.im.local.delivery.dto.*;
import com.im.local.delivery.entity.DeliveryOrder;
import com.im.local.delivery.entity.DeliveryRider;
import com.im.local.delivery.enums.DeliveryOrderStatus;
import com.im.local.delivery.repository.DeliveryOrderMapper;
import com.im.local.delivery.repository.DeliveryRiderMapper;
import com.im.local.delivery.repository.RiderLocationMapper;
import com.im.local.delivery.service.IDeliveryOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 配送订单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryOrderServiceImpl implements IDeliveryOrderService {
    
    private final DeliveryOrderMapper orderMapper;
    private final DeliveryRiderMapper riderMapper;
    private final RiderLocationMapper locationMapper;
    
    @Override
    @Transactional
    public DeliveryOrderResponse createOrder(CreateDeliveryOrderRequest request) {
        DeliveryOrder order = new DeliveryOrder();
        order.setMerchantOrderId(request.getMerchantOrderId());
        order.setMerchantId(request.getMerchantId());
        order.setUserId(request.getUserId());
        order.setDeliveryNo(generateDeliveryNo());
        order.setStatus(DeliveryOrderStatus.PENDING_ASSIGN.getCode());
        
        // 地址信息
        order.setPickupAddress(request.getPickupAddress());
        order.setPickupLat(request.getPickupLat());
        order.setPickupLng(request.getPickupLng());
        order.setPickupContact(request.getPickupContact());
        order.setPickupPhone(request.getPickupPhone());
        
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryLat(request.getDeliveryLat());
        order.setDeliveryLng(request.getDeliveryLng());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        
        // 计算距离和费用
        double distance = calculateDistance(
            request.getPickupLat().doubleValue(), request.getPickupLng().doubleValue(),
            request.getDeliveryLat().doubleValue(), request.getDeliveryLng().doubleValue()
        );
        order.setDistance((int) distance);
        order.setEstimatedDuration(calculateEstimatedDuration(distance));
        
        BigDecimal deliveryFee = request.getDeliveryFee() != null ? 
            request.getDeliveryFee() : calculateDeliveryFee(distance, 
                request.getItemWeight() != null ? request.getItemWeight().doubleValue() : 1.0);
        order.setDeliveryFee(deliveryFee);
        
        order.setItemType(request.getItemType());
        order.setItemWeight(request.getItemWeight());
        order.setRemark(request.getRemark());
        order.setSignCode(generateSignCode());
        
        if (request.getExpectDeliverTime() != null) {
            order.setExpectDeliverTime(LocalDateTime.parse(request.getExpectDeliverTime(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        order.setDeliveryType(request.getDeliveryType());
        
        orderMapper.insert(order);
        
        // 自动分配骑手
        assignRider(order.getId());
        
        return convertToResponse(order);
    }
    
    @Override
    @Transactional
    public boolean assignRider(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != DeliveryOrderStatus.PENDING_ASSIGN.getCode()) {
            return false;
        }
        
        Long riderId = findOptimalRider(order);
        if (riderId == null) {
            log.warn("No available rider for order: {}", orderId);
            return false;
        }
        
        orderMapper.assignRider(orderId, riderId);
        riderMapper.updateStatus(riderId, 2); // 接单中
        
        log.info("Order {} assigned to rider {}", orderId, riderId);
        return true;
    }
    
    @Override
    public DeliveryOrderResponse getOrderDetail(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        return convertToResponse(order);
    }
    
    @Override
    public List<DeliveryOrderResponse> getUserOrders(Long userId, Integer limit) {
        List<DeliveryOrder> orders = orderMapper.selectByUser(userId, limit);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    public List<DeliveryOrderResponse> getRiderActiveOrders(Long riderId) {
        List<DeliveryOrder> orders = orderMapper.selectActiveByRider(riderId);
        return orders.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean markPickedUp(Long orderId, Long riderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return false;
        }
        
        orderMapper.markPickedUp(orderId);
        riderMapper.updateStatus(riderId, 4); // 配送中
        return true;
    }
    
    @Override
    @Transactional
    public boolean markDelivered(Long orderId, Long riderId, String signImageUrl) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return false;
        }
        
        orderMapper.markDelivered(orderId, signImageUrl);
        riderMapper.updateStatus(riderId, 1); // 恢复空闲
        riderMapper.incrementOrderCount(riderId);
        return true;
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, String reason) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() > DeliveryOrderStatus.PICKING_UP.getCode()) {
            return false;
        }
        
        if (order.getRiderId() != null) {
            riderMapper.updateStatus(order.getRiderId(), 1); // 恢复骑手空闲
        }
        
        orderMapper.updateStatus(orderId, DeliveryOrderStatus.CANCELLED.getCode());
        return true;
    }
    
    @Override
    public BigDecimal calculateDeliveryFee(Double distance, Double weight) {
        // 基础费用 + 距离费用 + 重量费用
        BigDecimal baseFee = new BigDecimal("5.00");
        BigDecimal distanceFee = new BigDecimal(distance / 1000).multiply(new BigDecimal("2.00"));
        BigDecimal weightFee = new BigDecimal(weight > 5 ? (weight - 5) * 1.5 : 0);
        return baseFee.add(distanceFee).add(weightFee).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public List<RiderLocationResponse> getOrderTrajectory(Long orderId) {
        return locationMapper.selectByDeliveryOrder(orderId).stream()
            .map(loc -> {
                RiderLocationResponse resp = new RiderLocationResponse();
                resp.setLat(loc.getLat());
                resp.setLng(loc.getLng());
                resp.setSpeed(loc.getSpeed());
                resp.setDirection(loc.getDirection());
                resp.setLocatedAt(loc.getLocatedAt());
                resp.setAddress(loc.getAddress());
                return resp;
            }).collect(Collectors.toList());
    }
    
    @Override
    public Long findOptimalRider(DeliveryOrder order) {
        // 获取可用骑手列表
        List<DeliveryRider> availableRiders = riderMapper.selectAllAvailable();
        
        if (availableRiders.isEmpty()) {
            return null;
        }
        
        // 计算每个骑手的评分并排序
        return availableRiders.stream()
            .map(rider -> new RiderScore(rider, calculateRiderScore(rider, order)))
            .sorted((a, b) -> Double.compare(b.score, a.score))
            .findFirst()
            .map(rs -> rs.rider.getId())
            .orElse(null);
    }
    
    /**
     * 计算骑手评分（距离40% + 评分30% + 今日单量20% + 速度10%）
     */
    private double calculateRiderScore(DeliveryRider rider, DeliveryOrder order) {
        if (rider.getCurrentLat() == null || rider.getCurrentLng() == null) {
            return 0;
        }
        
        double distance = haversineDistance(
            rider.getCurrentLat().doubleValue(), rider.getCurrentLng().doubleValue(),
            order.getPickupLat().doubleValue(), order.getPickupLng().doubleValue()
        );
        
        double distanceScore = Math.max(0, 1 - distance / 5000) * 40;
        double ratingScore = (rider.getRating() != null ? rider.getRating().doubleValue() : 4.5) / 5 * 30;
        double orderCountScore = Math.min(rider.getTodayOrderCount() != null ? 
            rider.getTodayOrderCount() : 0, 50) / 50.0 * 20;
        double speedScore = 10; // 默认速度分
        
        return distanceScore + ratingScore + orderCountScore + speedScore;
    }
    
    private DeliveryOrderResponse convertToResponse(DeliveryOrder order) {
        DeliveryOrderResponse resp = new DeliveryOrderResponse();
        resp.setId(order.getId());
        resp.setDeliveryNo(order.getDeliveryNo());
        resp.setStatus(order.getStatus());
        resp.setStatusName(DeliveryOrderStatus.fromCode(order.getStatus()) != null ? 
            DeliveryOrderStatus.fromCode(order.getStatus()).getName() : "未知");
        resp.setRiderId(order.getRiderId());
        
        if (order.getRiderId() != null) {
            DeliveryRider rider = riderMapper.selectById(order.getRiderId());
            if (rider != null) {
                resp.setRiderName(rider.getRealName());
                resp.setRiderPhone(rider.getPhone());
                resp.setRiderRating(rider.getRating());
                resp.setRiderLat(rider.getCurrentLat());
                resp.setRiderLng(rider.getCurrentLng());
            }
        }
        
        resp.setPickupAddress(order.getPickupAddress());
        resp.setDeliveryAddress(order.getDeliveryAddress());
        resp.setDistance(order.getDistance());
        resp.setEstimatedDuration(order.getEstimatedDuration());
        resp.setRemainingMinutes(order.getRemainingMinutes());
        resp.setProgressPercent(order.getProgressPercent());
        resp.setDeliveryFee(order.getDeliveryFee());
        resp.setIsTimeout(order.isTimeout());
        resp.setCreatedAt(order.getCreatedAt());
        resp.setAssignedAt(order.getAssignedAt());
        resp.setPickedUpAt(order.getPickedUpAt());
        resp.setDeliveredAt(order.getDeliveredAt());
        resp.setExpectDeliverTime(order.getExpectDeliverTime());
        
        return resp;
    }
    
    private String generateDeliveryNo() {
        return "DL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
            + String.format("%04d", new Random().nextInt(10000));
    }
    
    private String generateSignCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        return haversineDistance(lat1, lng1, lat2, lng2);
    }
    
    private int calculateEstimatedDuration(double distanceMeters) {
        // 假设平均速度25km/h，加上取货和交付时间
        int travelMinutes = (int) (distanceMeters / 1000 / 25 * 60);
        return travelMinutes + 10; // 加10分钟取货和交付时间
    }
    
    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    @RequiredArgsConstructor
    private static class RiderScore {
        final DeliveryRider rider;
        final double score;
    }
}
