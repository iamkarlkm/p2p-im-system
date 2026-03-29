package com.im.service.discovery;

import com.im.entity.discovery.*;
import com.im.dto.discovery.*;
import java.util.List;
import java.util.Map;

/**
 * 探店发现服务接口
 */
public interface DiscoveryService {
    
    /**
     * 获取个性化探店推荐列表
     */
    List<DiscoveryRecommendation> getRecommendations(Long userId, Double longitude, Double latitude, 
                                                      Integer pageNum, Integer pageSize);
    
    /**
     * 获取新店发现列表
     */
    List<DiscoveryNewStore> getNewStores(String cityCode, Double longitude, Double latitude,
                                         Integer pageNum, Integer pageSize);
    
    /**
     * 获取探店榜单列表
     */
    List<DiscoveryRanking> getRankings(String cityCode, String rankingType, 
                                       Integer pageNum, Integer pageSize);
    
    /**
     * 获取榜单详情
     */
    DiscoveryRanking getRankingDetail(Long rankingId);
    
    /**
     * 创建打卡记录
     */
    DiscoveryCheckIn createCheckIn(DiscoveryCheckIn checkIn);
    
    /**
     * 获取用户打卡记录列表
     */
    List<DiscoveryCheckIn> getUserCheckIns(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取POI打卡记录列表
     */
    List<DiscoveryCheckIn> getPoiCheckIns(Long poiId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取探店路线列表
     */
    List<DiscoveryRoute> getRoutes(String sceneTag, String cityCode, 
                                   Integer pageNum, Integer pageSize);
    
    /**
     * 获取路线详情
     */
    DiscoveryRoute getRouteDetail(Long routeId);
    
    /**
     * 创建探店路线
     */
    DiscoveryRoute createRoute(DiscoveryRoute route);
    
    /**
     * 获取探店内容列表
     */
    List<DiscoveryContent> getContents(Long poiId, String contentType, 
                                       Integer pageNum, Integer pageSize);
    
    /**
     * 搜索探店内容
     */
    List<DiscoveryContent> searchContents(String keyword, String cityCode,
                                          Integer pageNum, Integer pageSize);
    
    /**
     * 地理围栏打卡检测
     */
    boolean checkGeofenceCheckIn(Long userId, Double longitude, Double latitude);
    
    /**
     * 获取用户探店统计
     */
    Map<String, Object> getUserDiscoveryStats(Long userId);
    
    /**
     * 获取附近热门探店地点
     */
    List<Map<String, Object>> getNearbyHotSpots(Double longitude, Double latitude, 
                                                Integer radius, Integer limit);
    
    /**
     * 生成智能探店路线
     */
    DiscoveryRoute generateSmartRoute(Long userId, Double startLng, Double startLat,
                                      String sceneTag, Integer poiCount, Double maxDistance);
    
    /**
     * 获取用户足迹地图
     */
    List<Map<String, Object>> getUserFootprintMap(Long userId, String cityCode);
    
    /**
     * 获取探店达人列表
     */
    List<Map<String, Object>> getExpertUsers(String cityCode, Integer pageNum, Integer pageSize);
    
    /**
     * 关注/取消关注探店达人
     */
    boolean followExpert(Long userId, Long expertId, boolean follow);
    
    /**
     * 获取关注达人的内容动态
     */
    List<DiscoveryContent> getFollowingExpertContents(Long userId, Integer pageNum, Integer pageSize);
}
