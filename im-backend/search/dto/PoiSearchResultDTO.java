package com.im.search.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * POI搜索结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoiSearchResultDTO {

    /** POI ID */
    private String id;

    /** POI名称 */
    private String name;

    /** 品牌名称 */
    private String brandName;

    /** 分类名称 */
    private String category;

    /** 分类图标 */
    private String categoryIcon;

    /** 详细地址 */
    private String address;

    /** 距离（米） */
    private Integer distance;

    /** 距离描述 */
    private String distanceText;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 评分 */
    private Float rating;

    /** 评分数量 */
    private Integer ratingCount;

    /** 人均消费 */
    private Integer avgPrice;

    /** 价格描述 */
    private String priceText;

    /** 特色标签 */
    private List<String> tags;

    /** 主图URL */
    private String mainImage;

    /** 图片列表 */
    private List<String> images;

    /** 营业时间 */
    private String businessHours;

    /** 是否营业中 */
    private Boolean isOpen;

    /** 电话 */
    private String phone;

    /** 是否支持WiFi */
    private Boolean hasWifi;

    /** 是否支持停车 */
    private Boolean hasParking;

    /** 热搜标签 */
    private List<String> hotTags;

    /** 推荐语 */
    private String recommendation;

    /** 优惠信息 */
    private List<CouponInfoDTO> coupons;

    /** 商户活动 */
    private List<ActivityInfoDTO> activities;

    /** 推荐理由（个性化） */
    private String personalizedReason;

    /** 好友推荐 */
    private List<FriendRecommendDTO> friendRecommends;

    /** 扩展字段 */
    private Map<String, Object> extFields;

    /**
     * 优惠券信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponInfoDTO {
        private String couponId;
        private String title;
        private String discount;
        private Boolean isNewUserOnly;
    }

    /**
     * 活动信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityInfoDTO {
        private String activityId;
        private String title;
        private String type;
        private String tag;
    }

    /**
     * 好友推荐DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRecommendDTO {
        private Long userId;
        private String userName;
        private String avatar;
        private String comment;
        private Float rating;
    }
}
