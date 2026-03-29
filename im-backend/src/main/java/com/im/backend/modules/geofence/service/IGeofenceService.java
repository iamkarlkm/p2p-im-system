package com.im.backend.modules.geofence.service;

import com.im.backend.modules.geofence.dto.*;
import com.im.backend.modules.geofence.entity.Geofence;

import java.util.List;

/**
 * 地理围栏服务接口
 */
public interface IGeofenceService {

    /**
     * 创建地理围栏
     */
    GeofenceResponse createGeofence(CreateGeofenceRequest request);

    /**
     * 更新地理围栏
     */
    GeofenceResponse updateGeofence(Long id, CreateGeofenceRequest request);

    /**
     * 删除地理围栏
     */
    void deleteGeofence(Long id);

    /**
     * 获取围栏详情
     */
    GeofenceResponse getGeofenceById(Long id);

    /**
     * 获取商户围栏列表
     */
    List<GeofenceResponse> getGeofencesByMerchantId(Long merchantId);

    /**
     * 获取门店围栏列表
     */
    List<GeofenceResponse> getGeofencesByStoreId(Long storeId);

    /**
     * 上报位置并检测围栏
     */
    List<GeofenceTriggerEvent> reportLocation(Long userId, LocationReportRequest request);

    /**
     * 批量检测围栏触发
     */
    void checkGeofenceTriggers(Long userId, Double longitude, Double latitude);

    /**
     * 计算点到围栏边界的距离
     */
    double calculateDistanceToGeofence(Double longitude, Double latitude, Geofence geofence);

    /**
     * 判断点是否在围栏内
     */
    boolean isPointInGeofence(Double longitude, Double latitude, Geofence geofence);

    /**
     * 激活围栏
     */
    void activateGeofence(Long id);

    /**
     * 停用围栏
     */
    void deactivateGeofence(Long id);
}
