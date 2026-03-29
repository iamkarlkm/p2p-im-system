package com.im.backend.modules.local_life.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价DTO
 */
@Data
public class MerchantReviewDTO {

    /** 评价ID */
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** POI兴趣点ID */
    private Long poiId;

    /** 用户ID */
    private Long userId;

    /** 订单ID */
    private Long orderId;

    /** 综合星级评分 */
    private BigDecimal overallRating;

    /** 口味评分 */
    private BigDecimal tasteRating;

    /** 环境评分 */
    private BigDecimal environmentRating;

    /** 服务评分 */
    private BigDecimal serviceRating;

    /** 性价比评分 */
    private BigDecimal valueRating;

    /** 评价内容 */
    private String content;

    /** 评价图片列表 */
    private List<String> images;

    /** 评价视频URL */
    private String videoUrl;

    /** 视频封面图 */
    private String videoCover;

    /** 视频时长（秒） */
    private Integer videoDuration;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 人均消费 */
    private BigDecimal perCapitaCost;

    /** 是否匿名 */
    private Boolean anonymous;

    /** 评价类型：1-图文 2-视频 */
    private Integer reviewType;

    /** 来源：1-APP 2-小程序 3-H5 */
    private Integer source;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 是否推荐 */
    private Boolean recommended;

    /** 用餐时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime diningTime;

    /** 用餐人数 */
    private Integer diningPeople;

    /** 标签列表 */
    private List<String> tags;

    /** 是否体验过 */
    private Boolean experienced;

    /** 体验方式 */
    private Integer experienceType;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // ========== 用户信息 ==========

    /** 用户昵称 */
    private String userNickname;

    /** 用户头像 */
    private String userAvatar;

    /** 用户等级 */
    private Integer userLevel;

    // ========== 商户信息 ==========

    /** 商户名称 */
    private String merchantName;

    /** 商户Logo */
    private String merchantLogo;

    // ========== 交互状态 ==========

    /** 是否已点赞 */
    private Boolean hasLiked;

    /** 回复列表 */
    private List<MerchantReviewReplyDTO> replies;

    /** 星级文本描述 */
    private String ratingText;

    /** 是否为优质评价 */
    private Boolean highQuality;
}
