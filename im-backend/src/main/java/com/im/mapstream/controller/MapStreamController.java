package com.im.mapstream.controller;

import com.im.common.result.Result;
import com.im.mapstream.dto.*;
import com.im.mapstream.service.InfoClusterDTO;
import com.im.mapstream.service.MapSearchResultDTO;
import com.im.mapstream.service.MapStatsDTO;
import com.im.mapstream.service.MapStreamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 地图信息流控制器
 * 提供地图实时信息流聚合与热点发现API接口
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/v1/map")
@RequiredArgsConstructor
@Api(tags = "地图信息流", description = "地图实时信息流聚合与热点发现服务")
@Validated
public class MapStreamController {

    private final MapStreamService mapStreamService;

    /**
     * 发布地图信息流
     */
    @PostMapping("/stream/publish")
    @ApiOperation(value = "发布地图信息流", notes = "在地图上发布朋友圈/直播/短视频等内容")
    public Result<MapStreamResponse> publishStream(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody PublishStreamRequest request) {
        return Result.success(mapStreamService.publishStream(userId, request));
    }

    /**
     * 附近信息流查询
     */
    @GetMapping("/stream/nearby")
    @ApiOperation(value = "附近信息流查询", notes = "查询指定位置附近的信息流")
    public Result<List<MapStreamResponse>> queryNearbyStreams(@Valid StreamQueryRequest request) {
        return Result.success(mapStreamService.queryNearbyStreams(request));
    }

    /**
     * 聚合查询
     */
    @GetMapping("/stream/cluster")
    @ApiOperation(value = "聚合查询", notes = "根据地图缩放级别返回聚合后的信息流簇")
    public Result<List<InfoClusterDTO>> queryClusterStreams(@Valid ClusterQueryRequest request) {
        return Result.success(mapStreamService.queryClusterStreams(request));
    }

    /**
     * 实时热力图
     */
    @GetMapping("/heat/realtime")
    @ApiOperation(value = "实时热力图", notes = "获取指定区域的实时热力图数据")
    public Result<List<HeatMapResponse>> getRealtimeHeatMap(
            @ApiParam("最小经度") @RequestParam Double minLon,
            @ApiParam("最大经度") @RequestParam Double maxLon,
            @ApiParam("最小纬度") @RequestParam Double minLat,
            @ApiParam("最大纬度") @RequestParam Double maxLat,
            @ApiParam("缩放级别") @RequestParam Integer zoom) {
        return Result.success(mapStreamService.getRealtimeHeatMap(minLon, maxLon, minLat, maxLat, zoom));
    }

    /**
     * 历史热力图
     */
    @GetMapping("/heat/history")
    @ApiOperation(value = "历史热力图", notes = "获取指定时间段的历史热力图数据")
    public Result<List<HeatMapResponse>> getHistoryHeatMap(
            @ApiParam("最小经度") @RequestParam Double minLon,
            @ApiParam("最大经度") @RequestParam Double maxLon,
            @ApiParam("最小纬度") @RequestParam Double minLat,
            @ApiParam("最大纬度") @RequestParam Double maxLat,
            @ApiParam("缩放级别") @RequestParam Integer zoom,
            @ApiParam("小时前") @RequestParam(defaultValue = "24") Integer hoursAgo) {
        return Result.success(mapStreamService.getHistoryHeatMap(minLon, maxLon, minLat, maxLat, zoom, hoursAgo));
    }

    /**
     * 热点列表
     */
    @GetMapping("/hotspot/list")
    @ApiOperation(value = "热点列表", notes = "获取当前热门地点列表")
    public Result<List<HotSpotResponse>> getHotSpots(
            @ApiParam("城市编码") @RequestParam(required = false) String cityCode,
            @ApiParam("数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(mapStreamService.getHotSpots(cityCode, limit));
    }

    /**
     * 创建热点
     */
    @PostMapping("/hotspot/create")
    @ApiOperation(value = "创建热点", notes = "手动创建地图热点")
    public Result<HotSpotResponse> createHotSpot(
            @ApiParam("热点名称") @RequestParam String name,
            @ApiParam("热点描述") @RequestParam(required = false) String description,
            @ApiParam("经度") @RequestParam Double longitude,
            @ApiParam("纬度") @RequestParam Double latitude,
            @ApiParam("覆盖半径(米)") @RequestParam(defaultValue = "1000") Double radius) {
        return Result.success(mapStreamService.createHotSpot(name, description, longitude, latitude, radius));
    }

    /**
     * 直播流地图
     */
    @GetMapping("/stream/live")
    @ApiOperation(value = "直播流地图", notes = "获取地图上的直播流")
    public Result<List<MapStreamResponse>> getLiveStreams(@Valid StreamQueryRequest request) {
        return Result.success(mapStreamService.getLiveStreams(request));
    }

    /**
     * 短视频地图
     */
    @GetMapping("/stream/video")
    @ApiOperation(value = "短视频地图", notes = "获取地图上的短视频")
    public Result<List<MapStreamResponse>> getVideoStreams(@Valid StreamQueryRequest request) {
        return Result.success(mapStreamService.getVideoStreams(request));
    }

    /**
     * 朋友圈地图
     */
    @GetMapping("/stream/friends")
    @ApiOperation(value = "朋友圈地图", notes = "获取好友在地图上的动态")
    public Result<List<MapStreamResponse>> getFriendStreams(
            @RequestAttribute("userId") Long userId,
            @Valid StreamQueryRequest request) {
        return Result.success(mapStreamService.getFriendStreams(userId, request));
    }

    /**
     * 地图综合搜索
     */
    @GetMapping("/search")
    @ApiOperation(value = "地图综合搜索", notes = "按关键字搜索地图上的内容")
    public Result<MapSearchResultDTO> searchMap(
            @ApiParam("搜索关键字") @RequestParam String keyword,
            @ApiParam("经度") @RequestParam Double longitude,
            @ApiParam("纬度") @RequestParam Double latitude,
            @ApiParam("搜索半径") @RequestParam(defaultValue = "5000") Integer radius) {
        return Result.success(mapStreamService.searchMap(keyword, longitude, latitude, radius));
    }

    /**
     * 地图数据统计
     */
    @GetMapping("/stats")
    @ApiOperation(value = "地图数据统计", notes = "获取地图相关统计数据")
    public Result<MapStatsDTO> getMapStats(
            @ApiParam("城市编码") @RequestParam(required = false) String cityCode,
            @ApiParam("统计天数") @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(mapStreamService.getMapStats(cityCode, days));
    }

    /**
     * 更新信息流热度
     */
    @PostMapping("/stream/{streamId}/update-heat")
    @ApiOperation(value = "更新信息流热度", notes = "手动触发热度计算更新")
    public Result<Void> updateStreamHeat(
            @ApiParam("信息流ID") @PathVariable String streamId) {
        mapStreamService.updateStreamHeat(streamId);
        return Result.success();
    }

    /**
     * 删除信息流
     */
    @DeleteMapping("/stream/{streamId}")
    @ApiOperation(value = "删除信息流", notes = "删除指定的地图信息流")
    public Result<Void> deleteStream(
            @RequestAttribute("userId") Long userId,
            @ApiParam("信息流ID") @PathVariable String streamId) {
        mapStreamService.deleteStream(streamId, userId);
        return Result.success();
    }
}
