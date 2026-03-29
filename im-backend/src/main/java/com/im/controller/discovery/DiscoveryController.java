package com.im.controller.discovery;

import com.im.common.Result;
import com.im.service.discovery.DiscoveryService;
import com.im.entity.discovery.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 探店发现控制器
 */
@RestController
@RequestMapping("/api/v1/discovery")
public class DiscoveryController {
    
    @Autowired
    private DiscoveryService discoveryService;
    
    /**
     * 获取个性化探店推荐
     */
    @GetMapping("/recommendations")
    public Result<List<DiscoveryRecommendation>> getRecommendations(
            @RequestParam Long userId,
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getRecommendations(userId, longitude, latitude, pageNum, pageSize));
    }
    
    /**
     * 获取新店发现
     */
    @GetMapping("/new-stores")
    public Result<List<DiscoveryNewStore>> getNewStores(
            @RequestParam String cityCode,
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getNewStores(cityCode, longitude, latitude, pageNum, pageSize));
    }
    
    /**
     * 获取探店榜单列表
     */
    @GetMapping("/rankings")
    public Result<List<DiscoveryRanking>> getRankings(
            @RequestParam String cityCode,
            @RequestParam(required = false) String rankingType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(discoveryService.getRankings(cityCode, rankingType, pageNum, pageSize));
    }
    
    /**
     * 获取榜单详情
     */
    @GetMapping("/rankings/{rankingId}")
    public Result<DiscoveryRanking> getRankingDetail(@PathVariable Long rankingId) {
        return Result.success(discoveryService.getRankingDetail(rankingId));
    }
    
    /**
     * 创建打卡记录
     */
    @PostMapping("/check-ins")
    public Result<DiscoveryCheckIn> createCheckIn(@RequestBody DiscoveryCheckIn checkIn) {
        return Result.success(discoveryService.createCheckIn(checkIn));
    }
    
    /**
     * 获取用户打卡记录
     */
    @GetMapping("/users/{userId}/check-ins")
    public Result<List<DiscoveryCheckIn>> getUserCheckIns(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getUserCheckIns(userId, pageNum, pageSize));
    }
    
    /**
     * 获取POI打卡记录
     */
    @GetMapping("/pois/{poiId}/check-ins")
    public Result<List<DiscoveryCheckIn>> getPoiCheckIns(
            @PathVariable Long poiId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getPoiCheckIns(poiId, pageNum, pageSize));
    }
    
    /**
     * 获取探店路线列表
     */
    @GetMapping("/routes")
    public Result<List<DiscoveryRoute>> getRoutes(
            @RequestParam(required = false) String sceneTag,
            @RequestParam String cityCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getRoutes(sceneTag, cityCode, pageNum, pageSize));
    }
    
    /**
     * 获取路线详情
     */
    @GetMapping("/routes/{routeId}")
    public Result<DiscoveryRoute> getRouteDetail(@PathVariable Long routeId) {
        return Result.success(discoveryService.getRouteDetail(routeId));
    }
    
    /**
     * 创建探店路线
     */
    @PostMapping("/routes")
    public Result<DiscoveryRoute> createRoute(@RequestBody DiscoveryRoute route) {
        return Result.success(discoveryService.createRoute(route));
    }
    
    /**
     * 获取探店内容列表
     */
    @GetMapping("/contents")
    public Result<List<DiscoveryContent>> getContents(
            @RequestParam(required = false) Long poiId,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getContents(poiId, contentType, pageNum, pageSize));
    }
    
    /**
     * 搜索探店内容
     */
    @GetMapping("/contents/search")
    public Result<List<DiscoveryContent>> searchContents(
            @RequestParam String keyword,
            @RequestParam String cityCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.searchContents(keyword, cityCode, pageNum, pageSize));
    }
    
    /**
     * 地理围栏打卡检测
     */
    @PostMapping("/geofence/check-in")
    public Result<Boolean> checkGeofenceCheckIn(
            @RequestParam Long userId,
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        return Result.success(discoveryService.checkGeofenceCheckIn(userId, longitude, latitude));
    }
    
    /**
     * 获取用户探店统计
     */
    @GetMapping("/users/{userId}/stats")
    public Result<Map<String, Object>> getUserDiscoveryStats(@PathVariable Long userId) {
        return Result.success(discoveryService.getUserDiscoveryStats(userId));
    }
    
    /**
     * 获取附近热门探店地点
     */
    @GetMapping("/nearby-hot-spots")
    public Result<List<Map<String, Object>>> getNearbyHotSpots(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(discoveryService.getNearbyHotSpots(longitude, latitude, radius, limit));
    }
    
    /**
     * 生成智能探店路线
     */
    @PostMapping("/routes/generate")
    public Result<DiscoveryRoute> generateSmartRoute(
            @RequestParam Long userId,
            @RequestParam Double startLng,
            @RequestParam Double startLat,
            @RequestParam String sceneTag,
            @RequestParam(defaultValue = "5") Integer poiCount,
            @RequestParam(defaultValue = "10000") Double maxDistance) {
        return Result.success(discoveryService.generateSmartRoute(userId, startLng, startLat, 
                                                                  sceneTag, poiCount, maxDistance));
    }
    
    /**
     * 获取用户足迹地图
     */
    @GetMapping("/users/{userId}/footprint")
    public Result<List<Map<String, Object>>> getUserFootprintMap(
            @PathVariable Long userId,
            @RequestParam(required = false) String cityCode) {
        return Result.success(discoveryService.getUserFootprintMap(userId, cityCode));
    }
    
    /**
     * 获取探店达人列表
     */
    @GetMapping("/experts")
    public Result<List<Map<String, Object>>> getExpertUsers(
            @RequestParam String cityCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getExpertUsers(cityCode, pageNum, pageSize));
    }
    
    /**
     * 关注/取消关注探店达人
     */
    @PostMapping("/experts/{expertId}/follow")
    public Result<Boolean> followExpert(
            @RequestParam Long userId,
            @PathVariable Long expertId,
            @RequestParam Boolean follow) {
        return Result.success(discoveryService.followExpert(userId, expertId, follow));
    }
    
    /**
     * 获取关注达人的内容动态
     */
    @GetMapping("/users/{userId}/following-contents")
    public Result<List<DiscoveryContent>> getFollowingExpertContents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(discoveryService.getFollowingExpertContents(userId, pageNum, pageSize));
    }
}
