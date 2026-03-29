package com.im.backend.modules.delivery.service.impl;

import com.im.backend.modules.delivery.entity.DeliveryOrder;
import com.im.backend.modules.delivery.entity.DeliveryRider;
import com.im.backend.modules.delivery.enums.DeliveryOrderStatus;
import com.im.backend.modules.delivery.enums.RiderWorkStatus;
import com.im.backend.modules.delivery.repository.DeliveryOrderMapper;
import com.im.backend.modules.delivery.repository.DeliveryRiderMapper;
import com.im.backend.modules.delivery.service.IDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements IDispatchService {
    
    private final DeliveryOrderMapper orderMapper;
    private final DeliveryRiderMapper riderMapper;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String RIDER_GEO_KEY = "rider:location:geo";
    private static final double MAX_DISPATCH_DISTANCE = 5.0;
    private static final double BASE_FEE = 5.0;
    private static final double PER_KM_FEE = 1.5;
    private static final double PER_KG_FEE = 0.5;
    
    @Override
    public boolean dispatchOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || !order.getStatus().equals(DeliveryOrderStatus.PENDING_ASSIGN.getCode())) {
            return false;
        }
        
        Long riderId = findBestRider(order);
        if (riderId != null) {
            boolean success = orderMapper.assignOrder(orderId, riderId);
            if (success) {
                log.info("智能派单成功: orderId={}, riderId={}", orderId, riderId);
                return true;
            }
        }
        
        log.warn("智能派单失败，未找到合适骑手: orderId={}", orderId);
        return false;
    }
    
    @Override
    public int batchDispatch() {
        List<DeliveryOrder> pendingOrders = orderMapper.selectPendingOrders();
        int successCount = 0;
        
        for (DeliveryOrder order : pendingOrders) {
            if (dispatchOrder(order.getId())) {
                successCount++;
            }
        }
        
        log.info("批量派单完成: 总数={}, 成功={}", pendingOrders.size(), successCount);
        return successCount;
    }
    
    @Override
    public boolean redispatchOrder(Long orderId) {
        DeliveryOrder order = orderMapper.selectById(orderId);
        if (order == null || order.getRiderId() == null) {
            return false;
        }
        
        orderMapper.updateStatus(orderId, DeliveryOrderStatus.PENDING_ASSIGN.getCode());
        log.info("订单重新进入派单池: orderId={}", orderId);
        
        return dispatchOrder(orderId);
    }
    
    @Override
    public Double calculateDeliveryFee(Double distance, Double weight) {
        double fee = BASE_FEE;
        fee += distance * PER_KM_FEE;
        if (weight != null && weight > 0) {
            fee += weight * PER_KG_FEE;
        }
        return new BigDecimal(fee).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    private Long findBestRider(DeliveryOrder order) {
        Point center = new Point(order.getPickupLng().doubleValue(), order.getPickupLat().doubleValue());
        Distance distance = new Distance(MAX_DISPATCH_DISTANCE, Metrics.KILOMETERS);
        Circle circle = new Circle(center, distance);
        
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .sortAscending()
                .limit(10);
        
        var results = redisTemplate.opsForGeo().radius(RIDER_GEO_KEY, circle, args);
        
        if (results == null || results.isEmpty()) {
            return null;
        }
        
        for (var result : results) {
            String riderIdStr = result.getContent().getName();
            Long riderId = Long.valueOf(riderIdStr);
            
            DeliveryRider rider = riderMapper.selectById(riderId);
            if (rider != null && 
                rider.getWorkStatus().equals(RiderWorkStatus.ONLINE_IDLE.getCode()) &&
                rider.getAuthStatus().equals(2)) {
                return riderId;
            }
        }
        
        return null;
    }
}
