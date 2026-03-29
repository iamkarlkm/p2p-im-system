package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * POI语义搜索响应DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "POI语义搜索响应")
public class POISemanticSearchResponse {
    
    @Schema(description = "搜索ID", example = "search_1234567890")
    private String searchId;
    
    @Schema(description = "总结果数", example = "156")
    private Integer totalCount;
    
    @Schema(description = "当前页", example = "1")
    private Integer currentPage;
    
    @Schema(description = "总页数", example = "8")
    private Integer totalPages;
    
    @Schema(description = "语义解析结果")
    private SemanticParseResult semanticParse;
    
    @Schema(description = "POI结果列表")
    private List<POIResultItem> results;
    
    @Schema(description = "搜索结果聚合")
    private SearchAggregation aggregation;
    
    @Schema(description = "相关搜索建议")
    private List<String> relatedSearches;
    
    @Schema(description = "搜索耗时（毫秒）", example = "128")
    private Long searchTimeMs;
    
    @Schema(description = "是否零结果", example = "false")
    private Boolean zeroResult;
    
    @Schema(description = "纠错建议", example = "您是否想搜索：火锅")
    private String correctionSuggestion;
    
    /**
     * 语义解析结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "语义解析结果")
    public static class SemanticParseResult {
        @Schema(description = "原始查询", example = "附近适合约会的西餐厅")
        private String originalQuery;
        
        @Schema(description = "标准化查询", example = "西餐 约会 附近")
        private String normalizedQuery;
        
        @Schema(description = "搜索意图", example = "DISCOVER")
        private String intent;
        
        @Schema(description = "意图置信度", example = "0.89")
        private Double intentConfidence;
        
        @Schema(description = "提取的实体列表")
        private List<Entity> entities;
        
        @Schema(description = "情感倾向：positive/neutral/negative", example = "positive")
        private String sentiment;
    }
    
    /**
     * 实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "实体")
    public static class Entity {
        @Schema(description = "实体类型", example = "CATEGORY")
        private String type;
        
        @Schema(description = "实体值", example = "西餐")
        private String value;
        
        @Schema(description = "同义词", example = "[\"西餐厅\", \"Western\"]")
        private List<String> synonyms;
    }
    
    /**
     * POI结果项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "POI结果项")
    public static class POIResultItem {
        @Schema(description = "POI ID", example = "poi_123456")
        private String poiId;
        
        @Schema(description = "名称", example = "莫尔顿牛排坊")
        private String name;
        
        @Schema(description = "地址", example = "上海市黄浦区中山东一路18号5楼")
        private String address;
        
        @Schema(description = "经度", example = "121.485291")
        private Double longitude;
        
        @Schema(description = "纬度", example = "31.235745")
        private Double latitude;
        
        @Schema(description = "距离（米）", example = "850")
        private Integer distance;
        
        @Schema(description = "评分", example = "4.6")
        private Double rating;
        
        @Schema(description = "评价数量", example = "3241")
        private Integer reviewCount;
        
        @Schema(description = "人均消费", example = "680")
        private Integer avgPrice;
        
        @Schema(description = "一级分类", example = "美食")
        private String category;
        
        @Schema(description = "二级分类", example = "西餐")
        private String subCategory;
        
        @Schema(description = "电话", example = "021-63339788")
        private String phone;
        
        @Schema(description = "营业时间", example = "11:30-22:30")
        private String businessHours;
        
        @Schema(description = "是否营业中", example = "true")
        private Boolean isOpen;
        
        @Schema(description = "图片列表")
        private List<String> photos;
        
        @Schema(description = "标签列表", example = "[\"江景\", \"约会圣地\", \"求婚热门\"]")
        private List<String> tags;
        
        @Schema(description = "特色服务", example = "[\"包间\", \"景观位\", \"代客泊车\"]")
        private List<String> services;
        
        @Schema(description = "匹配分数", example = "0.92")
        private Double matchScore;
        
        @Schema(description = "推荐理由", example = "高分江景西餐厅，适合约会")
        private String recommendReason;
        
        @Schema(description = "用户交互数据")
        private UserInteraction userInteraction;
    }
    
    /**
     * 用户交互数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户交互数据")
    public static class UserInteraction {
        @Schema(description = "是否收藏", example = "false")
        private Boolean isFavorited;
        
        @Schema(description = "是否去过", example = "false")
        private Boolean isVisited;
        
        @Schema(description = "好友推荐数", example = "3")
        private Integer friendRecommendCount;
    }
    
    /**
     * 搜索结果聚合
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "搜索结果聚合")
    public static class SearchAggregation {
        @Schema(description = "分类聚合")
        private List<CategoryBucket> categories;
        
        @Schema(description = "区域聚合")
        private List<AreaBucket> areas;
        
        @Schema(description = "价格区间聚合")
        private List<PriceBucket> priceRanges;
        
        @Schema(description = "标签聚合")
        private List<TagBucket> tags;
    }
    
    /**
     * 分类桶
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类桶")
    public static class CategoryBucket {
        @Schema(description = "分类名称", example = "西餐")
        private String name;
        
        @Schema(description = "数量", example = "45")
        private Integer count;
    }
    
    /**
     * 区域桶
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "区域桶")
    public static class AreaBucket {
        @Schema(description = "区域名称", example = "黄浦区")
        private String name;
        
        @Schema(description = "数量", example = "23")
        private Integer count;
    }
    
    /**
     * 价格桶
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "价格桶")
    public static class PriceBucket {
        @Schema(description = "价格区间", example = "200-300")
        private String range;
        
        @Schema(description = "数量", example = "18")
        private Integer count;
    }
    
    /**
     * 标签桶
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "标签桶")
    public static class TagBucket {
        @Schema(description = "标签", example = "约会圣地")
        private String tag;
        
        @Schema(description = "数量", example = "12")
        private Integer count;
    }
}
