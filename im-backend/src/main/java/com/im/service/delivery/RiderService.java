package com.im.service.delivery;

import com.im.entity.delivery.DeliveryRider;
import com.im.entity.delivery.RiderLocation;
import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手服务接口 - 即时配送运力调度系统
 */
public interface RiderService {
    
    /**
     * 注册骑手
     */
    DeliveryRider registerRider(DeliveryRider rider);
    
    /**
     * 获取骑手信息
     */
    DeliveryRider getRiderById(Long riderId);
    
    /**
     * 根据用户ID获取骑手信息
     */
    DeliveryRider getRiderByUserId(Long userId);
    
    /**
     * 更新骑手工作状态
     */
    void updateWorkStatus(Long riderId, String status);
    
    /**
     * 更新骑手位置
     */
    void updateLocation(Long riderId, BigDecimal longitude, BigDecimal latitude, String source);
    
    /**
     * 批量更新骑手位置(批量上报优化)
     */
    void batchUpdateLocations(List<RiderLocation> locations);
    
    /**
     * 获取附近可用骑手
     */
    List<DeliveryRider> findNearbyAvailableRiders(BigDecimal longitude, BigDecimal latitude, Integer radius);
    
    /**
     * 获取骑手位置历史
     */
    List<RiderLocation> getRiderLocationHistory(Long riderId, String startTime, String endTime);
    
    /**
     * 获取骑手实时位置
     */
    RiderLocation getRiderCurrentLocation(Long riderId);
    
    /**
     * 增加骑手活跃订单数
     */
    void incrementActiveOrderCount(Long riderId);
    
    /**
     * 减少骑手活跃订单数
     */
    void decrementActiveOrderCount(Long riderId);
    
    /**
     * 获取区域内所有在线骑手
     */
    List<DeliveryRider> getOnlineRidersByZone(Long zoneId);
    
    /**
     * 骑手签到(开始工作)
     */
    void riderCheckIn(Long riderId);
    
    /**
     * 骑手签退(结束工作)
     */
    void riderCheckOut(Long riderId);
    
    /**
     * 更新骑手今日统计
     */
    void updateDailyStats(Long riderId, Integer completedCount, BigDecimal income);
    
    /**
     * 获取骑手今日统计
     */
    DeliveryRider getTodayStats(Long riderId);
    
    /**
     * 审核骑手
     */
    void verifyRider(Long riderId, String status, String reason);
    
    /**
     * 获取骑手列表
     */
    List<DeliveryRider> getRiderList(String status, Long zoneId, Integer page, Integer size);
    
    /**
     * 骑手评级更新
     */
    void updateRiderRating(Long riderId, BigDecimal rating);
}
