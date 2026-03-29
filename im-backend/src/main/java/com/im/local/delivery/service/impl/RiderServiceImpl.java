package com.im.local.delivery.service.impl;

import com.im.local.delivery.dto.*;
import com.im.local.delivery.entity.DeliveryRider;
import com.im.local.delivery.entity.RiderLocation;
import com.im.local.delivery.enums.RiderStatus;
import com.im.local.delivery.repository.DeliveryOrderMapper;
import com.im.local.delivery.repository.DeliveryRiderMapper;
import com.im.local.delivery.repository.RiderLocationMapper;
import com.im.local.delivery.service.IRiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 骑手服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements IRiderService {
    
    private final DeliveryRiderMapper riderMapper;
    private final RiderLocationMapper locationMapper;
    private final DeliveryOrderMapper orderMapper;
    private final StringRedisTemplate redisTemplate;
    
    private static final String RIDER_LOCATION_KEY = "rider:location:";
    private static final String RIDER_GEO_KEY = "rider:geo:available";
    
    @Override
    public boolean uploadLocation(RiderLocationUploadRequest request) {
        // 保存位置到数据库
        RiderLocation location = new RiderLocation();
        location.setRiderId(request.getRiderId());
        location.setDeliveryOrderId(request.getDeliveryOrderId());
        location.setLat(request.getLat());
        location.setLng(request.getLng());
        location.setAccuracy(request.getAccuracy());
        location.setAltitude(request.getAltitude());
        location.setSpeed(request.getSpeed());
        location.setDirection(request.getDirection());
        location.setLocationType(request.getLocationType());
        location.setBatteryLevel(request.getBatteryLevel());
        location.setLocatedAt(LocalDateTime.now());
        location.setGeoHash(location.calculateGeoHash(7));
        location.setIsMock(false);
        
        locationMapper.insert(location);
        
        // 更新骑手当前位置
        riderMapper.updateLocation(request.getRiderId(), request.getLat(), request.getLng());
        
        // 更新Redis位置缓存
        try {
            String key = RIDER_LOCATION_KEY + request.getRiderId();
            redisTemplate.opsForValue().set(key, request.getLat() + "," + request.getLng(), 5, TimeUnit.MINUTES);
            
            // 更新Geo索引（只有空闲骑手）
            DeliveryRider rider = riderMapper.selectById(request.getRiderId());
            if (rider != null && rider.getStatus() == 1) {
                redisTemplate.opsForGeo().add(RIDER_GEO_KEY, 
                    new org.springframework.data.geo.Point(request.getLng().doubleValue(), request.getLat().doubleValue()),
                    request.getRiderId().toString());
            }
        } catch (Exception e) {
            log.warn("Redis update failed: {}", e.getMessage());
        }
        
        return true;
    }
    
    @Override
    public RiderResponse getRiderInfo(Long riderId) {
        DeliveryRider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            return null;
        }
        return convertToResponse(rider);
    }
    
    @Override
    public boolean updateStatus(Long riderId, Integer status) {
        riderMapper.updateStatus(riderId, status);
        
        // 更新Redis Geo索引
        try {
            if (status == 1) { // 空闲
                DeliveryRider rider = riderMapper.selectById(riderId);
                if (rider != null && rider.getCurrentLat() != null) {
                    redisTemplate.opsForGeo().add(RIDER_GEO_KEY,
                        new org.springframework.data.geo.Point(rider.getCurrentLng().doubleValue(), 
                            rider.getCurrentLat().doubleValue()),
                        riderId.toString());
                }
            } else {
                redisTemplate.opsForGeo().remove(RIDER_GEO_KEY, riderId.toString());
            }
        } catch (Exception e) {
            log.warn("Redis geo update failed: {}", e.getMessage());
        }
        
        return true;
    }
    
    @Override
    public List<RiderLocationResponse> getRiderTrajectory(Long riderId, Integer hours) {
        return locationMapper.selectRecentByRider(riderId, hours != null ? hours : 2).stream()
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
    public List<RiderResponse> getNearbyAvailableRiders(Double lat, Double lng, Double radius) {
        try {
            // 使用Redis GeoRadius查询附近骑手
            org.springframework.data.geo.Circle circle = new org.springframework.data.geo.Circle(
                new org.springframework.data.geo.Point(lng, lat),
                new org.springframework.data.geo.Distance(radius != null ? radius : 5000, 
                    org.springframework.data.redis.connection.redis.GeoCommands.DistanceUnit.METERS)
            );
            
            org.springframework.data.redis.core.GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            List<org.springframework.data.geo.GeoResult<
                org.springframework.data.redis.connection.redis.GeoCommands.GeoLocation<String>>> results = 
                geoOps.radius(RIDER_GEO_KEY, circle).getContent();
            
            return results.stream()
                .map(result -> riderMapper.selectById(Long.parseLong(result.getContent().getName())))
                .filter(rider -> rider != null && rider.getStatus() == 1)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Redis geo query failed: {}", e.getMessage());
            // 降级：从数据库查询
            List<DeliveryRider> riders = riderMapper.selectAllAvailable();
            return riders.stream()
                .filter(r -> calculateDistance(lat, lng, r.getCurrentLat(), r.getCurrentLng()) < 
                    (radius != null ? radius : 5000))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        }
    }
    
    @Override
    public boolean goOnline(Long riderId) {
        updateStatus(riderId, RiderStatus.IDLE.getCode());
        log.info("Rider {} is now online", riderId);
        return true;
    }
    
    @Override
    public boolean goOffline(Long riderId) {
        updateStatus(riderId, RiderStatus.OFFLINE.getCode());
        
        // 从Geo索引移除
        try {
            redisTemplate.opsForGeo().remove(RIDER_GEO_KEY, riderId.toString());
        } catch (Exception e) {
            log.warn("Redis geo remove failed: {}", e.getMessage());
        }
        
        log.info("Rider {} is now offline", riderId);
        return true;
    }
    
    @Override
    public RiderTodayStatsResponse getTodayStats(Long riderId) {
        RiderTodayStatsResponse stats = new RiderTodayStatsResponse();
        
        DeliveryRider rider = riderMapper.selectById(riderId);
        if (rider != null) {
            stats.setTodayOrderCount(rider.getTodayOrderCount());
            stats.setTodayDistance(rider.getTodayDistance());
        }
        
        // 今日完成单数
        int completedCount = orderMapper.countTodayDeliveredByRider(riderId);
        stats.setTodayCompletedCount(completedCount);
        
        // 当前配送中订单数
        int deliveringCount = orderMapper.selectActiveByRider(riderId).size();
        stats.setDeliveringCount(deliveringCount);
        
        // 估算收入（每单配送费+小费）
        stats.setTodayIncome(BigDecimal.valueOf(completedCount * 8));
        
        return stats;
    }
    
    private RiderResponse convertToResponse(DeliveryRider rider) {
        RiderResponse resp = new RiderResponse();
        resp.setId(rider.getId());
        resp.setRealName(rider.getRealName());
        resp.setPhone(rider.getPhone());
        resp.setEmployeeNo(rider.getEmployeeNo());
        resp.setCurrentLat(rider.getCurrentLat());
        resp.setCurrentLng(rider.getCurrentLng());
        resp.setLocationUpdatedAt(rider.getLocationUpdatedAt());
        resp.setStatus(rider.getStatus());
        
        RiderStatus status = RiderStatus.fromCode(rider.getStatus());
        resp.setStatusName(status != null ? status.getName() : "未知");
        
        resp.setTodayOrderCount(rider.getTodayOrderCount());
        resp.setRating(rider.getRating());
        resp.setTotalDeliveries(rider.getTotalDeliveries());
        
        return resp;
    }
    
    private double calculateDistance(Double lat1, Double lng1, BigDecimal lat2, BigDecimal lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE;
        }
        return haversineDistance(lat1, lng1, lat2.doubleValue(), lng2.doubleValue());
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
}
