package com.im.service.geofence.service;

import com.im.service.geofence.dto.*;
import com.im.service.geofence.entity.Geofence;
import com.im.service.geofence.entity.GeofenceEvent;
import com.im.service.geofence.entity.LocationShare;

import java.util.List;

/**
 * 地理围栏服务接口
 */
public interface GeofenceService {

    // ==================== 围栏管理 ====================

    /**
     * 创建围栏
     */
    GeofenceResponse createGeofence(CreateGeofenceRequest request);

    /**
     * 更新围栏
     */
    GeofenceResponse updateGeofence(String geofenceId, CreateGeofenceRequest request);

    /**
     * 删除围栏
     */
    boolean deleteGeofence(String geofenceId);

    /**
     * 获取围栏详情
     */
    GeofenceResponse getGeofenceById(String geofenceId);

    /**
     * 获取商户的所有围栏
     */
    List<GeofenceResponse> getGeofencesByMerchant(String merchantId);

    /**
     * 获取POI的所有围栏
     */
    List<GeofenceResponse> getGeofencesByPoi(String poiId);

    /**
     * 获取所有激活的围栏
     */
    List<GeofenceResponse> getAllActiveGeofences();

    /**
     * 启用围栏
     */
    GeofenceResponse enableGeofence(String geofenceId);

    /**
     * 禁用围栏
     */
    GeofenceResponse disableGeofence(String geofenceId);

    /**
     * 复制围栏到目标POI
     */
    String copyGeofence(String geofenceId, String targetPoiId);

    /**
     * 获取围栏层级树
     */
    List<GeofenceResponse> getGeofenceTree(String merchantId);

    /**
     * 查询附近的围栏
     */
    List<GeofenceResponse> findNearbyGeofences(Double longitude, Double latitude, Integer radius);

    // ==================== 位置上报 ====================

    /**
     * 上报位置并检查围栏
     */
    LocationReportResponse reportLocation(LocationReportRequest request);

    /**
     * 检查点是否在围栏内
     */
    boolean isPointInGeofence(String geofenceId, Double longitude, Double latitude);

    /**
     * 查询点所在的所有围栏
     */
    List<String> findGeofencesByPoint(Double longitude, Double latitude);

    // ==================== 事件查询 ====================

    /**
     * 获取围栏的触发事件记录
     */
    List<GeofenceEvent> getGeofenceEvents(String geofenceId, Integer limit);

    /**
     * 获取用户的围栏事件记录
     */
    List<GeofenceEvent> getUserGeofenceEvents(String userId, Integer limit);
}
