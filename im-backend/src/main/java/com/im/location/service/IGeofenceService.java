package com.im.location.service;

import com.im.location.dto.CreateGeofenceRequest;
import com.im.location.dto.GeofenceResponse;
import com.im.location.entity.GeofenceArea;
import com.im.location.entity.GeofenceTriggerRecord;

import java.util.List;

/**
 * 地理围栏服务接口
 */
public interface IGeofenceService {
    
    /**
     * 创建地理围栏
     */
    GeofenceResponse createGeofence(Long creatorId, CreateGeofenceRequest request);
    
    /**
     * 获取围栏详情
     */
    GeofenceResponse getGeofenceDetail(String geofenceId);
    
    /**
     * 获取会话的所有围栏
     */
    List<GeofenceResponse> getSessionGeofences(String sessionId);
    
    /**
     * 删除围栏
     */
    void deleteGeofence(String geofenceId);
    
    /**
     * 检查点是否在围栏内
     */
    boolean isPointInGeofence(Double longitude, Double latitude, GeofenceArea geofence);
    
    /**
     * 检查圆形围栏
     */
    boolean isPointInCircle(Double longitude, Double latitude, Double centerLng, Double centerLat, Integer radius);
    
    /**
     * 计算两点间距离(米)
     */
    double calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2);
    
    /**
     * 记录围栏触发
     */
    void recordTrigger(String geofenceId, String sessionId, Long userId, 
                       Integer triggerType, Double longitude, Double latitude);
    
    /**
     * 获取围栏触发记录
     */
    List<GeofenceTriggerRecord> getTriggerRecords(String geofenceId);
    
    /**
     * 处理未处理的触发记录
     */
    void processPendingTriggers();
}
