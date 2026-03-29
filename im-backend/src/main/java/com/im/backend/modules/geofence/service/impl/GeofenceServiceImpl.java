package com.im.backend.modules.geofence.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.backend.modules.geofence.dto.*;
import com.im.backend.modules.geofence.entity.Geofence;
import com.im.backend.modules.geofence.entity.GeofenceTrigger;
import com.im.backend.modules.geofence.enums.TriggerCondition;
import com.im.backend.modules.geofence.enums.TriggerType;
import com.im.backend.modules.geofence.repository.GeofenceMapper;
import com.im.backend.modules.geofence.repository.GeofenceTriggerMapper;
import com.im.backend.modules.geofence.service.IGeofenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceServiceImpl extends ServiceImpl<GeofenceMapper, Geofence> implements IGeofenceService {

    private final GeofenceMapper geofenceMapper;
    private final GeofenceTriggerMapper triggerMapper;
    private final StringRedisTemplate redisTemplate;

    private static final double EARTH_RADIUS = 6371000; // 地球半径(米)
    private static final String GEOFENCE_STATUS_KEY = "geofence:status:%d:%d";

    @Override
    @Transactional
    public GeofenceResponse createGeofence(CreateGeofenceRequest request) {
        Geofence geofence = new Geofence();
        BeanUtils.copyProperties(request, geofence);
        
        if (request.getPolygonPoints() != null) {
            geofence.setPolygonPoints(JSON.toJSONString(request.getPolygonPoints()));
        }
        geofence.setStatus("ACTIVE");
        
        geofenceMapper.insert(geofence);
        return convertToResponse(geofence);
    }

    @Override
    @Transactional
    public GeofenceResponse updateGeofence(Long id, CreateGeofenceRequest request) {
        Geofence geofence = geofenceMapper.selectById(id);
        if (geofence == null) {
            throw new RuntimeException("围栏不存在");
        }
        
        BeanUtils.copyProperties(request, geofence);
        if (request.getPolygonPoints() != null) {
            geofence.setPolygonPoints(JSON.toJSONString(request.getPolygonPoints()));
        }
        
        geofenceMapper.updateById(geofence);
        return convertToResponse(geofence);
    }

    @Override
    @Transactional
    public void deleteGeofence(Long id) {
        geofenceMapper.deleteById(id);
    }

    @Override
    public GeofenceResponse getGeofenceById(Long id) {
        Geofence geofence = geofenceMapper.selectById(id);
        return geofence != null ? convertToResponse(geofence) : null;
    }

    @Override
    public List<GeofenceResponse> getGeofencesByMerchantId(Long merchantId) {
        return geofenceMapper.selectByMerchantId(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceResponse> getGeofencesByStoreId(Long storeId) {
        return geofenceMapper.selectByStoreId(storeId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GeofenceTriggerEvent> reportLocation(Long userId, LocationReportRequest request) {
        checkGeofenceTriggers(userId, request.getLongitude(), request.getLatitude());
        
        // 从Redis获取用户触发的事件
        String key = String.format("geofence:events:%d", userId);
        String eventsJson = redisTemplate.opsForValue().get(key);
        if (eventsJson != null) {
            redisTemplate.delete(key);
            return JSON.parseArray(eventsJson, GeofenceTriggerEvent.class);
        }
        return new ArrayList<>();
    }

    @Override
    public void checkGeofenceTriggers(Long userId, Double longitude, Double latitude) {
        List<Geofence> activeGeofences = geofenceMapper.selectAllActive();
        List<GeofenceTriggerEvent> events = new ArrayList<>();
        
        for (Geofence geofence : activeGeofences) {
            String statusKey = String.format(GEOFENCE_STATUS_KEY, userId, geofence.getId());
            String previousStatus = redisTemplate.opsForValue().get(statusKey);
            boolean currentlyInside = isPointInGeofence(longitude, latitude, geofence);
            
            if ("INSIDE".equals(previousStatus) && !currentlyInside) {
                // 离开围栏
                if ("EXIT".equals(geofence.getTriggerCondition()) || "DWELL".equals(geofence.getTriggerCondition())) {
                    saveTrigger(userId, geofence, longitude, latitude, TriggerType.EXIT);
                    events.add(createTriggerEvent(geofence, TriggerType.EXIT, longitude, latitude, false));
                }
                redisTemplate.delete(statusKey);
            } else if (!"INSIDE".equals(previousStatus) && currentlyInside) {
                // 进入围栏
                if ("ENTER".equals(geofence.getTriggerCondition()) || "DWELL".equals(geofence.getTriggerCondition())) {
                    saveTrigger(userId, geofence, longitude, latitude, TriggerType.ENTER);
                    events.add(createTriggerEvent(geofence, TriggerType.ENTER, longitude, latitude, true));
                }
                redisTemplate.opsForValue().set(statusKey, "INSIDE", 24, TimeUnit.HOURS);
            }
        }
        
        if (!events.isEmpty()) {
            String key = String.format("geofence:events:%d", userId);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(events), 5, TimeUnit.MINUTES);
        }
    }

    private void saveTrigger(Long userId, Geofence geofence, Double lng, Double lat, TriggerType type) {
        GeofenceTrigger trigger = new GeofenceTrigger();
        trigger.setGeofenceId(geofence.getId());
        trigger.setUserId(userId);
        trigger.setMerchantId(geofence.getMerchantId());
        trigger.setStoreId(geofence.getStoreId());
        trigger.setTriggerType(type.getCode());
        trigger.setLongitude(lng);
        trigger.setLatitude(lat);
        trigger.setTriggerTime(LocalDateTime.now());
        trigger.setProcessStatus("PENDING");
        triggerMapper.insert(trigger);
    }

    private GeofenceTriggerEvent createTriggerEvent(Geofence geofence, TriggerType type, 
                                                     Double lng, Double lat, boolean isEnter) {
        GeofenceTriggerEvent event = new GeofenceTriggerEvent();
        event.setGeofenceId(geofence.getId());
        event.setMerchantId(geofence.getMerchantId());
        event.setStoreId(geofence.getStoreId());
        event.setGeofenceName(geofence.getName());
        event.setTriggerType(type.getCode());
        event.setLongitude(lng);
        event.setLatitude(lat);
        event.setTriggerTime(LocalDateTime.now());
        event.setIsEnter(isEnter);
        return event;
    }

    @Override
    public double calculateDistanceToGeofence(Double longitude, Double latitude, Geofence geofence) {
        if ("CIRCLE".equals(geofence.getType())) {
            return calculateDistance(longitude, latitude, geofence.getCenterLongitude(), geofence.getCenterLatitude());
        }
        return 0;
    }

    @Override
    public boolean isPointInGeofence(Double longitude, Double latitude, Geofence geofence) {
        if ("CIRCLE".equals(geofence.getType())) {
            double distance = calculateDistance(longitude, latitude, 
                    geofence.getCenterLongitude(), geofence.getCenterLatitude());
            return distance <= geofence.getRadius();
        } else if ("POLYGON".equals(geofence.getType())) {
            return isPointInPolygon(longitude, latitude, geofence.getPolygonPoints());
        }
        return false;
    }

    private double calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private boolean isPointInPolygon(Double longitude, Double latitude, String polygonPointsJson) {
        // 射线法判断点是否在多边形内
        List<GeofenceResponse.Point> points = JSON.parseArray(polygonPointsJson, GeofenceResponse.Point.class);
        if (points == null || points.size() < 3) return false;
        
        boolean inside = false;
        int j = points.size() - 1;
        for (int i = 0; i < points.size(); i++) {
            GeofenceResponse.Point pi = points.get(i);
            GeofenceResponse.Point pj = points.get(j);
            
            if (((pi.getLat() > latitude) != (pj.getLat() > latitude)) &&
                    (longitude < (pj.getLng() - pi.getLng()) * (latitude - pi.getLat()) / (pj.getLat() - pi.getLat()) + pi.getLng())) {
                inside = !inside;
            }
            j = i;
        }
        return inside;
    }

    @Override
    @Transactional
    public void activateGeofence(Long id) {
        Geofence geofence = geofenceMapper.selectById(id);
        if (geofence != null) {
            geofence.setStatus("ACTIVE");
            geofenceMapper.updateById(geofence);
        }
    }

    @Override
    @Transactional
    public void deactivateGeofence(Long id) {
        Geofence geofence = geofenceMapper.selectById(id);
        if (geofence != null) {
            geofence.setStatus("INACTIVE");
            geofenceMapper.updateById(geofence);
        }
    }

    private GeofenceResponse convertToResponse(Geofence geofence) {
        GeofenceResponse response = new GeofenceResponse();
        BeanUtils.copyProperties(geofence, response);
        if (geofence.getPolygonPoints() != null) {
            response.setPolygonPoints(JSON.parseArray(geofence.getPolygonPoints(), GeofenceResponse.Point.class));
        }
        return response;
    }
}
