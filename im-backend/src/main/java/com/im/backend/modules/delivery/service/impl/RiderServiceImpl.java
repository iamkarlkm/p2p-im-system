package com.im.backend.modules.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.im.backend.modules.delivery.dto.*;
import com.im.backend.modules.delivery.entity.DeliveryRider;
import com.im.backend.modules.delivery.entity.RiderLocation;
import com.im.backend.modules.delivery.enums.RiderAuthStatus;
import com.im.backend.modules.delivery.enums.RiderWorkStatus;
import com.im.backend.modules.delivery.repository.DeliveryRiderMapper;
import com.im.backend.modules.delivery.repository.RiderLocationMapper;
import com.im.backend.modules.delivery.service.IRiderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements IRiderService {
    
    private final DeliveryRiderMapper riderMapper;
    private final RiderLocationMapper locationMapper;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String RIDER_GEO_KEY = "rider:location:geo";
    
    @Override
    public RiderResponse getRiderById(Long riderId) {
        DeliveryRider rider = riderMapper.selectById(riderId);
        return rider != null ? convertToResponse(rider) : null;
    }
    
    @Override
    public RiderResponse getRiderByUserId(Long userId) {
        LambdaQueryWrapper<DeliveryRider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeliveryRider::getUserId, userId);
        DeliveryRider rider = riderMapper.selectOne(wrapper);
        return rider != null ? convertToResponse(rider) : null;
    }
    
    @Override
    public boolean uploadLocation(RiderLocationUploadRequest request) {
        RiderLocation location = new RiderLocation();
        BeanUtils.copyProperties(request, location);
        location.setRecordTime(LocalDateTime.now());
        location.setCreateTime(LocalDateTime.now());
        locationMapper.insert(location);
        
        riderMapper.updateLocation(request.getRiderId(), request.getLatitude(), request.getLongitude());
        
        String member = String.valueOf(request.getRiderId());
        Point point = new Point(request.getLongitude().doubleValue(), request.getLatitude().doubleValue());
        redisTemplate.opsForGeo().add(RIDER_GEO_KEY, point, member);
        
        return true;
    }
    
    @Override
    public List<RiderLocationResponse> getNearbyAvailableRiders(Double lat, Double lng, Double radius) {
        Point center = new Point(lng, lat);
        Distance distance = new Distance(radius, Metrics.KILOMETERS);
        Circle circle = new Circle(center, distance);
        
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .sortAscending()
                .limit(20);
        
        var results = redisTemplate.opsForGeo().radius(RIDER_GEO_KEY, circle, args);
        
        List<RiderLocationResponse> riders = new ArrayList<>();
        if (results != null) {
            for (var result : results) {
                String riderId = result.getContent().getName();
                RiderResponse rider = getRiderById(Long.valueOf(riderId));
                if (rider != null && rider.getWorkStatus().equals(RiderWorkStatus.ONLINE_IDLE.getCode())) {
                    RiderLocationResponse resp = new RiderLocationResponse();
                    resp.setRiderId(rider.getId());
                    resp.setRiderName(rider.getRiderName());
                    resp.setLatitude(rider.getCurrentLat());
                    resp.setLongitude(rider.getCurrentLng());
                    riders.add(resp);
                }
            }
        }
        return riders;
    }
    
    @Override
    public boolean riderGoOnline(Long riderId) {
        int rows = riderMapper.updateWorkStatus(riderId, RiderWorkStatus.ONLINE_IDLE.getCode());
        log.info("骑手上岗: riderId={}", riderId);
        return rows > 0;
    }
    
    @Override
    public boolean riderGoOffline(Long riderId) {
        int rows = riderMapper.updateWorkStatus(riderId, RiderWorkStatus.OFFLINE.getCode());
        redisTemplate.opsForGeo().remove(RIDER_GEO_KEY, String.valueOf(riderId));
        log.info("骑手下岗: riderId={}", riderId);
        return rows > 0;
    }
    
    @Override
    public boolean riderStartRest(Long riderId) {
        return riderMapper.updateWorkStatus(riderId, RiderWorkStatus.RESTING.getCode()) > 0;
    }
    
    @Override
    public boolean riderEndRest(Long riderId) {
        return riderMapper.updateWorkStatus(riderId, RiderWorkStatus.ONLINE_IDLE.getCode()) > 0;
    }
    
    @Override
    public RiderTodayStatsResponse getRiderTodayStats(Long riderId) {
        RiderTodayStatsResponse stats = new RiderTodayStatsResponse();
        stats.setRiderId(riderId);
        stats.setOrderCount(0);
        stats.setCompletedCount(0);
        stats.setTotalIncome(0.0);
        stats.setTotalDistance(0.0);
        stats.setOnlineMinutes(0);
        return stats;
    }
    
    private RiderResponse convertToResponse(DeliveryRider rider) {
        RiderResponse resp = new RiderResponse();
        BeanUtils.copyProperties(rider, resp);
        return resp;
    }
}
