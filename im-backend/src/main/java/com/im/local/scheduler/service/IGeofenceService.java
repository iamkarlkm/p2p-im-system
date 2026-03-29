package com.im.local.scheduler.service;

import com.im.local.scheduler.dto.*;
import java.util.List;

/**
 * 围栏服务接口
 */
public interface IGeofenceService {
    
    /**
     * 创建围栏
     */
    GeofenceResponse createGeofence(CreateGeofenceRequest request);
    
    /**
     * 根据ID查询围栏
     */
    GeofenceResponse getGeofenceById(Long geofenceId);
    
    /**
     * 查询所有围栏
     */
    List<GeofenceResponse> listAllGeofences();
    
    /**
     * 查询城市围栏列表
     */
    List<GeofenceResponse> listGeofencesByCity(String cityCode);
    
    /**
     * 更新围栏动态边界
     */
    boolean updateDynamicBoundary(Long geofenceId);
    
    /**
     * 批量更新所有围栏动态边界
     */
    void batchUpdateDynamicBoundaries();
    
    /**
     * 获取围栏热力图数据
     */
    GeofenceHeatmapResponse getHeatmapData(Long geofenceId);
    
    /**
     * 检查围栏饱和度
     */
    boolean checkSaturation(Long geofenceId);
    
    /**
     * 删除围栏
     */
    boolean deleteGeofence(Long geofenceId);
    
    /**
     * 查询位置所属围栏
     */
    List<GeofenceResponse> findGeofencesByLocation(Double lng, Double lat);
}
