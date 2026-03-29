package com.im.backend.modules.local_life.search.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能搜索结果DTO
 */
@Data
public class IntelligentSearchResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 搜索ID
     */
    private Long searchId;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 原始查询文本
     */
    private String originalQuery;

    /**
     * 理解后的查询意图
     */
    private String understoodIntent;

    /**
     * 搜索意图类型
     */
    private String intentType;

    /**
     * 意图置信度
     */
    private Double intentConfidence;

    /**
     * 提取的实体列表
     */
    private List<ExtractedEntityDTO> extractedEntities;

    /**
     * 是否是多轮对话
     */
    private Boolean isMultiRound;

    /**
     * 对话轮次
     */
    private Integer dialogRound;

    /**
     * 智能回复消息
     */
    private String smartReply;

    /**
     * 搜索结果列表
     */
    private List<SearchResultItemDTO> results;

    /**
     * 总结果数
     */
    private Long totalCount;

    /**
     * 当前页码
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
     * 搜索耗时(ms)
     */
    private Long searchTimeMs;

    /**
     * 是否零结果
     */
    private Boolean isZeroResult;

    /**
     * 推荐搜索词（零结果时提供）
     */
    private List<String> suggestedQueries;

    /**
     * 热门搜索词
     */
    private List<String> hotSearches;

    /**
     * 搜索时间
     */
    private LocalDateTime searchTime;

    /**
     * 提取的实体DTO
     */
    @Data
    public static class ExtractedEntityDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 实体类型: LOCATION-位置, CATEGORY-类别, PRICE-价格, TIME-时间, BRAND-品牌
         */
        private String entityType;

        /**
         * 实体值
         */
        private String entityValue;

        /**
         * 原始文本
         */
        private String originalText;

        /**
         * 置信度
         */
        private Double confidence;
    }

    /**
     * 搜索结果项DTO
     */
    @Data
    public static class SearchResultItemDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * POI/商户ID
         */
        private Long poiId;

        /**
         * 商户名称
         */
        private String name;

        /**
         * 商户类别
         */
        private String category;

        /**
         * 类别名称
         */
        private String categoryName;

        /**
         * 主图URL
         */
        private String mainImage;

        /**
         * 图片列表
         */
        private List<String> images;

        /**
         * 评分
         */
        private Double rating;

        /**
         * 评分人数
         */
        private Integer ratingCount;

        /**
         * 人均消费
         */
        private Integer avgPrice;

        /**
         * 距离(米)
         */
        private Integer distance;

        /**
         * 距离描述
         */
        private String distanceText;

        /**
         * 地址
         */
        private String address;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 纬度
         */
        private Double latitude;

        /**
         * 营业时间
         */
        private String businessHours;

        /**
         * 是否营业中
         */
        private Boolean isOpen;

        /**
         * 电话
         */
        private String phone;

        /**
         * 特色标签
         */
        private List<String> tags;

        /**
         * 优惠信息
         */
        private List<String> promotions;

        /**
         * 当前排队人数
         */
        private Integer queueCount;

        /**
         * 是否可预约
         */
        private Boolean canReserve;

        /**
         * 是否有优惠券
         */
        private Boolean hasCoupon;

        /**
         * 相关度分数
         */
        private Double relevanceScore;

        /**
         * 排序分数
         */
        private Double sortScore;

        /**
         * 推荐理由
         */
        private String recommendReason;

        /**
         * 好友推荐信息
         */
        private List<FriendRecommendDTO> friendRecommends;

        /**
         * 额外属性
         */
        private Map<String, Object> extraProperties;
    }

    /**
     * 好友推荐DTO
     */
    @Data
    public static class FriendRecommendDTO implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 好友ID
         */
        private Long friendId;

        /**
         * 好友昵称
         */
        private String friendName;

        /**
         * 好友头像
         */
        private String friendAvatar;

        /**
         * 推荐类型: VISITED-去过, LIKED-喜欢, RECOMMENDED-推荐
         */
        private String recommendType;

        /**
         * 推荐语
         */
        private String recommendText;
    }
}
