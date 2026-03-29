package com.im.local.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户口碑统计实体
 * 实时统计商户的各项口碑指标
 */
@Data
@TableName("merchant_reputation_stats")
public class MerchantReputationStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 综合评分 */
    private BigDecimal overallScore;

    /** 口味评分 */
    private BigDecimal tasteScore;

    /** 环境评分 */
    private BigDecimal environmentScore;

    /** 服务评分 */
    private BigDecimal serviceScore;

    /** 性价比评分 */
    private BigDecimal valueScore;

    /** 总评价数 */
    private Integer totalReviews;

    /** 5星评价数 */
    private Integer fiveStarCount;

    /** 4星评价数 */
    private Integer fourStarCount;

    /** 3星评价数 */
    private Integer threeStarCount;

    /** 2星评价数 */
    private Integer twoStarCount;

    /** 1星评价数 */
    private Integer oneStarCount;

    /** 好评率（%） */
    private BigDecimal positiveRate;

    /** 有图评价数 */
    private Integer withImageCount;

    /** 优质评价数 */
    private Integer recommendedCount;

    /** 商圈排名 */
    private Integer districtRank;

    /** 分类排名 */
    private Integer categoryRank;

    /** 口碑标签（JSON数组） */
    private String tags;

    /** 统计更新时间 */
    private LocalDateTime statsUpdatedAt;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
