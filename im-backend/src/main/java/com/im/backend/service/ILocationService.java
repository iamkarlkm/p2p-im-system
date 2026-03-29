package com.im.backend.service;

import com.im.backend.dto.*;
import com.im.backend.entity.LocationPoint;

import java.util.List;

/**
 * 位置服务接口
 * 提供位置点管理和查询功能
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface ILocationService {
    
    /**
     * 上报位置
     */
    LocationPoint reportLocation(LocationPoint location);
    
    /**
     * 批量上报位置
     */
    List<LocationPoint> batchReportLocations(List<LocationPoint> locations);
    
    /**
     * 获取位置详情
     */
    LocationPoint getLocationById(Long id);
    
    /**
     * 获取用户最新位置
     */
    LocationPoint getUserLatestLocation(Long userId);
    
    /**
     * 获取用户位置历史
     */
    List<LocationPoint> getUserLocationHistory(Long userId, int days);
    
    /**
     * 附近查询（圆形范围）
     */
    List<LocationPoint> nearbySearch(double latitude, double longitude, double radius);
    
    /**
     * 附近查询（分页）
     */
    List<LocationPoint> nearbySearchWithPage(double latitude, double longitude, double radius, int pageNum, int pageSize);
    
    /**
     * 矩形范围查询
     */
    List<LocationPoint> boundingBoxSearch(double minLat, double maxLat, double minLon, double maxLon);
    
    /**
     * 删除过期位置
     */
    int deleteExpiredLocations();
    
    /**
     * 更新位置共享状态
     */
    boolean updateShareStatus(Long locationId, boolean isShared);
    
    /**
     * 获取附近用户数量
     */
    Long countNearbyUsers(double latitude, double longitude, double radius);
    
    /**
     * 获取位置分布统计
     */
    java.util.Map<String, Object> getLocationDistribution(String geohash);
}
