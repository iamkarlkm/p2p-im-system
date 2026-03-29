package com.im.backend.dto;

import com.im.backend.entity.PoiInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 位置推荐响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class LocationRecommendResponse {
    
    /**
     * 推荐结果列表
     */
    private List<RecommendItem> recommendations;
    
    /**
     * 推荐总数
     */
    private Integer totalCount;
    
    /**
     * 用户位置
     */
    private Double userLat;
    
    /**
     * 用户位置
     */
    private Double userLon;
    
    /**
     * 推荐场景
     */
    private String sceneTypeDesc;
    
    /**
     * 推荐耗时（毫秒）
     */
    private Long responseTime;
    
    /**
     * 推荐说明
     */
    private String recommendReason;
    
    /**
     * 个性化因子
     */
    private PersonalizationFactors factors;
    
    /**
     * 推荐项
     */
    @Data
    public static class RecommendItem {
        private PoiInfo poi;
        private Double score;
        private Double distance;
        private String reason;
        private List<String> matchTags;
    }
    
    /**
     * 个性化因子
     */
    @Data
    public static class PersonalizationFactors {
        private Double distanceWeight;
        private Double ratingWeight;
        private Double popularityWeight;
        private Double preferenceWeight;
        private Map<String, Double> categoryPreferences;
    }
    
    /**
     * 构建成功响应
     */
    public static LocationRecommendResponse success(List<RecommendItem> recommendations, LocationRecommendRequest request) {
        LocationRecommendResponse response = new LocationRecommendResponse();
        response.setRecommendations(recommendations);
        response.setTotalCount(recommendations.size());
        response.setUserLat(request.getLatitude());
        response.setUserLon(request.getLongitude());
        response.setSceneTypeDesc(getSceneTypeDesc(request.getSceneType()));
        return response;
    }
    
    private static String getSceneTypeDesc(Integer type) {
        return switch (type) {
            case 1 -> "餐饮推荐";
            case 2 -> "购物推荐";
            case 3 -> "娱乐推荐";
            case 4 -> "出行推荐";
            default -> "综合推荐";
        };
    }
}
