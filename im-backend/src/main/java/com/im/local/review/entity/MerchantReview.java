package com.im.local.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户评价实体
 * 支持多维度评分、图文评价、视频评价
 */
@Data
@TableName("merchant_review")
public class MerchantReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 订单ID（可选，关联订单评价） */
    private Long orderId;

    /** 综合星级评分（1-5分） */
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

    /** 评价类型：1-文字评价 2-图文评价 3-视频评价 */
    private Integer reviewType;

    /** 评价状态：0-待审核 1-已发布 2-已隐藏 3-已删除 */
    private Integer status;

    /** 是否匿名：0-实名 1-匿名 */
    private Integer isAnonymous;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 是否推荐（优质评价） */
    private Integer isRecommended;

    /** 是否置顶 */
    private Integer isTop;

    /** AI审核分数（0-100，越高越可能是虚假评价） */
    private Integer aiAuditScore;

    /** AI审核标签 */
    private String aiAuditTags;

    /** 商家回复内容 */
    private String merchantReply;

    /** 商家回复时间 */
    private LocalDateTime merchantReplyTime;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 消费时间 */
    private LocalDateTime consumptionTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;
}
