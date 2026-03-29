package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索建议结果DTO
 */
@Data
public class SearchSuggestionResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 输入关键词
     */
    private String keyword;

    /**
     * 搜索建议列表
     */
    private List<SuggestionItemDTO> suggestions;

    /**
     * 历史搜索记录
     */
    private List<String> historySearches;

    /**
     * 热门搜索
     */
    private List<HotSearchDTO> hotSearches;

    /**
     * 搜索发现
     */
    private List<String> discoveryKeywords;

    /**
     * 建议项DTO
     */
    @Data
    public static class SuggestionItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 建议类型: COMPLETION-补全, HISTORY-历史, HOT-热门, POI-商户, CATEGORY-类别
         */
        private String type;

        /**
         * 建议文本
         */
        private String text;

        /**
         * 高亮文本（HTML格式）
         */
        private String highlightedText;

        /**
         * 关联POI ID（如果是商户建议）
         */
        private Long poiId;

        /**
         * 关联POI名称
         */
        private String poiName;

        /**
         * 类别名称（如果是类别建议）
         */
        private String categoryName;

        /**
         * 图标URL
         */
        private String icon;

        /**
         * 距离描述
         */
        private String distance;

        /**
         * 评分
         */
        private Double rating;
    }

    /**
     * 热门搜索DTO
     */
    @Data
    public static class HotSearchDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 排名
         */
        private Integer rank;

        /**
         * 搜索词
         */
        private String keyword;

        /**
         * 热度值
         */
        private Integer heat;

        /**
         * 变化趋势: UP-上升, DOWN-下降, FLAT-持平, NEW-新上榜
         */
        private String trend;
    }
}
