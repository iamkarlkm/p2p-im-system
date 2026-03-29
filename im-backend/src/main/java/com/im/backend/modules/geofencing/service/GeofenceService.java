package com.im.backend.modules.geofencing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.result.PageResult;
import com.im.backend.modules.geofencing.entity.GeofenceZone;
import com.im.backend.modules.geofencing.dto.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 地理围栏服务接口
 * 智能到店服务核心服务层
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface GeofenceService extends IService<GeofenceZone> {
    
    /**
     * 创建地理围栏
     * @param dto 围栏创建DTO
     * @return 创建的围栏ID
     */
    Long createGeofence(GeofenceCreateDTO dto);
    
    /**
     * 更新地理围栏
     * @param id 围栏ID
     * @param dto 围栏更新DTO
     */
    void updateGeofence(Long id, GeofenceUpdateDTO dto);
    
    /**
     * 删除地理围栏
     * @param id 围栏ID
     */
    void deleteGeofence(Long id);
    
    /**
     * 获取围栏详情
     * @param id 围栏ID
     * @return 围栏详情
     */
    GeofenceDetailVO getGeofenceDetail(Long id);
    
    /**
     * 分页查询围栏列表
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<GeofenceListVO> queryGeofencePage(GeofenceQueryDTO query);
    
    /**
     * 查询商户的所有围栏
     * @param merchantId 商户ID
     * @return 围栏列表
     */
    List<GeofenceListVO> getMerchantGeofences(Long merchantId);
    
    /**
     * 查询POI关联的围栏
     * @param poiId POI ID
     * @return 围栏列表
     */
    List<GeofenceListVO> getPoiGeofences(Long poiId);
    
    /**
     * 判断点是否在围栏内
     * @param geofenceId 围栏ID
     * @param longitude 经度
     * @param latitude 纬度
     * @return 是否在围栏内
     */
    boolean isPointInGeofence(Long geofenceId, BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 批量判断点在哪些围栏内
     * @param longitude 经度
     * @param latitude 纬度
     * @return 围栏ID列表
     */
    List<Long> findGeofencesByPoint(BigDecimal longitude, BigDecimal latitude);
    
    /**
     * 查询附近围栏（基于GeoHash）
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径（米）
     * @return 围栏列表
     */
    List<GeofenceListVO> findNearbyGeofences(BigDecimal longitude, BigDecimal latitude, Integer radius);
    
    /**
     * 启用围栏
     * @param id 围栏ID
     */
    void enableGeofence(Long id);
    
    /**
     * 禁用围栏
     * @param id 围栏ID
     */
    void disableGeofence(Long id);
    
    /**
     * 复制围栏
     * @param id 源围栏ID
     * @param targetPoiId 目标POI ID
     * @return 新围栏ID
     */
    Long copyGeofence(Long id, Long targetPoiId);
    
    /**
     * 获取围栏层级树
     * @param merchantId 商户ID
     * @return 围栏层级树
     */
    List<GeofenceTreeVO> getGeofenceTree(Long merchantId);
    
    /**
     * 计算围栏面积
     * @param geofenceId 围栏ID
     * @return 面积（平方米）
     */
    BigDecimal calculateArea(Long geofenceId);
    
    /**
     * 批量创建围栏（从POI列表）
     * @param poiIds POI ID列表
     * @param radius 默认半径
     */
    void batchCreateFromPois(List<Long> poiIds, Integer radius);
    
    /**
     * 获取围栏统计信息
     * @param merchantId 商户ID
     * @return 统计信息
     */
    GeofenceStatisticsVO getGeofenceStatistics(Long merchantId);
}
