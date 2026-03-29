package com.im.service.geofence;

import com.im.entity.geofence.GeoFence;
import java.util.List;

/**
 * 地理围栏服务接口
 */
public interface GeoFenceService {
    
    /**
     * 创建围栏
     */
    GeoFence createFence(GeoFence fence);
    
    /**
     * 更新围栏
     */
    GeoFence updateFence(String fenceId, GeoFence fence);
    
    /**
     * 删除围栏
     */
    void deleteFence(String fenceId);
    
    /**
     * 根据ID获取围栏
     */
    GeoFence getFenceById(String fenceId);
    
    /**
     * 获取POI的所有围栏
     */
    List<GeoFence> getFencesByPoiId(String poiId);
    
    /**
     * 获取所有启用的围栏
     */
    List<GeoFence> getAllActiveFences();
    
    /**
     * 检查点是否在围栏内
     */
    boolean isPointInFence(Double longitude, Double latitude, GeoFence fence);
    
    /**
     * 批量检查点命中的围栏
     */
    List<GeoFence> checkPointInFences(Double longitude, Double latitude);
    
    /**
     * 启用围栏
     */
    void enableFence(String fenceId);
    
    /**
     * 禁用围栏
     */
    void disableFence(String fenceId);
}
