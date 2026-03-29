package com.im.backend.modules.local.search.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 搜索结果响应DTO
 */
@Data
public class SemanticSearchResponse {

    /**
     * 搜索意图类型
     */
    private String intentType;

    /**
     * 意图置信度
     */
    private Integer intentConfidence;

    /**
     * 识别的话术建议
     */
    private String suggestedReply;

    /**
     * 是否是多轮对话
     */
    private Boolean isMultiTurn;

    /**
     * 搜索结果列表
     */
    private List<SearchResultItem> results;

    /**
     * 总结果数
     */
    private Long total;

    /**
     * 搜索响应时间(ms)
     */
    private Integer responseTime;

    /**
     * 相关推荐词
     */
    private List<String> relatedQueries;

    /**
     * 纠错建议（如果有）
     */
    private String correctedQuery;

    /**
     * 是否需要纠错
     */
    private Boolean hasCorrection;

    @Data
    public static class SearchResultItem {
        /**
         * POI ID
         */
        private Long poiId;

        /**
         * 商户名称
         */
        private String name;

        /**
         * 分类
         */
        private String category;

        /**
         * 地址
         */
        private String address;

        /**
         * 距离(米)
         */
        private Integer distance;

        /**
         * 评分
         */
        private BigDecimal rating;

        /**
         * 人均消费
         */
        private Integer avgPrice;

        /**
         * 图片URL
         */
        private String coverImage;

        /**
         * 标签列表
         */
        private List<String> tags;

        /**
         * 营业时间
         */
        private String businessHours;

        /**
         * 是否营业中
         */
        private Boolean isOpen;

        /**
         * 热度分数
         */
        private Double hotScore;

        /**
         * 相关性分数
         */
        private Double relevanceScore;
    }
}
