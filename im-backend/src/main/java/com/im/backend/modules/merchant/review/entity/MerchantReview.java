package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价实体类
 * 支持多维度评分、图文评价、短视频评价
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review")
public class MerchantReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private String reviewId;

    /** 商户ID */
    private Long merchantId;

    /** 用户评价ID */
    private Long userId;

    /** 订单ID（可选） */
    private Long orderId;

    /** 综合星级评分 1-5 */
    private Integer overallRating;

    /** 口味评分 1-5 */
    private Integer tasteRating;

    /** 环境评分 1-5 */
    private Integer environmentRating;

    /** 服务评分 1-5 */
    private Integer serviceRating;

    /** 性价比评分 1-5 */
    private Integer valueRating;

    /** 评价内容 */
    private String content;

    /** 评价图片URL列表（JSON数组） */
    private String images;

    /** 评价视频URL */
    private String videoUrl;

    /** 是否匿名评价 */
    private Boolean anonymous;

    /** 消费金额（用于验证真实消费） */
    private Integer consumeAmount;

    /** 用餐人数 */
    private Integer dinerCount;

    /** 人均消费 */
    private Integer perCapitaAmount;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 情感分析得分 -1~1 */
    private Double sentimentScore;

    /** AI质量评分 0-100 */
    private Integer qualityScore;

    /** 是否为虚假评价 */
    private Boolean fakeReview;

    /** 虚假评价原因 */
    private String fakeReason;

    /** 评价状态 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 置顶排序权重 */
    private Integer pinWeight;

    /** 是否精华评价 */
    private Boolean eliteReview;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 审核时间 */
    private LocalDateTime auditedAt;

    /** 审核人 */
    private Long auditorId;
}
