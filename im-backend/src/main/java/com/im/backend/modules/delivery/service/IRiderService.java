package com.im.backend.modules.delivery.service;

import com.im.backend.modules.delivery.dto.*;
import java.util.List;

/**
 * 骑手服务接口
 */
public interface IRiderService {
    
    RiderResponse getRiderById(Long riderId);
    RiderResponse getRiderByUserId(Long userId);
    
    boolean uploadLocation(RiderLocationUploadRequest request);
    List<RiderLocationResponse> getNearbyAvailableRiders(Double lat, Double lng, Double radius);
    
    boolean riderGoOnline(Long riderId);
    boolean riderGoOffline(Long riderId);
    boolean riderStartRest(Long riderId);
    boolean riderEndRest(Long riderId);
    
    RiderTodayStatsResponse getRiderTodayStats(Long riderId);
}
