package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.model.dto.RiderResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 骑手服务接口
 */
public interface RiderService {
    
    /**
     * 注册骑手
     */
    RiderResponse registerRider(Long userId, String realName, String phone);
    
    /**
     * 获取骑手信息
     */
    RiderResponse getRiderById(Long riderId);
    
    /**
     * 更新骑手位置
     */
    void updateRiderLocation(Long riderId, BigDecimal lat, BigDecimal lng);
    
    /**
     * 获取骑手实时位置
     */
    Object getRiderLocation(Long riderId);
    
    /**
     * 计算预计到达时间
     */
    Integer calculateETA(Long riderId, BigDecimal targetLat, BigDecimal targetLng);
    
    /**
     * 获取附近骑手
     */
    List<RiderResponse> getNearbyRiders(BigDecimal lat, BigDecimal lng, Integer radius);
}
