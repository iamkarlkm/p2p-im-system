package com.im.local.scheduler.service.impl;

import com.im.local.scheduler.dto.*;
import com.im.local.scheduler.entity.DeliveryGeofence;
import com.im.local.scheduler.entity.DeliveryOrderBatch;
import com.im.local.scheduler.entity.DeliveryStaff;
import com.im.local.scheduler.enums.*;
import com.im.local.scheduler.repository.DeliveryGeofenceMapper;
import com.im.local.scheduler.repository.DeliveryOrderBatchMapper;
import com.im.local.scheduler.repository.DeliveryStaffMapper;
import com.im.local.scheduler.service.ISmartDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能调度服务实现类
 * 基于地理围栏的订单聚合与智能派单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartDispatchServiceImpl implements ISmartDispatchService {
    
    private final DeliveryStaffMapper staffMapper;
    private final DeliveryOrderBatchMapper batchMapper;
    private final DeliveryGeofenceMapper geofenceMapper;
    
    @Override
    @Transactional
    public DispatchResultResponse dispatchOrder(DispatchOrderRequest request) {
        log.info("开始智能派单: orderCount={}, strategy={}", 
                request.getOrderIds().size(), request.getStrategy());
        
        // 1. 查找附近可用骑手
        List<DeliveryStaff> availableStaff = findAvailableStaff(
                request.getPickupLng().doubleValue(),
                request.getPickupLat().doubleValue(),
                5000
        );
        
        if (availableStaff.isEmpty()) {
            log.warn("附近无可用骑手");
            return DispatchResultResponse.builder()
                    .success(false)
                    .message("附近无可用骑手")
                    .build();
        }
        
        // 2. 根据策略选择最优骑手
        DeliveryStaff bestStaff = selectBestStaff(availableStaff, request);
        
        // 3. 计算最优配送路径
        RoutePlanRequest routeRequest = RoutePlanRequest.builder()
                .staffId(bestStaff.getStaffId())
                .startLng(request.getPickupLng())
                .startLat(request.getPickupLat())
                .points(convertToPoints(request.getDeliveryPoints()))
                .algorithm(RouteAlgorithm.GREEDY.getCode())
                .build();
        
        List<DispatchResultResponse.RouteNode> optimalRoute = calculateOptimalRoute(routeRequest);
        
        // 4. 创建配送批次
        String batchNo = generateBatchNo();
        DeliveryOrderBatch batch = DeliveryOrderBatch.builder()
                .batchNo(batchNo)
                .geofenceId(request.getGeofenceId())
                .geofenceName("围栏-" + request.getGeofenceId())
                .staffId(bestStaff.getStaffId())
                .orderIds(String.join(",", request.getOrderIds().stream().map(String::valueOf).collect(Collectors.toList())))
                .orderCount(request.getOrderIds().size())
                .estimatedTotalDistance(calculateTotalDistance(optimalRoute))
                .estimatedTotalTime(calculateTotalTime(optimalRoute))
                .optimalRoute(convertRouteToJson(optimalRoute))
                .status(BatchStatus.PENDING.getCode())
                .build();
        
        batchMapper.insert(batch);
        batchMapper.assignStaff(batch.getBatchId(), bestStaff.getStaffId());
        
        // 5. 更新骑手状态
        staffMapper.updateStatus(bestStaff.getStaffId(), StaffStatus.PICKING.getCode());
        
        log.info("派单成功: batchId={}, staffId={}, staffName={}", 
                batch.getBatchId(), bestStaff.getStaffId(), bestStaff.getStaffName());
        
        return DispatchResultResponse.builder()
                .batchId(batch.getBatchId())
                .batchNo(batchNo)
                .staffId(bestStaff.getStaffId())
                .staffName(bestStaff.getStaffName())
                .staffPhone(bestStaff.getPhone())
                .orderIds(request.getOrderIds())
                .orderCount(request.getOrderIds().size())
                .estimatedTotalDistance(batch.getEstimatedTotalDistance())
                .estimatedTotalTime(batch.getEstimatedTotalTime())
                .optimalRoute(optimalRoute)
                .assignedAt(batch.getAssignedAt())
                .success(true)
                .message("派单成功")
                .build();
    }
    
    @Override
    public List<DispatchResultResponse> batchDispatchOrders(List<DispatchOrderRequest> requests) {
        return requests.stream()
                .map(this::dispatchOrder)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Long> aggregateOrdersInGeofence(Long geofenceId) {
        List<DeliveryOrderBatch> pendingBatches = batchMapper.selectPendingByGeofenceId(geofenceId);
        return pendingBatches.stream()
                .flatMap(batch -> Arrays.stream(batch.getOrderIds().split(",")))
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<DispatchResultResponse.RouteNode> calculateOptimalRoute(RoutePlanRequest request) {
        List<RoutePlanRequest.DeliveryPoint> points = request.getPoints();
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<DispatchResultResponse.RouteNode> route = new ArrayList<>();
        
        double currentLng = request.getStartLng().doubleValue();
        double currentLat = request.getStartLat().doubleValue();
        
        Set<Long> visited = new HashSet<>();
        int sequence = 1;
        
        // 贪心算法：每次选择最近的点
        while (visited.size() < points.size()) {
            RoutePlanRequest.DeliveryPoint nearest = null;
            double minDistance = Double.MAX_VALUE;
            
            for (RoutePlanRequest.DeliveryPoint point : points) {
                if (visited.contains(point.getOrderId())) continue;
                
                double distance = calculateDistance(
                        currentLng, currentLat,
                        point.getLng().doubleValue(), point.getLat().doubleValue()
                );
                
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = point;
                }
            }
            
            if (nearest != null) {
                visited.add(nearest.getOrderId());
                int estimatedMinutes = (int) (minDistance / 200 * 60 / 60) + 5; // 假设200m/min速度
                
                route.add(DispatchResultResponse.RouteNode.builder()
                        .sequence(sequence++)
                        .orderId(nearest.getOrderId())
                        .address(nearest.getAddress())
                        .lng(nearest.getLng())
                        .lat(nearest.getLat())
                        .action(sequence == 2 ? "取餐" : "送达")
                        .estimatedArrivalMinutes(estimatedMinutes)
                        .build());
                
                currentLng = nearest.getLng().doubleValue();
                currentLat = nearest.getLat().doubleValue();
            }
        }
        
        return route;
    }
    
    @Override
    public boolean borrowStaffFromNearbyGeofence(Long targetGeofenceId, Integer staffCount) {
        log.info("跨围栏运力借调: targetGeofenceId={}, staffCount={}", targetGeofenceId, staffCount);
        // 实现跨围栏借调逻辑
        return true;
    }
    
    @Override
    public DispatchResultResponse reassignBatch(Long batchId, Long newStaffId) {
        log.info("重新分配批次: batchId={}, newStaffId={}", batchId, newStaffId);
        batchMapper.assignStaff(batchId, newStaffId);
        
        return DispatchResultResponse.builder()
                .batchId(batchId)
                .staffId(newStaffId)
                .success(true)
                .message("重新分配成功")
                .build();
    }
    
    @Override
    public List<String> getDispatchSuggestions(Long geofenceId) {
        List<String> suggestions = new ArrayList<>();
        
        DeliveryGeofence geofence = geofenceMapper.selectById(geofenceId);
        if (geofence == null) return suggestions;
        
        if (geofence.getSaturationRate() > 80) {
            suggestions.add("当前围栏运力紧张，建议扩大接单范围或从邻近围栏借调骑手");
        }
        
        int pendingCount = batchMapper.countPending();
        if (pendingCount > 10) {
            suggestions.add("当前有" + pendingCount + "个待分配批次，建议优先处理积压订单");
        }
        
        suggestions.add("建议开启动态围栏扩展以覆盖更多可用骑手");
        
        return suggestions;
    }
    
    private List<DeliveryStaff> findAvailableStaff(double lng, double lat, int radius) {
        double degree = radius / 111000.0;
        BigDecimal minLng = BigDecimal.valueOf(lng - degree);
        BigDecimal maxLng = BigDecimal.valueOf(lng + degree);
        BigDecimal minLat = BigDecimal.valueOf(lat - degree);
        BigDecimal maxLat = BigDecimal.valueOf(lat + degree);
        
        return staffMapper.selectIdleStaffInRange(minLng, maxLng, minLat, maxLat);
    }
    
    private DeliveryStaff selectBestStaff(List<DeliveryStaff> staffList, DispatchOrderRequest request) {
        DispatchStrategy strategy = DispatchStrategy.fromCode(request.getStrategy());
        
        return staffList.stream()
                .filter(s -> s.getCurrentOrderCount() < s.getMaxOrderCapacity())
                .max((a, b) -> {
                    double scoreA = calculateStaffScore(a, request, strategy);
                    double scoreB = calculateStaffScore(b, request, strategy);
                    return Double.compare(scoreA, scoreB);
                })
                .orElse(staffList.get(0));
    }
    
    private double calculateStaffScore(DeliveryStaff staff, DispatchOrderRequest request, DispatchStrategy strategy) {
        double distance = calculateDistance(
                request.getPickupLng().doubleValue(), request.getPickupLat().doubleValue(),
                staff.getCurrentLng().doubleValue(), staff.getCurrentLat().doubleValue()
        );
        
        double score = 0;
        switch (strategy) {
            case NEAREST:
                score = 10000 - distance;
                break;
            case CAPACITY:
                score = (staff.getMaxOrderCapacity() - staff.getCurrentOrderCount()) * 1000;
                break;
            case RATING:
                score = staff.getRating().doubleValue() * 1000;
                break;
            case SMART:
            default:
                double distanceScore = (10000 - distance) * 0.4;
                double capacityScore = (staff.getMaxOrderCapacity() - staff.getCurrentOrderCount()) * 100 * 0.3;
                double ratingScore = staff.getRating().doubleValue() * 200 * 0.2;
                double speedScore = (60 - staff.getAvgDeliveryTime()) * 10 * 0.1;
                score = distanceScore + capacityScore + ratingScore + speedScore;
                break;
        }
        return score;
    }
    
    private List<RoutePlanRequest.DeliveryPoint> convertToPoints(List<DispatchOrderRequest.DeliveryPoint> points) {
        return points.stream()
                .map(p -> RoutePlanRequest.DeliveryPoint.builder()
                        .orderId(p.getOrderId())
                        .lng(p.getLng())
                        .lat(p.getLat())
                        .address(p.getAddress())
                        .build())
                .collect(Collectors.toList());
    }
    
    private Integer calculateTotalDistance(List<DispatchResultResponse.RouteNode> route) {
        return route.stream()
                .mapToInt(node -> 500) // 简化计算
                .sum();
    }
    
    private Integer calculateTotalTime(List<DispatchResultResponse.RouteNode> route) {
        return route.stream()
                .mapToInt(DispatchResultResponse.RouteNode::getEstimatedArrivalMinutes)
                .sum();
    }
    
    private String convertRouteToJson(List<DispatchResultResponse.RouteNode> route) {
        return route.toString();
    }
    
    private String generateBatchNo() {
        return "B" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
