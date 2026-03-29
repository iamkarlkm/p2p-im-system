package com.im.backend.modules.logistics.service.impl;

import com.im.backend.modules.logistics.dto.*;
import com.im.backend.modules.logistics.entity.DeliveryRider;
import com.im.backend.modules.logistics.entity.RiderLocationTrace;
import com.im.backend.modules.logistics.repository.DeliveryRiderMapper;
import com.im.backend.modules.logistics.repository.RiderLocationTraceMapper;
import com.im.backend.modules.logistics.service.IRiderLocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 骑手位置服务实现
 */
@Slf4j
@Service
public class RiderLocationServiceImpl implements IRiderLocationService {

    @Autowired
    private RiderLocationTraceMapper traceMapper;

    @Autowired
    private DeliveryRiderMapper riderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String RIDER_GEO_KEY = "rider:geo:locations";
    private static final String RIDER_LOCATION_KEY = "rider:location:";

    @Override
    public boolean reportLocation(RiderLocationReportRequest request) {
        try {
            RiderLocationTrace trace = new RiderLocationTrace();
            BeanUtils.copyProperties(request, trace);
            trace.setCreateTime(LocalDateTime.now());
            
            if (trace.getReportTime() == null) {
                trace.setReportTime(LocalDateTime.now());
            }
            
            traceMapper.insert(trace);
            
            riderMapper.updateLocation(request.getRiderId(), request.getLongitude(), request.getLatitude());
            
            String redisKey = RIDER_LOCATION_KEY + request.getRiderId();
            redisTemplate.opsForHash().put(redisKey, "longitude", request.getLongitude().toString());
            redisTemplate.opsForHash().put(redisKey, "latitude", request.getLatitude().toString());
            redisTemplate.opsForHash().put(redisKey, "updateTime", LocalDateTime.now().toString());
            
            redisTemplate.opsForGeo().add(RIDER_GEO_KEY, 
                new Point(request.getLongitude().doubleValue(), request.getLatitude().doubleValue()),
                request.getRiderId().toString());
            
            return true;
        } catch (Exception e) {
            log.error("上报位置失败: riderId={}", request.getRiderId(), e);
            return false;
        }
    }

    @Override
    public boolean batchReportLocations(List<RiderLocationReportRequest> requests) {
        int successCount = 0;
        for (RiderLocationReportRequest request : requests) {
            if (reportLocation(request)) {
                successCount++;
            }
        }
        log.info("批量上报位置完成: 成功={}, 总数={}", successCount, requests.size());
        return successCount == requests.size();
    }

    @Override
    public RiderLocationResponse getRiderCurrentLocation(Long riderId) {
        String redisKey = RIDER_LOCATION_KEY + riderId;
        String longitude = (String) redisTemplate.opsForHash().get(redisKey, "longitude");
        String latitude = (String) redisTemplate.opsForHash().get(redisKey, "latitude");
        String updateTime = (String) redisTemplate.opsForHash().get(redisKey, "updateTime");
        
        RiderLocationResponse response = new RiderLocationResponse();
        response.setRiderId(riderId);
        
        if (longitude != null && latitude != null) {
            response.setLongitude(new BigDecimal(longitude));
            response.setLatitude(new BigDecimal(latitude));
            if (updateTime != null) {
                response.setLocationUpdateTime(LocalDateTime.parse(updateTime));
            }
        } else {
            DeliveryRider rider = riderMapper.selectById(riderId);
            if (rider != null) {
                response.setRiderName(rider.getRealName());
                response.setLongitude(rider.getCurrentLongitude());
                response.setLatitude(rider.getCurrentLatitude());
                response.setLocationUpdateTime(rider.getLocationUpdateTime());
            }
        }
        
        return response;
    }

    @Override
    public List<RiderLocationTrace> getOrderDeliveryTrace(Long orderId) {
        return traceMapper.selectByOrderId(orderId);
    }

    @Override
    public List<RiderLocationResponse> getNearbyRiders(Double longitude, Double latitude, Double radius) {
        List<RiderLocationResponse> result = new ArrayList<>();
        
        try {
            Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.KILOMETERS);
            Circle circle = new Circle(new Point(longitude, latitude), distance);
            
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending();
            
            var geoResults = redisTemplate.opsForGeo().radius(RIDER_GEO_KEY, circle, args);
            
            if (geoResults != null) {
                for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : geoResults) {
                    String riderIdStr = geoResult.getContent().getName();
                    Long riderId = Long.parseLong(riderIdStr);
                    
                    RiderLocationResponse location = getRiderCurrentLocation(riderId);
                    if (location.getLongitude() != null) {
                        result.add(location);
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询附近骑手失败", e);
        }
        
        return result;
    }

    @Override
    public boolean updateRiderWorkStatus(Long riderId, Integer workStatus) {
        return riderMapper.updateWorkStatus(riderId, workStatus) > 0;
    }
}
