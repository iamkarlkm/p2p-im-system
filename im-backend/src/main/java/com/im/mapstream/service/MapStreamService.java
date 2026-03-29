package com.im.mapstream.service;

import com.im.mapstream.dto.*;
import java.util.List;

/**
 * 地图信息流服务接口
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
public interface MapStreamService {
    
    /**
     * 发布地图信息流
     */
    MapStreamResponse publishStream(Long userId, PublishStreamRequest request);
    
    /**
     * 查询附近信息流
     */
    List<MapStreamResponse> queryNearbyStreams(StreamQueryRequest request);
    
    /**
     * 查询聚合信息
     */
    List<InfoClusterDTO> queryClusterStreams(ClusterQueryRequest request);
    
    /**
     * 获取实时热力图数据
     */
    List<HeatMapResponse> getRealtimeHeatMap(Double minLon, Double maxLon, Double minLat, Double maxLat, Integer zoom);
    
    /**
     * 获取历史热力图数据
     */
    List<HeatMapResponse> getHistoryHeatMap(Double minLon, Double maxLon, Double minLat, Double maxLat, Integer zoom, Integer hoursAgo);
    
    /**
     * 获取热点列表
     */
    List<HotSpotResponse> getHotSpots(String cityCode, Integer limit);
    
    /**
     * 创建热点
     */
    HotSpotResponse createHotSpot(String name, String description, Double longitude, Double latitude, Double radius);
    
    /**
     * 获取直播流地图数据
     */
    List<MapStreamResponse> getLiveStreams(StreamQueryRequest request);
    
    /**
     * 获取短视频地图数据
     */
    List<MapStreamResponse> getVideoStreams(StreamQueryRequest request);
    
    /**
     * 获取朋友圈地图数据
     */
    List<MapStreamResponse> getFriendStreams(Long userId, StreamQueryRequest request);
    
    /**
     * 地图综合搜索
     */
    MapSearchResultDTO searchMap(String keyword, Double longitude, Double latitude, Integer radius);
    
    /**
     * 获取地图统计数据
     */
    MapStatsDTO getMapStats(String cityCode, Integer days);
    
    /**
     * 更新信息流热度
     */
    void updateStreamHeat(String streamId);
    
    /**
     * 删除信息流
     */
    void deleteStream(String streamId, Long operatorId);
}

/**
 * 聚合信息DTO
 */
class InfoClusterDTO {
    private String clusterId;
    private Double centerLongitude;
    private Double centerLatitude;
    private Integer streamCount;
    private List<MapStreamResponse> previewStreams;
    private Double clusterHeat;
    public String getClusterId() { return clusterId; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    public Double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }
    public Double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }
    public Integer getStreamCount() { return streamCount; }
    public void setStreamCount(Integer streamCount) { this.streamCount = streamCount; }
    public List<MapStreamResponse> getPreviewStreams() { return previewStreams; }
    public void setPreviewStreams(List<MapStreamResponse> previewStreams) { this.previewStreams = previewStreams; }
    public Double getClusterHeat() { return clusterHeat; }
    public void setClusterHeat(Double clusterHeat) { this.clusterHeat = clusterHeat; }
}

/**
 * 地图搜索结果DTO
 */
class MapSearchResultDTO {
    private List<MapStreamResponse> streams;
    private List<HotSpotResponse> hotSpots;
    private Integer totalCount;
    public List<MapStreamResponse> getStreams() { return streams; }
    public void setStreams(List<MapStreamResponse> streams) { this.streams = streams; }
    public List<HotSpotResponse> getHotSpots() { return hotSpots; }
    public void setHotSpots(List<HotSpotResponse> hotSpots) { this.hotSpots = hotSpots; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
}

/**
 * 地图统计DTO
 */
class MapStatsDTO {
    private Long totalStreams;
    private Long todayStreams;
    private Long activeUsers;
    private Integer hotSpotCount;
    private List<DailyStatDTO> dailyStats;
    public Long getTotalStreams() { return totalStreams; }
    public void setTotalStreams(Long totalStreams) { this.totalStreams = totalStreams; }
    public Long getTodayStreams() { return todayStreams; }
    public void setTodayStreams(Long todayStreams) { this.todayStreams = todayStreams; }
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    public Integer getHotSpotCount() { return hotSpotCount; }
    public void setHotSpotCount(Integer hotSpotCount) { this.hotSpotCount = hotSpotCount; }
    public List<DailyStatDTO> getDailyStats() { return dailyStats; }
    public void setDailyStats(List<DailyStatDTO> dailyStats) { this.dailyStats = dailyStats; }
}

/**
 * 每日统计DTO
 */
class DailyStatDTO {
    private String date;
    private Long streamCount;
    private Long viewCount;
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getStreamCount() { return streamCount; }
    public void setStreamCount(Long streamCount) { this.streamCount = streamCount; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
}
