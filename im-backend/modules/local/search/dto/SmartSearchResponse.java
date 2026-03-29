package com.im.backend.modules.local.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 智能搜索响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能搜索响应")
public class SmartSearchResponse {
    
    @Schema(description = "搜索查询ID")
    private Long queryId;
    
    @Schema(description = "原始查询")
    private String originalQuery;
    
    @Schema(description = "标准化查询")
    private String normalizedQuery;
    
    @Schema(description = "是否进行了查询纠错")
    private Boolean isCorrected;
    
    @Schema(description = "纠错前的查询")
    private String correctedFrom;
    
    @Schema(description = "搜索意图")
    private SearchIntentDTO intent;
    
    @Schema(description = "搜索结果列表")
    private List<SearchResultItemDTO> results;
    
    @Schema(description = "联想推荐")
    private List<String> suggestions;
    
    @Schema(description = "相关搜索")
    private List<String> relatedQueries;
    
    @Schema(description = "知识图谱推荐")
    private List<KnowledgeGraphRecommendationDTO> kgRecommendations;
    
    @Schema(description = "总数")
    private Long total;
    
    @Schema(description = "页码")
    private Integer pageNum;
    
    @Schema(description = "每页数量")
    private Integer pageSize;
    
    @Schema(description = "总页数")
    private Integer totalPages;
    
    @Schema(description = "响应时间(毫秒)")
    private Long responseTime;
    
    @Schema(description = "是否零结果")
    private Boolean isZeroResult;
    
    @Schema(description = "零结果时的推荐提示")
    private String zeroResultTip;
    
    // ==================== 内部DTO ====================
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "搜索意图信息")
    public static class SearchIntentDTO {
        @Schema(description = "主意图类型")
        private String primaryIntent;
        
        @Schema(description = "意图置信度")
        private Double confidence;
        
        @Schema(description = "意图描述")
        private String intentDescription;
        
        @Schema(description = "提取的实体")
        private List<String> entities;
        
        @Schema(description = "时间约束")
        private String timeConstraint;
        
        @Schema(description = "价格约束")
        private String priceConstraint;
        
        @Schema(description = "场景标签")
        private String sceneTag;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "搜索结果项")
    public static class SearchResultItemDTO {
        @Schema(description = "POI ID")
        private Long poiId;
        
        @Schema(description = "POI名称")
        private String name;
        
        @Schema(description = "POI类型")
        private String type;
        
        @Schema(description = "分类标签")
        private List<String> categories;
        
        @Schema(description = "评分")
        private Double rating;
        
        @Schema(description = "人均消费")
        private Double avgPrice;
        
        @Schema(description = "距离（米）")
        private Integer distance;
        
        @Schema(description = "距离描述")
        private String distanceText;
        
        @Schema(description = "地址")
        private String address;
        
        @Schema(description = "经度")
        private Double longitude;
        
        @Schema(description = "纬度")
        private Double latitude;
        
        @Schema(description = "主图URL")
        private String mainImage;
        
        @Schema(description = "图片列表")
        private List<String> images;
        
        @Schema(description = "营业状态")
        private String businessStatus;
        
        @Schema(description = "营业时间")
        private String businessHours;
        
        @Schema(description = "特色标签")
        private List<String> features;
        
        @Schema(description = "促销活动")
        private List<PromotionDTO> promotions;
        
        @Schema(description = "推荐语")
        private String recommendationReason;
        
        @Schema(description = "搜索匹配分数")
        private Double matchScore;
        
        @Schema(description = "热度指数")
        private Integer heatScore;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "促销信息")
    public static class PromotionDTO {
        @Schema(description = "促销类型")
        private String type;
        
        @Schema(description = "促销标题")
        private String title;
        
        @Schema(description = "促销内容")
        private String content;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "知识图谱推荐")
    public static class KnowledgeGraphRecommendationDTO {
        @Schema(description = "推荐类型: SIMILAR-相似, COMPLEMENTARY-互补, COMPETITOR-竞品, RELATED-相关")
        private String type;
        
        @Schema(description = "推荐标题")
        private String title;
        
        @Schema(description = "推荐POI列表")
        private List<SimplePoiDTO> pois;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "简化的POI信息")
    public static class SimplePoiDTO {
        @Schema(description = "POI ID")
        private Long poiId;
        
        @Schema(description = "POI名称")
        private String name;
        
        @Schema(description = "评分")
        private Double rating;
        
        @Schema(description = "距离描述")
        private String distanceText;
    }
}
