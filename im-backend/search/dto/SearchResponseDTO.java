package com.im.search.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 搜索结果响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDTO {

    /** 总记录数 */
    private Long total;

    /** 当前页 */
    private Integer page;

    /** 每页大小 */
    private Integer size;

    /** 总页数 */
    private Integer totalPages;

    /** POI结果列表 */
    private List<PoiSearchResultDTO> results;

    /** 搜索意图 */
    private SearchIntentDTO intent;

    /** 搜索耗时（毫秒） */
    private Long took;

    /** 查询纠错 */
    private String correctedQuery;

    /** 是否需要纠错 */
    private Boolean hasCorrection;

    /** 搜索建议 */
    private List<String> suggestions;

    /** 热门搜索 */
    private List<String> hotSearches;

    /** 相关分类 */
    private List<CategorySuggestionDTO> relatedCategories;

    /** 区域筛选 */
    private List<AreaFilterDTO> areaFilters;

    /** 是否有下一页 */
    private Boolean hasNext;

    /** 聚合统计 */
    private SearchAggregationDTO aggregation;

    /**
     * 分类建议DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySuggestionDTO {
        private String code;
        private String name;
        private String icon;
        private Long count;
    }

    /**
     * 区域筛选DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AreaFilterDTO {
        private String name;
        private String code;
        private Long count;
        private List<AreaFilterDTO> children;
    }

    /**
     * 搜索聚合统计DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchAggregationDTO {
        /** 平均评分 */
        private Float avgRating;
        /** 平均价格 */
        private Integer avgPrice;
        /** 分类分布 */
        private List<CategoryCountDTO> categoryDistribution;
        /** 价格区间分布 */
        private List<PriceRangeDTO> priceDistribution;
        /** 标签分布 */
        private List<TagCountDTO> tagDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCountDTO {
        private String category;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRangeDTO {
        private String range;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagCountDTO {
        private String tag;
        private Long count;
    }
}
