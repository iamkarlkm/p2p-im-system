package com.im.backend.modules.merchant.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价响应DTO
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@Schema(description = "评价响应")
public class ReviewResponse {

    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "商户Logo")
    private String merchantLogo;

    @Schema(description = "POI ID")
    private Long poiId;

    @Schema(description = "POI名称")
    private String poiName;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "总体星级评分")
    private BigDecimal overallRating;

    @Schema(description = "评价标题")
    private String title;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评价类型：1-文字 2-图文 3-视频")
    private Integer reviewType;

    @Schema(description = "消费金额")
    private BigDecimal consumptionAmount;

    @Schema(description = "人均消费")
    private BigDecimal perCapitaAmount;

    @Schema(description = "是否推荐：0-不推荐 1-推荐")
    private Integer isRecommended;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "浏览数")
    private Integer viewCount;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "是否优质评价")
    private Integer isQuality;

    @Schema(description = "优质评分")
    private BigDecimal qualityScore;

    @Schema(description = "评价状态")
    private Integer status;

    @Schema(description = "是否已点赞")
    private Boolean hasLiked;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "评价维度列表")
    private List<DimensionResponse> dimensionList;

    @Schema(description = "媒体列表")
    private List<MediaResponse> mediaList;

    @Schema(description = "回复列表")
    private List<ReplyResponse> replyList;

    /**
     * 维度响应DTO
     */
    @Data
    @Schema(description = "评价维度")
    public static class DimensionResponse {

        @Schema(description = "维度ID")
        private Long id;

        @Schema(description = "维度编码")
        private String dimensionCode;

        @Schema(description = "维度名称")
        private String dimensionName;

        @Schema(description = "维度评分")
        private BigDecimal rating;

        @Schema(description = "评分标签")
        private String ratingLabel;

        @Schema(description = "维度标签")
        private String tags;
    }

    /**
     * 媒体响应DTO
     */
    @Data
    @Schema(description = "评价媒体")
    public static class MediaResponse {

        @Schema(description = "媒体ID")
        private Long id;

        @Schema(description = "媒体类型：1-图片 2-视频")
        private Integer mediaType;

        @Schema(description = "媒体URL")
        private String mediaUrl;

        @Schema(description = "缩略图URL")
        private String thumbnailUrl;

        @Schema(description = "宽度")
        private Integer width;

        @Schema(description = "高度")
        private Integer height;

        @Schema(description = "视频时长（秒）")
        private Integer duration;

        @Schema(description = "视频封面图")
        private String videoCover;
    }

    /**
     * 回复响应DTO
     */
    @Data
    @Schema(description = "评价回复")
    public static class ReplyResponse {

        @Schema(description = "回复ID")
        private Long id;

        @Schema(description = "父回复ID")
        private Long parentId;

        @Schema(description = "回复者ID")
        private Long replyUserId;

        @Schema(description = "回复者昵称")
        private String replyUserNickname;

        @Schema(description = "回复者头像")
        private String replyUserAvatar;

        @Schema(description = "回复者类型：1-用户 2-商家 3-平台")
        private Integer replyUserType;

        @Schema(description = "是否商家官方回复")
        private Integer isOfficial;

        @Schema(description = "被回复者昵称")
        private String toUserNickname;

        @Schema(description = "回复内容")
        private String content;

        @Schema(description = "点赞数")
        private Integer likeCount;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    /**
     * 获取评分星级显示
     */
    public String getRatingStars() {
        if (overallRating == null) {
            return "☆☆☆☆☆";
        }
        int fullStars = overallRating.intValue();
        boolean hasHalfStar = overallRating.doubleValue() - fullStars >= 0.5;
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        if (hasHalfStar) {
            stars.append("☆");
            fullStars++;
        }
        for (int i = fullStars; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
}
