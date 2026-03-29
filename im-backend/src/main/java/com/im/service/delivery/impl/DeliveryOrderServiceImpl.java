package com.im.service.delivery.impl;

import com.im.entity.delivery.DeliveryOrder;
import com.im.entity.delivery.DeliveryRider;
import com.im.entity.delivery.RiderLocation;
import com.im.service.delivery.DeliveryOrderService;
import com.im.service.delivery.RiderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 配送订单服务实现类 - 即时配送运力调度系统
 * 实现智能派单、订单调度、状态流转等核心逻辑
 */
@Slf4j
@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RiderService riderService;

    private static final String ORDER_KEY_PREFIX = "delivery:order:";
    private static final String PENDING_QUEUE_KEY = "delivery:pending_orders";
    private static final String RIDER_LOCATION_KEY = "delivery:rider:location:";

    @Override
    public DeliveryOrder createOrder(DeliveryOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus("WAITING");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setDeleted(false);
        
        // 计算配送距离
        int distance = calculateDistance(
            order.getMerchantLatitude(), order.getMerchantLongitude(),
            order.getDeliveryLatitude(), order.getDeliveryLongitude()
        );
        order.setDeliveryDistance(distance);
        
        // 计算预计送达时间(基于距离估算)
        int estimatedMinutes = estimateDeliveryTime(distance);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(estimatedMinutes));
        
        // 保存到Redis
        saveOrderToRedis(order);
        
        // 加入待分配队列
        redisTemplate.opsForList().rightPush(PENDING_QUEUE_KEY, order.getId());
        
        log.info("创建配送订单: {}, 订单号: {}, 距离: {}米", order.getId(), order.getOrderNo(), distance);
        return order;
    }

    @Override
    public DeliveryOrder getOrderById(Long orderId) {
        String key = ORDER_KEY_PREFIX + orderId;
        return (DeliveryOrder) redisTemplate.opsForValue().get(key);
    }

    @Override
    public DeliveryOrder getOrderByOrderNo(String orderNo) {
        // 通过订单号查询订单ID，再获取订单
        String orderIdStr = (String) redisTemplate.opsForValue().get("delivery:order_no:" + orderNo);
        if (orderIdStr != null) {
            return getOrderById(Long.valueOf(orderIdStr));
        }
        return null;
    }

    @Override
    public DeliveryOrder assignOrderToRider(Long orderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || !"WAITING".equals(order.getStatus())) {
            log.warn("订单无法分配, 订单ID: {}, 状态: {}", orderId, order != null ? order.getStatus() : "null");
            return order;
        }

        // 智能派单算法：寻找最优骑手
        DeliveryRider optimalRider = findOptimalRider(order);
        
        if (optimalRider == null) {
            log.warn("未找到可用骑手, 订单ID: {}", orderId);
            return order;
        }

        // 分配订单给骑手
        order.assignToRider(optimalRider.getId(), optimalRider.getName(), optimalRider.getPhone());
        order.setUpdatedAt(LocalDateTime.now());
        
        // 更新骑手状态
        riderService.incrementActiveOrderCount(optimalRider.getId());
        
        // 保存更新
        saveOrderToRedis(order);
        
        // 从待分配队列移除
        redisTemplate.opsForList().remove(PENDING_QUEUE_KEY, 0, orderId);
        
        log.info("订单分配成功, 订单ID: {}, 骑手ID: {}, 骑手: {}", 
                 orderId, optimalRider.getId(), optimalRider.getName());
        
        return order;
    }

    /**
     * 智能派单算法 - 寻找最优骑手
     * 综合考虑：距离、方向、骑手负载、评分
     */
    private DeliveryRider findOptimalRider(DeliveryOrder order) {
        // 获取商家位置附近可用骑手
        List<DeliveryRider> nearbyRiders = riderService.findNearbyAvailableRiders(
            order.getMerchantLongitude(), 
            order.getMerchantLatitude(), 
            5000 // 5公里范围
        );

        if (nearbyRiders.isEmpty()) {
            return null;
        }

        // 计算每个骑手的综合得分
        Map<DeliveryRider, Double> riderScores = new HashMap<>();
        
        for (DeliveryRider rider : nearbyRiders) {
            double score = calculateRiderScore(rider, order);
            riderScores.put(rider, score);
        }

        // 选择得分最高的骑手
        return riderScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * 计算骑手综合得分
     * 得分越高越优先派单
     */
    private double calculateRiderScore(DeliveryRider rider, DeliveryOrder order) {
        // 获取骑手实时位置
        RiderLocation location = riderService.getRiderCurrentLocation(rider.getId());
        if (location == null) {
            return 0.0;
        }

        // 1. 距离因子 (40%) - 越近越好
        double distanceToMerchant = location.distanceTo(new RiderLocation() {{
            setLongitude(order.getMerchantLongitude());
            setLatitude(order.getMerchantLatitude());
        }});
        double distanceScore = Math.max(0, 1.0 - distanceToMerchant / 5000.0) * 40;

        // 2. 负载因子 (25%) - 负载越低越好
        double loadFactor = 1.0 - (double) rider.getActiveOrderCount() / rider.getMaxOrderLimit();
        double loadScore = loadFactor * 25;

        // 3. 评分因子 (20%) - 评分越高越好
        double ratingScore = (rider.getRating() != null ? rider.getRating().doubleValue() : 4.0) / 5.0 * 20;

        // 4. 方向因子 (10%) - 与配送方向一致更好(简化处理)
        double directionScore = 10; // 默认满分

        // 5. 等级奖励 (5%) - 高等级骑手优先
        double levelScore = switch (rider.getLevel()) {
            case "DIAMOND" -> 5;
            case "PLATINUM" -> 4;
            case "GOLD" -> 3;
            case "SILVER" -> 2;
            default -> 1;
        };

        return distanceScore + loadScore + ratingScore + directionScore + levelScore;
    }

    @Override
    public DeliveryOrder assignOrderToSpecificRider(Long orderId, Long riderId) {
        DeliveryOrder order = getOrderById(orderId);
        DeliveryRider rider = riderService.getRiderById(riderId);
        
        if (order == null || rider == null) {
            return null;
        }

        order.assignToRider(rider.getId(), rider.getName(), rider.getPhone());
        order.setUpdatedAt(LocalDateTime.now());
        
        riderService.incrementActiveOrderCount(riderId);
        saveOrderToRedis(order);
        redisTemplate.opsForList().remove(PENDING_QUEUE_KEY, 0, orderId);
        
        return order;
    }

    @Override
    public DeliveryOrder riderAcceptOrder(Long orderId, Long riderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return null;
        }

        order.accept();
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        log.info("骑手接单, 订单ID: {}, 骑手ID: {}", orderId, riderId);
        return order;
    }

    @Override
    public DeliveryOrder riderArriveAtMerchant(Long orderId, Long riderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return null;
        }

        order.setStatus("PICKING");
        order.setArrivedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        log.info("骑手到店, 订单ID: {}, 骑手ID: {}", orderId, riderId);
        return order;
    }

    @Override
    public DeliveryOrder riderPickUpOrder(Long orderId, Long riderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return null;
        }

        order.pickUp();
        order.setUpdatedAt(LocalDateTime.now());
        
        // 重新计算预计送达时间(基于实际取货时间)
        int remainingMinutes = estimateDeliveryTime(order.getDeliveryDistance());
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(remainingMinutes));
        
        saveOrderToRedis(order);
        
        log.info("骑手取货, 订单ID: {}, 骑手ID: {}", orderId, riderId);
        return order;
    }

    @Override
    public DeliveryOrder riderDeliverOrder(Long orderId, Long riderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || !order.getRiderId().equals(riderId)) {
            return null;
        }

        order.arrive();
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        log.info("订单送达, 订单ID: {}, 骑手ID: {}", orderId, riderId);
        return order;
    }

    @Override
    public DeliveryOrder completeOrder(Long orderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null) {
            return null;
        }

        order.complete();
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        // 更新骑手统计
        riderService.decrementActiveOrderCount(order.getRiderId());
        
        // 更新骑手今日完成数和收入
        riderService.updateDailyStats(order.getRiderId(), 1, order.getDeliveryFee());
        
        log.info("订单完成, 订单ID: {}", orderId);
        return order;
    }

    @Override
    public DeliveryOrder cancelOrder(Long orderId, String reason, String cancelledBy) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null) {
            return null;
        }

        // 如果已分配给骑手，需要释放骑手资源
        if (order.getRiderId() != null && !"WAITING".equals(order.getStatus())) {
            riderService.decrementActiveOrderCount(order.getRiderId());
        }

        order.cancel(reason, cancelledBy);
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        log.info("订单取消, 订单ID: {}, 原因: {}, 取消人: {}", orderId, reason, cancelledBy);
        return order;
    }

    @Override
    public List<DeliveryOrder> getRiderOrders(Long riderId, String status, Integer page, Integer size) {
        // 从Redis获取骑手订单列表
        String riderOrdersKey = "delivery:rider_orders:" + riderId;
        List<Object> orderIds = redisTemplate.opsForList().range(riderOrdersKey, 0, -1);
        
        if (orderIds == null) return new ArrayList<>();
        
        return orderIds.stream()
            .map(id -> getOrderById(Long.valueOf(id.toString())))
            .filter(Objects::nonNull)
            .filter(o -> status == null || status.equals(o.getStatus()))
            .skip((long) (page - 1) * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public List<DeliveryOrder> getCustomerOrders(Long customerId, String status, Integer page, Integer size) {
        // 实现略... 实际应从数据库查询
        return new ArrayList<>();
    }

    @Override
    public List<DeliveryOrder> getMerchantOrders(Long merchantId, String status, Integer page, Integer size) {
        // 实现略... 实际应从数据库查询
        return new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DeliveryOrder> getPendingOrders(Long zoneId, Integer page, Integer size) {
        List<Object> pendingIds = redisTemplate.opsForList().range(PENDING_QUEUE_KEY, 0, -1);
        if (pendingIds == null) return new ArrayList<>();
        
        return pendingIds.stream()
            .map(id -> getOrderById(Long.valueOf(id.toString())))
            .filter(Objects::nonNull)
            .filter(o -> zoneId == null || zoneId.equals(o.getZoneId()))
            .skip((long) (page - 1) * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public void updateOrderPath(Long orderId, String pathJson) {
        DeliveryOrder order = getOrderById(orderId);
        if (order != null) {
            order.setDeliveryPath(pathJson);
            order.setUpdatedAt(LocalDateTime.now());
            saveOrderToRedis(order);
        }
    }

    @Override
    public void calculateEstimatedDeliveryTime(Long orderId) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || order.getDeliveryDistance() == null) return;
        
        int minutes = estimateDeliveryTime(order.getDeliveryDistance());
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(minutes));
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
    }

    @Override
    public DeliveryOrder markOrderException(Long orderId, String exceptionType, String reason) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null) return null;
        
        order.markException(exceptionType, reason);
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        log.warn("订单异常, 订单ID: {}, 类型: {}, 原因: {}", orderId, exceptionType, reason);
        return order;
    }

    @Override
    public DeliveryOrder rateOrder(Long orderId, Integer rating, String comment) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null) return null;
        
        order.setCustomerRating(rating);
        order.setCustomerComment(comment);
        order.setUpdatedAt(LocalDateTime.now());
        saveOrderToRedis(order);
        
        return order;
    }

    @Override
    public void batchAssignOrders(List<Long> orderIds) {
        for (Long orderId : orderIds) {
            try {
                assignOrderToRider(orderId);
            } catch (Exception e) {
                log.error("批量分配订单失败, 订单ID: {}", orderId, e);
            }
        }
    }

    @Override
    public DeliveryOrder reassignOrder(Long orderId, String reason) {
        DeliveryOrder order = getOrderById(orderId);
        if (order == null || order.getRiderId() == null) return null;
        
        // 释放原骑手
        riderService.decrementActiveOrderCount(order.getRiderId());
        
        // 重置订单状态
        Long oldRiderId = order.getRiderId();
        order.setRiderId(null);
        order.setRiderName(null);
        order.setRiderPhone(null);
        order.setStatus("WAITING");
        order.setAssignedAt(null);
        order.setUpdatedAt(LocalDateTime.now());
        
        saveOrderToRedis(order);
        redisTemplate.opsForList().rightPush(PENDING_QUEUE_KEY, orderId);
        
        log.info("订单重新分配, 订单ID: {}, 原骑手: {}, 原因: {}", orderId, oldRiderId, reason);
        
        // 立即重新分配
        return assignOrderToRider(orderId);
    }

    @Override
    public DeliveryOrder getOrderProgress(Long orderId) {
        return getOrderById(orderId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DeliveryOrder> checkDelayedOrders() {
        List<Object> allOrderIds = redisTemplate.opsForList().range(PENDING_QUEUE_KEY, 0, -1);
        if (allOrderIds == null) return new ArrayList<>();
        
        List<DeliveryOrder> delayedOrders = new ArrayList<>();
        
        for (Object id : allOrderIds) {
            DeliveryOrder order = getOrderById(Long.valueOf(id.toString()));
            if (order != null && order.isDelayed()) {
                delayedOrders.add(order);
                // 标记异常
                markOrderException(order.getId(), "DELAY", "订单配送超时");
            }
        }
        
        return delayedOrders;
    }

    @Override
    public void updateDeliveryDistance(Long orderId, Integer distance) {
        DeliveryOrder order = getOrderById(orderId);
        if (order != null) {
            order.setDeliveryDistance(distance);
            order.setUpdatedAt(LocalDateTime.now());
            saveOrderToRedis(order);
        }
    }

    @Override
    public BigDecimal calculateDeliveryFee(Long merchantId, BigDecimal deliveryLng, BigDecimal deliveryLat, BigDecimal weight) {
        // 简化实现：基础费 + 距离费 + 重量费
        BigDecimal baseFee = new BigDecimal("5.00");
        
        // 这里应从商家配置获取配送距离并计算
        BigDecimal distanceFee = new BigDecimal("1.50");
        BigDecimal weightFee = weight != null ? weight.multiply(new BigDecimal("0.50")) : BigDecimal.ZERO;
        
        return baseFee.add(distanceFee).add(weightFee).setScale(2, RoundingMode.HALF_UP);
    }

    // ========== 私有辅助方法 ==========

    private void saveOrderToRedis(DeliveryOrder order) {
        String key = ORDER_KEY_PREFIX + order.getId();
        redisTemplate.opsForValue().set(key, order, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set("delivery:order_no:" + order.getOrderNo(), order.getId().toString(), 24, TimeUnit.HOURS);
        
        // 如果已分配给骑手，添加到骑手的订单列表
        if (order.getRiderId() != null) {
            String riderOrdersKey = "delivery:rider_orders:" + order.getRiderId();
            redisTemplate.opsForList().remove(riderOrdersKey, 0, order.getId());
            redisTemplate.opsForList().rightPush(riderOrdersKey, order.getId());
            redisTemplate.expire(riderOrdersKey, 24, TimeUnit.HOURS);
        }
    }

    private String generateOrderNo() {
        return "DEL" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }

    private int calculateDistance(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        double R = 6371000;
        double radLat1 = Math.toRadians(lat1.doubleValue());
        double radLat2 = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (R * c);
    }

    private int estimateDeliveryTime(int distanceMeters) {
        // 基础时间5分钟 + 每公里3分钟
        int distanceKm = distanceMeters / 1000;
        return 5 + distanceKm * 3;
    }
}
