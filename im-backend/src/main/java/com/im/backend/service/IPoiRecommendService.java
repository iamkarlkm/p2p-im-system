package com.im.backend.service;

import com.im.backend.dto.*;

/**
 * POI推荐服务接口
 * 提供基于位置的POI推荐功能
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface IPoiRecommendService {
    
    /**
     * 获取附近POI列表
     */
    NearbyPoiResponse getNearbyPois(NearbyPoiRequest request);
    
    /**
     * 获取个性化推荐
     */
    LocationRecommendResponse getPersonalizedRecommendations(LocationRecommendRequest request);
    
    /**
     * 获取热门POI
     */
    NearbyPoiResponse getHotPois(double latitude, double longitude, int radius, int limit);
    
    /**
     * 获取猜你喜欢
     */
    LocationRecommendResponse getGuessYouLike(Long userId, double latitude, double longitude);
    
    /**
     * 刷新推荐缓存
     */
    void refreshRecommendCache(Long userId);
    
    /**
     * 获取推荐原因
     */
    String getRecommendReason(Long poiId, Long userId);
}
