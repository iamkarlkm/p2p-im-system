package com.im.backend.modules.geofencing.service;

import com.im.backend.modules.geofencing.dto.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户围栏状态管理服务接口
 * 管理用户与围栏的实时状态关系
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface UserGeofenceStateService {
    
    /**
     * 处理用户位置上报
     * @param userId 用户ID
     * @param longitude 经度
     * @param latitude 纬度
     * @param accuracy 精度
     * @param source 定位来源
     * @return 触发的事件列表
     */
    List<GeofenceEventVO> processLocationReport(Long userId, BigDecimal longitude, 
                                                BigDecimal latitude, BigDecimal accuracy, 
                                                String source);
    
    /**
     * 批量处理位置上报（优化性能）
     * @param reports 位置上报列表
     * @return 触发事件列表
     */
    List<GeofenceEventVO> batchProcessLocationReports(List<LocationReportDTO> reports);
    
    /**
     * 获取用户当前所在的所有围栏
     * @param userId 用户ID
     * @return 围栏状态列表
     */
    List<UserGeofenceStateVO> getUserCurrentGeofences(Long userId);
    
    /**
     * 获取用户在特定围栏的状态
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @return 围栏状态
     */
    UserGeofenceStateVO getUserGeofenceState(Long userId, Long geofenceId);
    
    /**
     * 获取用户围栏历史记录
     * @param userId 用户ID
     * @param days 查询天数
     * @return 历史记录
     */
    List<GeofenceHistoryVO> getUserGeofenceHistory(Long userId, Integer days);
    
    /**
     * 订阅围栏
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     */
    void subscribeGeofence(Long userId, Long geofenceId);
    
    /**
     * 取消订阅围栏
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     */
    void unsubscribeGeofence(Long userId, Long geofenceId);
    
    /**
     * 获取用户订阅的围栏列表
     * @param userId 用户ID
     * @return 围栏列表
     */
    List<GeofenceListVO> getUserSubscribedGeofences(Long userId);
    
    /**
     * 检测位置作弊
     * @param userId 用户ID
     * @param longitude 经度
     * @param latitude 纬度
     * @param accuracy 精度
     * @return 作弊检测结果
     */
    SpoofingDetectionResultVO detectLocationSpoofing(Long userId, BigDecimal longitude,
                                                      BigDecimal latitude, BigDecimal accuracy);
    
    /**
     * 更新围栏状态
     * @param userId 用户ID
     * @param geofenceId 围栏ID
     * @param newState 新状态
     */
    void updateGeofenceState(Long userId, Long geofenceId, String newState);
    
    /**
     * 获取用户到店统计
     * @param userId 用户ID
     * @param days 统计天数
     * @return 到店统计
     */
    UserArrivalStatisticsVO getUserArrivalStatistics(Long userId, Integer days);
    
    /**
     * 清理过期状态数据
     * @param days 保留天数
     */
    void cleanupExpiredStates(Integer days);
}
