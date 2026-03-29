package com.im.local.delivery.service;

import com.im.local.delivery.dto.*;
import java.util.List;

/**
 * 骑手服务接口
 */
public interface IRiderService {
    
    /**
     * 骑手上报位置
     */
    boolean uploadLocation(RiderLocationUploadRequest request);
    
    /**
     * 获取骑手信息
     */
    RiderResponse getRiderInfo(Long riderId);
    
    /**
     * 更新骑手状态
     */
    boolean updateStatus(Long riderId, Integer status);
    
    /**
     * 获取骑手位置轨迹
     */
    List<RiderLocationResponse> getRiderTrajectory(Long riderId, Integer hours);
    
    /**
     * 获取附近可用骑手
     */
    List<RiderResponse> getNearbyAvailableRiders(Double lat, Double lng, Double radius);
    
    /**
     * 骑手上线
     */
    boolean goOnline(Long riderId);
    
    /**
     * 骑手下线
     */
    boolean goOffline(Long riderId);
    
    /**
     * 获取骑手今日统计
     */
    RiderTodayStatsResponse getTodayStats(Long riderId);
}
