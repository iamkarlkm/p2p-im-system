package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 本地生活搜索响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 搜索结果总数
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
     * 是否有下一页
     */
    private Boolean hasNextPage;

    /**
     * 搜索结果列表
     */
    private List<SearchResultItem> results;

    /**
     * 搜索耗时(ms)
     */
    private Integer searchTime;

    /**
     * 是否来自缓存
     */
    private Boolean fromCache;

    /**
     * 推荐筛选标签
     */
    private List<SuggestFilter> suggestFilters;

    /**
     * 搜索建议(相关搜索词)
     */
    private List<String> searchSuggestions;

    /**
     * 当前位置信息
     */
    private LocationInfo currentLocation;

    /**
     * 搜索统计
     */
    private SearchStats stats;

    /**
     * 搜索结果项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchResultItem {

        /**
         * POI/商户ID
         */
        private Long poiId;

        /**
         * 商户名称
         */
        private String name;

        /**
         * 商户类型
         */
        private String type;

        /**
         * 类型名称
         */
        private String typeName;

        /**
         * 详细地址
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
         * 距离(米)
         */
        private Integer distance;

        /**
         * 评分(0-5)
         */
        private Double rating;

        /**
         * 人均消费
         */
        private Integer avgPrice;

        /**
         * 电话
         */
        private String phone;

        /**
         * 营业时间
         */
        private String businessHours;

        /**
         * 是否营业中
         */
        private Boolean isOpen;

        /**
         * 图片列表
         */
        private List<String> photos;

        /**
         * 标签列表
         */
        private List<String> tags;

        /**
         * 优惠信息
         */
        private String promotionInfo;

        /**
         * 当前活动数
         */
        private Integer activityCount;

        /**
         * 近期订单数
         */
        private Integer recentOrderCount;

        /**
         * 推荐理由
         */
        private String recommendReason;

        /**
         * 相关性分数
         */
        private Double relevanceScore;
    }

    /**
     * 推荐筛选
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SuggestFilter {

        /**
         * 筛选类型: DISTRICT-区域, TYPE-类型, PRICE-价格, TAG-标签
         */
        private String filterType;

        /**
         * 筛选值
         */
        private String filterValue;

        /**
         * 显示名称
         */
        private String displayName;

        /**
         * 结果数量
         */
        private Integer count;
    }

    /**
     * 位置信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationInfo {

        /**
         * 城市名称
         */
        private String cityName;

        /**
         * 区县名称
         */
        private String districtName;

        /**
         * 街道名称
         */
        private String streetName;

        /**
         * 搜索中心经度
         */
        private Double searchLongitude;

        /**
         * 搜索中心纬度
         */
        private Double searchLatitude;
    }

    /**
     * 搜索统计
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SearchStats {

        /**
         * 附近商户总数
         */
        private Integer nearbyMerchantCount;

        /**
         * 有优惠活动商户数
         */
        private Integer promotionMerchantCount;

        /**
         * 新入驻商户数
         */
        private Integer newMerchantCount;

        /**
         * 当前时段营业商户数
         */
        private Integer openNowMerchantCount;
    }
}
