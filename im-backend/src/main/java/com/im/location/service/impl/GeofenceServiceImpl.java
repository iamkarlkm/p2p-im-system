package com.im.location.service.impl;

import com.im.common.utils.SnowflakeIdGenerator;
import com.im.location.dto.CreateGeofenceRequest;
import com.im.location.dto.GeofenceResponse;
import com.im.location.entity.GeofenceArea;
import com.im.location.entity.GeofenceTriggerRecord;
import com.im.location.enums.*;
import com.im.location.repository.GeofenceAreaMapper;
import com.im.location.repository.GeofenceTriggerRecordMapper;
import com.im.location.service.IGeofenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地理围栏服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceServiceImpl implements IGeofenceService {
    
    private final GeofenceAreaMapper geofenceAreaMapper;
    private final GeofenceTriggerRecordMapper triggerRecordMapper;
    
    // 地球半径(米)
    private static final double EARTH_RADIUS = 6371000;
    
    @Override
    public GeofenceResponse createGeofence(Long creatorId, CreateGeofenceRequest request) {
        String geofenceId = "GF" + SnowflakeIdGenerator.nextId();
        
        GeofenceArea geofence = new GeofenceArea();
        geofence.setGeofenceId(geofenceId);
        geofence.setGeofenceType(request.getGeofenceType());
        geofence.setName(request.getName());
        geofence.setSessionId(request.getSessionId());
        geofence.setCenterLongitude(request.getCenterLongitude());
        geofence.setCenterLatitude(request.getCenterLatitude());
        geofence.setRadius(request.getRadius());
        geofence.setPolygonCoordinates(request.getPolygonCoordinates());
        geofence.setPurpose(request.getPurpose());
        geofence.setTriggerEvent(request.getTriggerEvent());
        geofence.setDwellTime(request.getDwellTime());
        geofence.setStatus(1);
        geofence.setCreatorId(creatorId);
        geofence.setCreateTime(LocalDateTime.now());
        geofence.setUpdateTime(LocalDateTime.now());
        
        geofenceAreaMapper.insert(geofence);
        
        return convertToGeofenceResponse(geofence);
    }
    
    @Override
    public GeofenceResponse getGeofenceDetail(String geofenceId) {
        GeofenceArea geofence = geofenceAreaMapper.selectByGeofenceId(geofenceId);
        if (geofence == null) {
            return null;
        }
        return convertToGeofenceResponse(geofence);
    }
    
    @Override
    public List<GeofenceResponse> getSessionGeofences(String sessionId) {
        List<GeofenceArea> geofences = geofenceAreaMapper.selectBySessionId(sessionId);
        return geofences.stream().map(this::convertToGeofenceResponse).collect(Collectors.toList());
    }
    
    @Override
    public void deleteGeofence(String geofenceId) {
        GeofenceArea geofence = geofenceAreaMapper.selectByGeofenceId(geofenceId);
        if (geofence != null) {
            geofence.setStatus(0);
            geofence.setUpdateTime(LocalDateTime.now());
            geofenceAreaMapper.updateById(geofence);
        }
    }
    
    @Override
    public boolean isPointInGeofence(Double longitude, Double latitude, GeofenceArea geofence) {
        if (geofence == null || geofence.getStatus() != 1) {
            return false;
        }
        
        if (geofence.getGeofenceType().equals(GeofenceType.CIRCLE.getCode())) {
            return isPointInCircle(longitude, latitude, 
                geofence.getCenterLongitude(), geofence.getCenterLatitude(), geofence.getRadius());
        }
        // 多边形围栏检查(简化实现)
        return false;
    }
    
    @Override
    public boolean isPointInCircle(Double longitude, Double latitude, 
                                   Double centerLng, Double centerLat, Integer radius) {
        if (centerLng == null || centerLat == null || radius == null) {
            return false;
        }
        double distance = calculateDistance(longitude, latitude, centerLng, centerLat);
        return distance <= radius;
    }
    
    @Override
    public double calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
            Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        return s * EARTH_RADIUS;
    }
    
    @Override
    public void recordTrigger(String geofenceId, String sessionId, Long userId,
                              Integer triggerType, Double longitude, Double latitude) {
        GeofenceTriggerRecord record = new GeofenceTriggerRecord();
        record.setGeofenceId(geofenceId);
        record.setSessionId(sessionId);
        record.setUserId(userId);
        record.setTriggerType(triggerType);
        record.setLongitude(longitude);
        record.setLatitude(latitude);
        record.setTriggerTime(LocalDateTime.now());
        record.setProcessStatus(0);
        record.setCreateTime(LocalDateTime.now());
        triggerRecordMapper.insert(record);
    }
    
    @Override
    public List<GeofenceTriggerRecord> getTriggerRecords(String geofenceId) {
        return triggerRecordMapper.selectByGeofenceId(geofenceId);
    }
    
    @Override
    public void processPendingTriggers() {
        List<GeofenceTriggerRecord> pendingRecords = triggerRecordMapper.selectUnprocessedRecords();
        for (GeofenceTriggerRecord record : pendingRecords) {
            // 处理触发事件，如发送IM消息通知
            log.info("处理围栏触发: geofenceId={}, userId={}, type={}", 
                record.getGeofenceId(), record.getUserId(), record.getTriggerType());
            record.setProcessStatus(1);
            triggerRecordMapper.updateById(record);
        }
    }
    
    private GeofenceResponse convertToGeofenceResponse(GeofenceArea geofence) {
        GeofenceResponse response = new GeofenceResponse();
        response.setGeofenceId(geofence.getGeofenceId());
        response.setName(geofence.getName());
        response.setGeofenceType(geofence.getGeofenceType());
        response.setGeofenceTypeDesc(GeofenceType.fromCode(geofence.getGeofenceType()).getDesc());
        response.setCenterLongitude(geofence.getCenterLongitude());
        response.setCenterLatitude(geofence.getCenterLatitude());
        response.setRadius(geofence.getRadius());
        response.setPurpose(geofence.getPurpose());
        response.setPurposeDesc(GeofencePurpose.fromCode(geofence.getPurpose()).getDesc());
        response.setTriggerEvent(geofence.getTriggerEvent());
        response.setCreateTime(geofence.getCreateTime());
        return response;
    }
}
