package com.im.backend.dto;

import com.im.backend.entity.PoiInfo;
import lombok.Data;

import java.util.List;

/**
 * 附近POI查询响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class NearbyPoiResponse {
    
    /**
     * 查询结果列表
     */
    private List<PoiInfo> poiList;
    
    /**
     * 总数
     */
    private Long total;
    
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 查询中心纬度
     */
    private Double centerLat;
    
    /**
     * 查询中心经度
     */
    private Double centerLon;
    
    /**
     * 查询半径（米）
     */
    private Integer radius;
    
    /**
     * 响应耗时（毫秒）
     */
    private Long responseTime;
    
    /**
     * 是否使用缓存
     */
    private Boolean fromCache;
    
    /**
     * 搜索结果说明
     */
    private String message;
    
    /**
     * 分类统计
     */
    private List<CategoryCount> categoryStats;
    
    /**
     * 热度分布
     */
    private HeatmapDistribution heatmap;
    
    /**
     * 分类统计内部类
     */
    @Data
    public static class CategoryCount {
        private String categoryCode;
        private String categoryName;
        private Long count;
    }
    
    /**
     * 热度分布内部类
     */
    @Data
    public static class HeatmapDistribution {
        private Double maxLat;
        private Double minLat;
        private Double maxLon;
        private Double minLon;
        private List<HeatPoint> heatPoints;
    }
    
    /**
     * 热力点
     */
    @Data
    public static class HeatPoint {
        private Double lat;
        private Double lon;
        private Long heat;
    }
    
    /**
     * 构建成功响应
     */
    public static NearbyPoiResponse success(List<PoiInfo> poiList, long total, NearbyPoiRequest request) {
        NearbyPoiResponse response = new NearbyPoiResponse();
        response.setPoiList(poiList);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        response.setTotalPages((int) Math.ceil((double) total / request.getPageSize()));
        response.setCenterLat(request.getLatitude());
        response.setCenterLon(request.getLongitude());
        response.setRadius(request.getRadius());
        response.setMessage("查询成功");
        return response;
    }
    
    /**
     * 构建空响应
     */
    public static NearbyPoiResponse empty(NearbyPoiRequest request) {
        NearbyPoiResponse response = new NearbyPoiResponse();
        response.setPoiList(List.of());
        response.setTotal(0L);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        response.setTotalPages(0);
        response.setCenterLat(request.getLatitude());
        response.setCenterLon(request.getLongitude());
        response.setRadius(request.getRadius());
        response.setMessage("未找到相关POI");
        return response;
    }
}
