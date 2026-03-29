package com.im.backend.modules.local.service;

import com.im.backend.modules.local.dto.*;
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
     * 获取围栏详情
     */
    GeofenceResponse getGeofenceById(String geofenceId);
    
    /**
     * 获取商户的所有围栏
     */
    List<GeofenceResponse> getGeofencesByMerchant(String merchantId);
    
    /**
     * 更新围栏
     */
    GeofenceResponse updateGeofence(String geofenceId, CreateGeofenceRequest request);
    
    /**
     * 启用/禁用围栏
     */
    GeofenceResponse toggleGeofence(String geofenceId, Boolean enable);
    
    /**
     * 删除围栏
     */
    void deleteGeofence(String geofenceId);
    
    /**
     * 检查点是否在围栏内
     */
    boolean isPointInGeofence(String geofenceId, java.math.BigDecimal longitude, java.math.BigDecimal latitude);
    
    /**
     * 查找包含点的所有围栏
     */
    List<GeofenceResponse> findGeofencesByPoint(java.math.BigDecimal longitude, java.math.BigDecimal latitude);
    
    /**
     * 获取围栏运力负载
     */
    CapacityLoadResponse getCapacityLoad(String geofenceId);
    
    /**
     * 更新围栏负载
     */
    void updateCapacityLoad(String geofenceId);
    
    /**
     * 资源调度
     */
    ResourceDispatchResponse dispatchResource(ResourceDispatchRequest request);
    
    /**
     * 跨围栏借调运力
     */
    ResourceDispatchResponse crossFenceDispatch(String fromGeofenceId, String toGeofenceId, Integer staffCount);
}
