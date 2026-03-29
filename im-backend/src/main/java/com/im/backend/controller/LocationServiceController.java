package com.im.backend.controller;

import com.im.backend.common.Result;
import com.im.backend.dto.*;
import com.im.backend.entity.GeoHashGrid;
import com.im.backend.entity.LocationHeatmap;
import com.im.backend.entity.LocationPoint;
import com.im.backend.service.IGeoHashService;
import com.im.backend.service.ILocationService;
import com.im.backend.service.IPoiRecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 位置服务控制器
 * 提供GeoHash索引、位置管理和POI推荐等REST API
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
@Validated
public class LocationServiceController {
    
    private final IGeoHashService geoHashService;
    private final ILocationService locationService;
    private final IPoiRecommendService poiRecommendService;
    
    // ==================== GeoHash编码解码 ====================
    
    /**
     * 编码经纬度为GeoHash
     */
    @GetMapping("/geohash/encode")
    public Result<String> encodeGeohash(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "9") Integer precision) {
        String geohash = geoHashService.encode(latitude, longitude, precision);
        return Result.success(geohash);
    }
    
    /**
     * 解码GeoHash为坐标
     */
    @GetMapping("/geohash/decode/{geohash}")
    public Result<Map<String, Double>> decodeGeohash(@PathVariable String geohash) {
        double[] coords = geoHashService.decode(geohash);
        return Result.success(Map.of("latitude", coords[0], "longitude", coords[1]));
    }
    
    /**
     * 获取邻居网格
     */
    @GetMapping("/geohash/neighbors/{geohash}")
    public Result<Map<String, String>> getNeighbors(@PathVariable String geohash) {
        String[] neighbors = geoHashService.getNeighbors(geohash);
        return Result.success(Map.of(
                "n", neighbors[0], "ne", neighbors[1],
                "e", neighbors[2], "se", neighbors[3],
                "s", neighbors[4], "sw", neighbors[5],
                "w", neighbors[6], "nw", neighbors[7]
        ));
    }
    
    /**
     * 计算两点距离
     */
    @GetMapping("/geohash/distance")
    public Result<Double> calculateDistance(
            @RequestParam String geohash1,
            @RequestParam String geohash2) {
        double distance = geoHashService.calculateDistance(geohash1, geohash2);
        return Result.success(distance);
    }
    
    // ==================== 网格管理 ====================
    
    /**
     * 查询网格信息
     */
    @PostMapping("/grid/query")
    public Result<List<GeoHashGrid>> queryGrids(@RequestBody @Validated GeoHashQueryRequest request) {
        List<GeoHashGrid> grids = geoHashService.queryGrids(request);
        return Result.success(grids);
    }
    
    /**
     * 获取网格详情
     */
    @GetMapping("/grid/{geohash}")
    public Result<GeoHashGrid> getGridInfo(@PathVariable String geohash) {
        GeoHashGrid grid = geoHashService.getGridInfo(geohash);
        return Result.success(grid);
    }
    
    /**
     * 获取热门网格
     */
    @GetMapping("/grid/hot")
    public Result<List<GeoHashGrid>> getHotGrids(
            @RequestParam(defaultValue = "10") Integer topN) {
        List<GeoHashGrid> grids = geoHashService.getHotGrids(topN);
        return Result.success(grids);
    }
    
    /**
     * 获取网格统计
     */
    @GetMapping("/grid/stats")
    public Result<Map<String, Object>> getGridStats() {
        Map<String, Object> stats = geoHashService.getGridStatistics();
        return Result.success(stats);
    }
    
    /**
     * 获取热力图数据
     */
    @GetMapping("/grid/heatmap/{geohash}")
    public Result<List<LocationHeatmap>> getHeatmapData(
            @PathVariable String geohash,
            @RequestParam(defaultValue = "7") Integer precision) {
        List<LocationHeatmap> heatmap = geoHashService.getHeatmapData(geohash, precision);
        return Result.success(heatmap);
    }
    
    // ==================== 位置管理 ====================
    
    /**
     * 上报位置
     */
    @PostMapping("/report")
    public Result<LocationPoint> reportLocation(@RequestBody @Validated LocationPoint location) {
        LocationPoint saved = locationService.reportLocation(location);
        return Result.success(saved);
    }
    
    /**
     * 获取用户最新位置
     */
    @GetMapping("/user/{userId}/latest")
    public Result<LocationPoint> getUserLatestLocation(@PathVariable Long userId) {
        LocationPoint location = locationService.getUserLatestLocation(userId);
        return Result.success(location);
    }
    
    /**
     * 附近查询
     */
    @GetMapping("/nearby")
    public Result<List<LocationPoint>> nearbySearch(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "1000") Double radius) {
        List<LocationPoint> points = locationService.nearbySearch(latitude, longitude, radius);
        return Result.success(points);
    }
    
    /**
     * 矩形范围查询
     */
    @GetMapping("/boundingbox")
    public Result<List<LocationPoint>> boundingBoxSearch(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        List<LocationPoint> points = locationService.boundingBoxSearch(minLat, maxLat, minLon, maxLon);
        return Result.success(points);
    }
    
    /**
     * 获取位置分布统计
     */
    @GetMapping("/distribution/{geohash}")
    public Result<Map<String, Object>> getLocationDistribution(@PathVariable String geohash) {
        Map<String, Object> distribution = locationService.getLocationDistribution(geohash);
        return Result.success(distribution);
    }
    
    // ==================== POI推荐 ====================
    
    /**
     * 获取附近POI
     */
    @PostMapping("/poi/nearby")
    public Result<NearbyPoiResponse> getNearbyPois(@RequestBody @Validated NearbyPoiRequest request) {
        NearbyPoiResponse response = poiRecommendService.getNearbyPois(request);
        return Result.success(response);
    }
    
    /**
     * 获取个性化推荐
     */
    @PostMapping("/poi/recommend")
    public Result<LocationRecommendResponse> getRecommendations(
            @RequestBody @Validated LocationRecommendRequest request) {
        LocationRecommendResponse response = poiRecommendService.getPersonalizedRecommendations(request);
        return Result.success(response);
    }
    
    /**
     * 获取热门POI
     */
    @GetMapping("/poi/hot")
    public Result<NearbyPoiResponse> getHotPois(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Integer radius,
            @RequestParam(defaultValue = "10") Integer limit) {
        NearbyPoiResponse response = poiRecommendService.getHotPois(latitude, longitude, radius, limit);
        return Result.success(response);
    }
    
    /**
     * 猜你喜欢
     */
    @GetMapping("/poi/guess/{userId}")
    public Result<LocationRecommendResponse> guessYouLike(
            @PathVariable Long userId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        LocationRecommendResponse response = poiRecommendService.getGuessYouLike(userId, latitude, longitude);
        return Result.success(response);
    }
    
    /**
     * 刷新推荐缓存
     */
    @PostMapping("/poi/refresh/{userId}")
    public Result<Void> refreshRecommendCache(@PathVariable Long userId) {
        poiRecommendService.refreshRecommendCache(userId);
        return Result.success();
    }
}
