package com.im.backend.modules.local_life.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 语义搜索结果DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "语义搜索结果")
public class SemanticSearchResultDTO {

    @Schema(description = "POI ID", example = "12345")
    private Long poiId;

    @Schema(description = "商户名称", example = "海底捞火锅（南京西路店）")
    private String name;

    @Schema(description = "POI分类", example = "火锅")
    private String category;

    @Schema(description = "详细分类", example = "[\"四川火锅\", \"自助火锅\"]")
    private List<String> subCategories;

    @Schema(description = "地址", example = "上海市静安区南京西路1266号恒隆广场5楼")
    private String address;

    @Schema(description = "经度", example = "121.4512")
    private Double longitude;

    @Schema(description = "纬度", example = "31.2256")
    private Double latitude;

    @Schema(description = "距离（米）", example = "850")
    private Integer distance;

    @Schema(description = "评分 0-5", example = "4.8")
    private Double rating;

    @Schema(description = "评分人数", example = "3256")
    private Integer ratingCount;

    @Schema(description = "人均消费", example = "128")
    private Integer avgPrice;

    @Schema(description = "价格区间：低价/中价/高价", example = "中价")
    private String priceLevel;

    @Schema(description = "营业时间", example = "10:00-22:00")
    private String businessHours;

    @Schema(description = "当前是否营业", example = "true")
    private Boolean isOpenNow;

    @Schema(description = "联系电话", example = "021-12345678")
    private String phone;

    @Schema(description = "商户图片列表")
    private List<String> images;

    @Schema(description = "特色标签", example = "[\"包间\", \"免费停车\", \"网红打卡\"]")
    private List<String> tags;

    @Schema(description = "综合推荐分数", example = "0.92")
    private Double recommendScore;

    @Schema(description = "推荐理由", example = "距离您850米，评分4.8分，人均128元，符合您的预算要求")
    private String recommendReason;

    @Schema(description = "优惠信息")
    private List<PromotionInfoDTO> promotions;

    @Schema(description = "好友推荐信息")
    private SocialProofDTO socialProof;

    @Schema(description = "匹配的关键词", example = "[\"火锅\", \"人均128\"]")
    private List<String> matchedKeywords;

    @Schema(description = "相关性分数", example = "0.95")
    private Double relevanceScore;

    @Schema(description = "排序分数", example = "0.88")
    private Double sortScore;

    /**
     * 优惠信息DTO（内部类）
     */
    @Data
    @Schema(description = "优惠信息")
    public static class PromotionInfoDTO {
        @Schema(description = "优惠类型：DISCOUNT-折扣, COUPON-优惠券, GROUPON-团购")
        private String type;

        @Schema(description = "优惠标题", example = "双人套餐7折优惠")
        private String title;

        @Schema(description = "优惠详情")
        private String detail;

        @Schema(description = "原价", example = "298")
        private Integer originalPrice;

        @Schema(description = "现价", example = "208")
        private Integer currentPrice;
    }

    /**
     * 社交证明DTO（内部类）
     */
    @Data
    @Schema(description = "社交证明信息")
    public static class SocialProofDTO {
        @Schema(description = "推荐好友数量", example = "3")
        private Integer friendCount;

        @Schema(description = "好友头像列表")
        private List<String> friendAvatars;

        @Schema(description = "最新好友评价", example = "小明：味道很不错，服务也很棒！")
        private String latestFriendReview;
    }
}
