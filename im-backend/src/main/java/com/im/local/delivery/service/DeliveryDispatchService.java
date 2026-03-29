package com.im.local.delivery.service;

import com.im.local.delivery.dto.*;
import com.im.local.delivery.entity.*;
import com.im.local.delivery.enums.*;
import com.im.local.delivery.repository.*;
import com.im.core.redis.RedisGeoService;
import com.im.core.websocket.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 运力调度服务
 * 智能订单分配、骑手调度、路径优化
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryDispatchService {

    private final DeliveryOrderRepository orderRepository;
    private final DeliveryRiderRepository riderRepository;
    private final DeliveryDispatchRecordRepository dispatchRecordRepository;
    private final RiderLocationRepository locationRepository;
    private final DeliveryRouteRepository routeRepository;
    private final RedisGeoService redisGeoService;
    private final WebSocketPushService webSocketPushService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Redis Key 前缀
    private static final String RIDER_LOCATION_KEY = "delivery:rider:location:";
    private static final String RIDER_STATUS_KEY = "delivery:rider:status:";
    private static final String ORDER_DISPATCH_KEY = "delivery:order:dispatch:";
    private static final String DISPATCH_LOCK_KEY = "delivery:dispatch:lock:";
    
    // 调度配置
    private static final double DISPATCH_RADIUS_METERS = 5000; // 派单半径5公里
    private static final int MAX_CANDIDATE_RIDERS = 10; // 最大候选骑手数
    private static final long DISPATCH_TIMEOUT_SECONDS = 30; // 派单超时30秒
    
    /**
     * 智能订单分配
     * 基于距离、顺路度、骑手负载的综合评分算法
     */
    @Transactional
    public DispatchResult dispatchOrder(Long orderId) {
        log.info("开始智能派单，订单ID: {}", orderId);
        
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new DeliveryException("订单不存在: " + orderId));
        
        // 获取订单取货坐标
        double pickupLat = order.getPickupLatitude();
        double pickupLng = order.getPickupLongitude();
        
        // 1. 获取附近可用骑手
        List<RiderCandidate> candidates = findNearbyAvailableRiders(
            pickupLat, pickupLng, DISPATCH_RADIUS_METERS
        );
        
        if (candidates.isEmpty()) {
            log.warn("附近无可用骑手，订单ID: {}", orderId);
            return DispatchResult.failed("附近暂无可用骑手");
        }
        
        // 2. 计算每个骑手的综合评分
        List<ScoredRider> scoredRiders = candidates.stream()
            .map(candidate -> calculateRiderScore(candidate, order))
            .sorted(Comparator.comparing(ScoredRider::getScore).reversed())
            .limit(MAX_CANDIDATE_RIDERS)
            .collect(Collectors.toList());
        
        // 3. 选择最优骑手
        ScoredRider bestRider = scoredRiders.get(0);
        
        // 4. 尝试分配给最优骑手
        boolean assigned = tryAssignOrder(order, bestRider.getRider());
        
        if (assigned) {
            // 创建派单记录
            DispatchRecord record = createDispatchRecord(order, bestRider);
            
            // 推送派单通知给骑手
            pushDispatchNotification(bestRider.getRider().getId(), order);
            
            log.info("派单成功，订单ID: {}, 骑手ID: {}, 评分: {}", 
                orderId, bestRider.getRider().getId(), bestRider.getScore());
            
            return DispatchResult.success(bestRider.getRider().getId(), record.getId());
        }
        
        // 5. 如果最优骑手拒绝，尝试下一个
        for (int i = 1; i < scoredRiders.size(); i++) {
            ScoredRider alternative = scoredRiders.get(i);
            if (tryAssignOrder(order, alternative.getRider())) {
                DispatchRecord record = createDispatchRecord(order, alternative);
                pushDispatchNotification(alternative.getRider().getId(), order);
                
                log.info("备选派单成功，订单ID: {}, 骑手ID: {}", 
                    orderId, alternative.getRider().getId());
                return DispatchResult.success(alternative.getRider().getId(), record.getId());
            }
        }
        
        log.warn("所有候选骑手均不可用，订单ID: {}", orderId);
        return DispatchResult.failed("暂无骑手可接单，请稍后重试");
    }
    
    /**
     * 批量智能派单
     * 使用约束满足问题(CSP) + 启发式优化
     */
    @Transactional
    public BatchDispatchResult batchDispatchOrders(List<Long> orderIds) {
        log.info("开始批量派单，订单数: {}", orderIds.size());
        
        List<DispatchResult> results = new ArrayList<>();
        List<DeliveryOrder> orders = orderRepository.findAllById(orderIds);
        
        // 按区域分组订单
        Map<String, List<DeliveryOrder>> regionOrders = orders.stream()
            .collect(Collectors.groupingBy(this::getOrderRegion));
        
        for (Map.Entry<String, List<DeliveryOrder>> entry : regionOrders.entrySet()) {
            List<DeliveryOrder> regionOrderList = entry.getValue();
            
            // 获取该区域的所有可用骑手
            List<DeliveryRider> availableRiders = getAvailableRidersInRegion(entry.getKey());
            
            // 使用匈牙利算法进行最优匹配
            List<DispatchAssignment> assignments = optimizeDispatch(
                regionOrderList, availableRiders
            );
            
            // 执行分配
            for (DispatchAssignment assignment : assignments) {
                DispatchResult result = executeDispatch(assignment);
                results.add(result);
            }
        }
        
        long successCount = results.stream().filter(DispatchResult::isSuccess).count();
        
        log.info("批量派单完成，成功: {}/{}", successCount, orderIds.size());
        
        return BatchDispatchResult.builder()
            .totalOrders(orderIds.size())
            .successCount((int) successCount)
            .failedCount(orderIds.size() - (int) successCount)
            .results(results)
            .build();
    }
    
    /**
     * 骑手接单
     */
    @Transactional
    public AcceptResult acceptOrder(Long riderId, Long orderId, Long dispatchId) {
        log.info("骑手接单，骑手ID: {}, 订单ID: {}", riderId, orderId);
        
        // 获取并验证派单记录
        DispatchRecord record = dispatchRecordRepository.findById(dispatchId)
            .orElseThrow(() -> new DeliveryException("派单记录不存在"));
        
        if (!record.getRiderId().equals(riderId)) {
            throw new DeliveryException("无权操作此派单");
        }
        
        if (record.isExpired()) {
            record.setStatus(DispatchStatus.EXPIRED);
            dispatchRecordRepository.save(record);
            throw new DeliveryException("派单已过期");
        }
        
        // 更新订单状态
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new DeliveryException("订单不存在"));
        
        order.setRiderId(riderId);
        order.setStatus(DeliveryStatus.RIDER_ASSIGNED);
        order.setAcceptTime(LocalDateTime.now());
        orderRepository.save(order);
        
        // 更新派单记录
        record.setStatus(DispatchStatus.ACCEPTED);
        record.setAcceptTime(LocalDateTime.now());
        dispatchRecordRepository.save(record);
        
        // 更新骑手状态
        updateRiderStatus(riderId, RiderStatus.DELIVERING);
        
        // 推送给用户
        pushOrderStatusToUser(order.getUserId(), order);
        
        // 推送给商家
        pushOrderStatusToMerchant(order.getMerchantId(), order);
        
        // 计算预计送达时间
        LocalDateTime estimatedArrival = calculateEstimatedArrival(riderId, order);
        order.setEstimatedDeliveryTime(estimatedArrival);
        orderRepository.save(order);
        
        log.info("骑手接单成功，骑手ID: {}, 订单ID: {}", riderId, orderId);
        
        return AcceptResult.builder()
            .success(true)
            .orderId(orderId)
            .riderId(riderId)
            .estimatedArrival(estimatedArrival)
            .build();
    }
    
    /**
     * 骑手拒单
     */
    @Transactional
    public void rejectOrder(Long riderId, Long orderId, Long dispatchId, String reason) {
        log.info("骑手拒单，骑手ID: {}, 订单ID: {}, 原因: {}", riderId, orderId, reason);
        
        DispatchRecord record = dispatchRecordRepository.findById(dispatchId)
            .orElseThrow(() -> new DeliveryException("派单记录不存在"));
        
        record.setStatus(DispatchStatus.REJECTED);
        record.setRejectReason(reason);
        record.setRejectTime(LocalDateTime.now());
        dispatchRecordRepository.save(record);
        
        // 重新派单
        dispatchOrder(orderId);
    }
    
    /**
     * 更新骑手位置
     */
    public void updateRiderLocation(Long riderId, double latitude, double longitude) {
        // 保存到Redis Geo
        String key = RIDER_LOCATION_KEY + riderId;
        redisGeoService.addLocation(key, latitude, longitude, riderId.toString());
        
        // 保存到数据库
        RiderLocation location = RiderLocation.builder()
            .riderId(riderId)
            .latitude(latitude)
            .longitude(longitude)
            .updateTime(LocalDateTime.now())
            .build();
        locationRepository.save(location);
        
        // 推送位置给关联订单的用户
        List<DeliveryOrder> activeOrders = orderRepository
            .findByRiderIdAndStatusIn(riderId, Arrays.asList(
                DeliveryStatus.RIDER_ASSIGNED,
                DeliveryStatus.PICKED_UP,
                DeliveryStatus.DELIVERING
            ));
        
        for (DeliveryOrder order : activeOrders) {
            pushRiderLocationToUser(order.getUserId(), riderId, latitude, longitude);
        }
    }
    
    /**
     * 获取骑手当前位置
     */
    public RiderLocationDTO getRiderCurrentLocation(Long riderId) {
        RiderLocation location = locationRepository
            .findTopByRiderIdOrderByUpdateTimeDesc(riderId)
            .orElse(null);
        
        if (location == null) {
            return null;
        }
        
        return RiderLocationDTO.builder()
            .riderId(riderId)
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .updateTime(location.getUpdateTime())
            .build();
    }
    
    /**
     * 获取订单配送轨迹
     */
    public List<RiderLocationDTO> getOrderDeliveryTrajectory(Long orderId) {
        DeliveryOrder order = orderRepository.findById(orderId)
            .orElseThrow(() -> new DeliveryException("订单不存在"));
        
        Long riderId = order.getRiderId();
        if (riderId == null) {
            return Collections.emptyList();
        }
        
        LocalDateTime startTime = order.getAcceptTime();
        LocalDateTime endTime = order.getDeliveryTime() != null 
            ? order.getDeliveryTime() 
            : LocalDateTime.now();
        
        List<RiderLocation> locations = locationRepository
            .findByRiderIdAndUpdateTimeBetweenOrderByUpdateTimeAsc(
                riderId, startTime, endTime
            );
        
        return locations.stream()
            .map(loc -> RiderLocationDTO.builder()
                .riderId(riderId)
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .updateTime(loc.getUpdateTime())
                .build())
            .collect(Collectors.toList());
    }
    
    /**
     * 优化配送路径
     * 使用Dijkstra/A*算法
     */
    public RouteOptimizationResult optimizeDeliveryRoute(Long riderId) {
        // 获取骑手当前位置
        RiderLocation currentLocation = locationRepository
            .findTopByRiderIdOrderByUpdateTimeDesc(riderId)
            .orElseThrow(() -> new DeliveryException("无法获取骑手位置"));
        
        // 获取骑手的所有待配送订单
        List<DeliveryOrder> pendingOrders = orderRepository
            .findByRiderIdAndStatusIn(riderId, Arrays.asList(
                DeliveryStatus.RIDER_ASSIGNED,
                DeliveryStatus.PICKED_UP
            ));
        
        if (pendingOrders.isEmpty()) {
            return RouteOptimizationResult.empty();
        }
        
        // 构建路径优化问题
        List<RoutePoint> points = new ArrayList<>();
        
        // 添加当前位置
        points.add(RoutePoint.builder()
            .type(PointType.CURRENT)
            .latitude(currentLocation.getLatitude())
            .longitude(currentLocation.getLongitude())
            .build());
        
        // 添加所有取货点和送货点
        for (DeliveryOrder order : pendingOrders) {
            // 取货点
            points.add(RoutePoint.builder()
                .type(PointType.PICKUP)
                .orderId(order.getId())
                .latitude(order.getPickupLatitude())
                .longitude(order.getPickupLongitude())
                .address(order.getPickupAddress())
                .estimatedTime(order.getEstimatedPickupTime())
                .build());
            
            // 送货点
            points.add(RoutePoint.builder()
                .type(PointType.DELIVERY)
                .orderId(order.getId())
                .latitude(order.getDeliveryLatitude())
                .longitude(order.getDeliveryLongitude())
                .address(order.getDeliveryAddress())
                .estimatedTime(order.getEstimatedDeliveryTime())
                .build());
        }
        
        // 使用TSP求解最优路径
        List<RoutePoint> optimizedRoute = solveTSP(points);
        
        // 计算总距离和预计时间
        double totalDistance = calculateTotalDistance(optimizedRoute);
        Duration totalDuration = estimateTotalDuration(optimizedRoute, totalDistance);
        
        return RouteOptimizationResult.builder()
            .riderId(riderId)
            .points(optimizedRoute)
            .totalDistance(totalDistance)
            .estimatedDuration(totalDuration)
            .orderCount(pendingOrders.size())
            .build();
    }
    
    /**
     * 定时任务：自动重新派单
     * 每10秒执行一次
     */
    @Scheduled(fixedRate = 10000)
    public void autoRedispatchExpiredOrders() {
        // 查找超时的派单记录
        LocalDateTime timeoutThreshold = LocalDateTime.now()
            .minusSeconds(DISPATCH_TIMEOUT_SECONDS);
        
        List<DispatchRecord> expiredRecords = dispatchRecordRepository
            .findByStatusAndCreateTimeBefore(DispatchStatus.PENDING, timeoutThreshold);
        
        for (DispatchRecord record : expiredRecords) {
            record.setStatus(DispatchStatus.EXPIRED);
            dispatchRecordRepository.save(record);
            
            // 重新派单
            dispatchOrder(record.getOrderId());
        }
    }
    
    /**
     * 定时任务：更新预计送达时间
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void updateEstimatedArrivalTimes() {
        List<DeliveryOrder> activeOrders = orderRepository
            .findByStatusIn(Arrays.asList(
                DeliveryStatus.RIDER_ASSIGNED,
                DeliveryStatus.PICKED_UP,
                DeliveryStatus.DELIVERING
            ));
        
        for (DeliveryOrder order : activeOrders) {
            if (order.getRiderId() != null) {
                LocalDateTime newEstimate = calculateEstimatedArrival(
                    order.getRiderId(), order
                );
                order.setEstimatedDeliveryTime(newEstimate);
                orderRepository.save(order);
                
                // 推送更新时间给用户
                pushEstimatedTimeUpdate(order.getUserId(), order);
            }
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 查找附近可用骑手
     */
    private List<RiderCandidate> findNearbyAvailableRiders(
        double latitude, double longitude, double radiusMeters
    ) {
        // 从Redis Geo查询附近骑手
        List<Long> nearbyRiderIds = redisGeoService.findNearby(
            RIDER_LOCATION_KEY + "*", latitude, longitude, radiusMeters
        );
        
        if (nearbyRiderIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取骑手详细信息并过滤可用状态
        List<DeliveryRider> riders = riderRepository
            .findAllByIdInAndStatus(nearbyRiderIds, RiderStatus.AVAILABLE);
        
        return riders.stream()
            .map(rider -> {
                double distance = calculateDistance(
                    latitude, longitude,
                    rider.getCurrentLatitude(), rider.getCurrentLongitude()
                );
                return new RiderCandidate(rider, distance);
            })
            .sorted(Comparator.comparing(RiderCandidate::getDistance))
            .collect(Collectors.toList());
    }
    
    /**
     * 计算骑手综合评分
     * 评分 = 距离分 * 0.3 + 顺路度 * 0.3 + 负载分 * 0.2 + 评分分 * 0.2
     */
    private ScoredRider calculateRiderScore(RiderCandidate candidate, DeliveryOrder order) {
        DeliveryRider rider = candidate.getRider();
        double distance = candidate.getDistance();
        
        // 距离分 (越近越高，最高100)
        double distanceScore = Math.max(0, 100 - (distance / 50));
        
        // 顺路度 (计算当前订单与骑手已有订单的路线重合度)
        double routeMatchScore = calculateRouteMatchScore(rider, order);
        
        // 负载分 (当前订单数越少越高)
        int currentOrders = getCurrentOrderCount(rider.getId());
        double loadScore = Math.max(0, 100 - (currentOrders * 20));
        
        // 评分分 (基于骑手历史评分)
        double ratingScore = rider.getRating() * 20; // 5分制转100分制
        
        // 综合评分
        double totalScore = distanceScore * 0.3 + routeMatchScore * 0.3 
            + loadScore * 0.2 + ratingScore * 0.2;
        
        return new ScoredRider(rider, totalScore, distanceScore, 
            routeMatchScore, loadScore, ratingScore);
    }
    
    /**
     * 计算顺路度
     */
    private double calculateRouteMatchScore(DeliveryRider rider, DeliveryOrder newOrder) {
        // 获取骑手当前订单
        List<DeliveryOrder> currentOrders = orderRepository
            .findByRiderIdAndStatusIn(rider.getId(), Arrays.asList(
                DeliveryStatus.RIDER_ASSIGNED,
                DeliveryStatus.PICKED_UP
            ));
        
        if (currentOrders.isEmpty()) {
            return 100; // 无订单时顺路度最高
        }
        
        // 计算新订单与现有订单路线的重合度
        double totalMatch = 0;
        for (DeliveryOrder existingOrder : currentOrders) {
            double match = calculateRouteOverlap(existingOrder, newOrder);
            totalMatch += match;
        }
        
        return totalMatch / currentOrders.size();
    }
    
    /**
     * 计算两条配送路线的重合度
     */
    private double calculateRouteOverlap(DeliveryOrder order1, DeliveryOrder order2) {
        // 简化的重合度计算：基于起点和终点的距离
        double pickupDistance = calculateDistance(
            order1.getPickupLatitude(), order1.getPickupLongitude(),
            order2.getPickupLatitude(), order2.getPickupLongitude()
        );
        
        double deliveryDistance = calculateDistance(
            order1.getDeliveryLatitude(), order1.getDeliveryLongitude(),
            order2.getDeliveryLatitude(), order2.getDeliveryLongitude()
        );
        
        // 距离越近重合度越高
        double avgDistance = (pickupDistance + deliveryDistance) / 2;
        return Math.max(0, 100 - (avgDistance / 100));
    }
    
    /**
     * 获取骑手当前订单数
     */
    private int getCurrentOrderCount(Long riderId) {
        return orderRepository.countByRiderIdAndStatusIn(riderId, Arrays.asList(
            DeliveryStatus.RIDER_ASSIGNED,
            DeliveryStatus.PICKED_UP,
            DeliveryStatus.DELIVERING
        ));
    }
    
    /**
     * 尝试分配订单给骑手
     */
    private boolean tryAssignOrder(DeliveryOrder order, DeliveryRider rider) {
        // 检查骑手是否仍然可用
        DeliveryRider currentRider = riderRepository.findById(rider.getId())
            .orElse(null);
        
        if (currentRider == null || currentRider.getStatus() != RiderStatus.AVAILABLE) {
            return false;
        }
        
        // 检查是否已有进行中的派单
        boolean hasPendingDispatch = dispatchRecordRepository
            .existsByRiderIdAndStatus(rider.getId(), DispatchStatus.PENDING);
        
        return !hasPendingDispatch;
    }
    
    /**
     * 创建派单记录
     */
    private DispatchRecord createDispatchRecord(DeliveryOrder order, ScoredRider scoredRider) {
        DispatchRecord record = DispatchRecord.builder()
            .orderId(order.getId())
            .riderId(scoredRider.getRider().getId())
            .status(DispatchStatus.PENDING)
            .score(scoredRider.getScore())
            .distanceScore(scoredRider.getDistanceScore())
            .routeMatchScore(scoredRider.getRouteMatchScore())
            .loadScore(scoredRider.getLoadScore())
            .ratingScore(scoredRider.getRatingScore())
            .createTime(LocalDateTime.now())
            .expireTime(LocalDateTime.now().plusSeconds(DISPATCH_TIMEOUT_SECONDS))
            .build();
        
        return dispatchRecordRepository.save(record);
    }
    
    /**
     * 推送派单通知
     */
    private void pushDispatchNotification(Long riderId, DeliveryOrder order) {
        DispatchNotificationDTO notification = DispatchNotificationDTO.builder()
            .orderId(order.getId())
            .orderNo(order.getOrderNo())
            .pickupAddress(order.getPickupAddress())
            .deliveryAddress(order.getDeliveryAddress())
            .estimatedIncome(order.getRiderIncome())
            .distance(calculateDistance(
                order.getPickupLatitude(), order.getPickupLongitude(),
                order.getDeliveryLatitude(), order.getDeliveryLongitude()
            ))
            .expireSeconds(DISPATCH_TIMEOUT_SECONDS)
            .build();
        
        webSocketPushService.pushToUser(riderId, "NEW_DISPATCH", notification);
    }
    
    /**
     * 计算预计送达时间
     */
    private LocalDateTime calculateEstimatedArrival(Long riderId, DeliveryOrder order) {
        RiderLocation location = locationRepository
            .findTopByRiderIdOrderByUpdateTimeDesc(riderId)
            .orElse(null);
        
        if (location == null) {
            return LocalDateTime.now().plusMinutes(45);
        }
        
        // 计算到取货点距离
        double toPickup = calculateDistance(
            location.getLatitude(), location.getLongitude(),
            order.getPickupLatitude(), order.getPickupLongitude()
        );
        
        // 计算取货点到送货点距离
        double toDelivery = calculateDistance(
            order.getPickupLatitude(), order.getPickupLongitude(),
            order.getDeliveryLatitude(), order.getDeliveryLongitude()
        );
        
        // 总距离
        double totalDistance = toPickup + toDelivery;
        
        // 预估时间：每公里3分钟 + 取货准备10分钟 + 送货交接5分钟
        int estimatedMinutes = (int) (totalDistance / 1000 * 3) + 15;
        
        return LocalDateTime.now().plusMinutes(estimatedMinutes);
    }
    
    /**
     * 计算两点间距离（米）
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000; // 地球半径（米）
        
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
            Math.cos(lat1Rad) * Math.cos(lat2Rad) *
            Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 获取订单所属区域
     */
    private String getOrderRegion(DeliveryOrder order) {
        // 使用GeoHash或行政区划编码
        return order.getRegionCode() != null 
            ? order.getRegionCode() 
            : "default";
    }
    
    /**
     * 获取区域内的可用骑手
     */
    private List<DeliveryRider> getAvailableRidersInRegion(String regionCode) {
        return riderRepository.findByRegionCodeAndStatus(regionCode, RiderStatus.AVAILABLE);
    }
    
    /**
     * 使用匈牙利算法优化批量派单
     */
    private List<DispatchAssignment> optimizeDispatch(
        List<DeliveryOrder> orders, 
        List<DeliveryRider> riders
    ) {
        // 简化的贪心算法实现
        List<DispatchAssignment> assignments = new ArrayList<>();
        Set<Long> assignedRiders = new HashSet<>();
        
        for (DeliveryOrder order : orders) {
            DeliveryRider bestRider = null;
            double bestScore = -1;
            
            for (DeliveryRider rider : riders) {
                if (assignedRiders.contains(rider.getId())) {
                    continue;
                }
                
                double score = calculateSimpleMatchScore(order, rider);
                if (score > bestScore) {
                    bestScore = score;
                    bestRider = rider;
                }
            }
            
            if (bestRider != null) {
                assignments.add(new DispatchAssignment(order, bestRider, bestScore));
                assignedRiders.add(bestRider.getId());
            }
        }
        
        return assignments;
    }
    
    /**
     * 计算简单匹配分数
     */
    private double calculateSimpleMatchScore(DeliveryOrder order, DeliveryRider rider) {
        double distance = calculateDistance(
            order.getPickupLatitude(), order.getPickupLongitude(),
            rider.getCurrentLatitude(), rider.getCurrentLongitude()
        );
        return 10000 - distance; // 距离越近分数越高
    }
    
    /**
     * 执行派单
     */
    private DispatchResult executeDispatch(DispatchAssignment assignment) {
        return dispatchOrder(assignment.getOrder().getId());
    }
    
    /**
     * 更新骑手状态
     */
    private void updateRiderStatus(Long riderId, RiderStatus status) {
        DeliveryRider rider = riderRepository.findById(riderId)
            .orElseThrow(() -> new DeliveryException("骑手不存在"));
        rider.setStatus(status);
        riderRepository.save(rider);
        
        // 更新Redis缓存
        redisTemplate.opsForValue().set(
            RIDER_STATUS_KEY + riderId, 
            status.name(), 
            5, TimeUnit.MINUTES
        );
    }
    
    /**
     * 推送订单状态给用户
     */
    private void pushOrderStatusToUser(Long userId, DeliveryOrder order) {
        webSocketPushService.pushToUser(userId, "ORDER_STATUS_UPDATE", 
            OrderStatusUpdateDTO.from(order));
    }
    
    /**
     * 推送订单状态给商家
     */
    private void pushOrderStatusToMerchant(Long merchantId, DeliveryOrder order) {
        webSocketPushService.pushToUser(merchantId, "MERCHANT_ORDER_UPDATE", 
            MerchantOrderUpdateDTO.from(order));
    }
    
    /**
     * 推送骑手位置给用户
     */
    private void pushRiderLocationToUser(Long userId, Long riderId, 
                                         double lat, double lng) {
        webSocketPushService.pushToUser(userId, "RIDER_LOCATION_UPDATE",
            RiderLocationUpdateDTO.builder()
                .riderId(riderId)
                .latitude(lat)
                .longitude(lng)
                .timestamp(LocalDateTime.now())
                .build());
    }
    
    /**
     * 推送预计时间更新
     */
    private void pushEstimatedTimeUpdate(Long userId, DeliveryOrder order) {
        webSocketPushService.pushToUser(userId, "ESTIMATED_TIME_UPDATE",
            EstimatedTimeUpdateDTO.builder()
                .orderId(order.getId())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .build());
    }
    
    /**
     * TSP求解最优路径（简化版最近邻算法）
     */
    private List<RoutePoint> solveTSP(List<RoutePoint> points) {
        if (points.size() <= 2) {
            return points;
        }
        
        List<RoutePoint> route = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        // 从当前位置开始
        route.add(points.get(0));
        visited.add(0);
        int currentIdx = 0;
        
        while (visited.size() < points.size()) {
            double minDistance = Double.MAX_VALUE;
            int nextIdx = -1;
            
            for (int i = 0; i < points.size(); i++) {
                if (visited.contains(i)) continue;
                
                double dist = calculateDistance(
                    points.get(currentIdx).getLatitude(),
                    points.get(currentIdx).getLongitude(),
                    points.get(i).getLatitude(),
                    points.get(i).getLongitude()
                );
                
                if (dist < minDistance) {
                    minDistance = dist;
                    nextIdx = i;
                }
            }
            
            if (nextIdx != -1) {
                route.add(points.get(nextIdx));
                visited.add(nextIdx);
                currentIdx = nextIdx;
            }
        }
        
        return route;
    }
    
    /**
     * 计算路线总距离
     */
    private double calculateTotalDistance(List<RoutePoint> route) {
        double total = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            total += calculateDistance(
                route.get(i).getLatitude(), route.get(i).getLongitude(),
                route.get(i + 1).getLatitude(), route.get(i + 1).getLongitude()
            );
        }
        return total;
    }
    
    /**
     * 估算总时间
     */
    private Duration estimateTotalDuration(List<RoutePoint> route, double totalDistance) {
        // 每公里3分钟 + 每个点5分钟处理时间
        int minutes = (int) (totalDistance / 1000 * 3) + (route.size() - 1) * 5;
        return Duration.ofMinutes(minutes);
    }
    
    // ==================== 内部类 ====================
    
    @lombok.Value
    private static class RiderCandidate {
        DeliveryRider rider;
        double distance;
    }
    
    @lombok.Value
    private static class ScoredRider {
        DeliveryRider rider;
        double score;
        double distanceScore;
        double routeMatchScore;
        double loadScore;
        double ratingScore;
    }
    
    @lombok.Value
    private static class DispatchAssignment {
        DeliveryOrder order;
        DeliveryRider rider;
        double score;
    }
}
