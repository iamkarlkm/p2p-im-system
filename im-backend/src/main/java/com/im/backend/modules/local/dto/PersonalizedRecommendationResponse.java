package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 个性化推荐响应DTO
 * 包含多路召回结果和智能排序
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "个性化推荐响应")
public class PersonalizedRecommendationResponse {
    
    @Schema(description = "推荐ID", example = "rec_1234567890")
    private String recommendationId;
    
    @Schema(description = "推荐列表")
    private List<RecommendationItem> items;
    
    @Schema(description = "召回统计")
    private RecallStatistics recallStats;
    
    @Schema(description = "推荐解释")
    private RecommendationExplanation explanation;
    
    @Schema(description = "是否有更多", example = "true")
    private Boolean hasMore;
    
    @Schema(description = "推荐耗时（毫秒）", example = "89")
    private Long recommendTimeMs;
    
    @Schema(description = "刷新时间戳", example = "1711632000000")
    private Long timestamp;
    
    /**
     * 推荐项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "推荐项")
    public static class RecommendationItem {
        @Schema(description = "内容ID", example = "poi_123456")
        private String itemId;
        
        @Schema(description = "内容类型：poi/activity/coupon", example = "poi")
        private String contentType;
        
        @Schema(description = "排序分数", example = "0.92")
        private Double score;
        
        @Schema(description = "召回来源：geo/hot/cf/vector/mixed", example = "geo")
        private String recallSource;
        
        @Schema(description = "推荐原因", example = "距离您仅500米")
        private String recommendReason;
        
        @Schema(description = "POI数据（当contentType=poi时）")
        private POIData poiData;
        
        @Schema(description = "活动数据（当contentType=activity时）")
        private ActivityData activityData;
        
        @Schema(description = "优惠券数据（当contentType=coupon时）")
        private CouponData couponData;
        
        @Schema(description = "特征分数详情")
        private FeatureScores featureScores;
    }
    
    /**
     * POI数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "POI数据")
    public static class POIData {
        @Schema(description = "POI ID", example = "poi_123456")
        private String poiId;
        
        @Schema(description = "名称", example = "海底捞火锅")
        private String name;
        
        @Schema(description = "地址", example = "上海市浦东新区陆家嘴环路1000号")
        private String address;
        
        @Schema(description = "纬度", example = "31.230416")
        private Double latitude;
        
        @Schema(description = "经度", example = "121.473701")
        private Double longitude;
        
        @Schema(description = "距离（米）", example = "500")
        private Integer distance;
        
        @Schema(description = "评分", example = "4.8")
        private Double rating;
        
        @Schema(description = "人均消费", example = "150")
        private Integer avgPrice;
        
        @Schema(description = "分类", example = "火锅")
        private String category;
        
        @Schema(description = "图片", example = "https://example.com/img.jpg")
        private String image;
        
        @Schema(description = "标签", example = "[\"网红店\", \"服务热情\"]")
        private List<String> tags;
        
        @Schema(description = "是否营业中", example = "true")
        private Boolean isOpen;
        
        @Schema(description = "优惠信息", example = "满200减50")
        private String promotion;
    }
    
    /**
     * 活动数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "活动数据")
    public static class ActivityData {
        @Schema(description = "活动ID", example = "act_123456")
        private String activityId;
        
        @Schema(description = "活动名称", example = "周末美食节")
        private String name;
        
        @Schema(description = "活动类型", example = "food_festival")
        private String type;
        
        @Schema(description = "开始时间", example = "1711632000000")
        private Long startTime;
        
        @Schema(description = "结束时间", example = "1712236800000")
        private Long endTime;
        
        @Schema(description = "活动地点", example = "世纪公园")
        private String location;
        
        @Schema(description = "参与人数", example = "1234")
        private Integer participantCount;
        
        @Schema(description = "活动图片", example = "https://example.com/act.jpg")
        private String image;
    }
    
    /**
     * 优惠券数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "优惠券数据")
    public static class CouponData {
        @Schema(description = "优惠券ID", example = "coupon_123456")
        private String couponId;
        
        @Schema(description = "优惠券名称", example = "满100减20")
        private String name;
        
        @Schema(description = "折扣类型：amount/percent", example = "amount")
        private String discountType;
        
        @Schema(description = "折扣值", example = "20")
        private Double discountValue;
        
        @Schema(description = "最低消费", example = "100")
        private Double minSpend;
        
        @Schema(description = "适用商户", example = "海底捞火锅")
        private String applicableMerchant;
        
        @Schema(description = "有效期", example = "2026-04-30")
        private String validUntil;
    }
    
    /**
     * 特征分数
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "特征分数")
    public static class FeatureScores {
        @Schema(description = "个性化分数", example = "0.85")
        private Double personalizationScore;
        
        @Schema(description = "距离分数", example = "0.92")
        private Double distanceScore;
        
        @Schema(description = "热度分数", example = "0.78")
        private Double popularityScore;
        
        @Schema(description = "质量分数", example = "0.88")
        private Double qualityScore;
        
        @Schema(description = "时效分数", example = "0.95")
        private Double freshnessScore;
    }
    
    /**
     * 召回统计
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "召回统计")
    public static class RecallStatistics {
        @Schema(description = "Geo召回数量", example = "50")
        private Integer geoRecallCount;
        
        @Schema(description = "热门召回数量", example = "30")
        private Integer hotRecallCount;
        
        @Schema(description = "协同过滤召回数量", example = "40")
        private Integer cfRecallCount;
        
        @Schema(description = "向量召回数量", example = "30")
        private Integer vectorRecallCount;
        
        @Schema(description = "去重前总数", example = "150")
        private Integer totalBeforeDeduplication;
        
        @Schema(description = "去重后总数", example = "120")
        private Integer totalAfterDeduplication;
    }
    
    /**
     * 推荐解释
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "推荐解释")
    public static class RecommendationExplanation {
        @Schema(description = "主要推荐理由", example = "基于您的位置和偏好推荐")
        private String mainReason;
        
        @Schema(description = "多样性说明", example = "包含3种不同类型推荐")
        private String diversityNote;
        
        @Schema(description = "实时性说明", example = "基于30分钟内最新数据")
        private String freshnessNote;
    }
}
