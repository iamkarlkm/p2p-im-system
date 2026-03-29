package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商户口碑统计数据实体
 * 实时统计商户的各项口碑指标
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_reputation_stats")
public class MerchantReputationStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 综合评分 0-5.0 */
    private Double overallScore;

    /** 口味评分 0-5.0 */
    private Double tasteScore;

    /** 环境评分 0-5.0 */
    private Double environmentScore;

    /** 服务评分 0-5.0 */
    private Double serviceScore;

    /** 性价比评分 0-5.0 */
    private Double valueScore;

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

    /** 好评率 0-100% */
    private Double positiveRate;

    /** 有图评价数 */
    private Integer hasImageCount;

    /** 视频评价数 */
    private Integer hasVideoCount;

    /** 追评数 */
    private Integer追加ReviewCount;

    /** 商户回复数 */
    private Integer merchantReplyCount;

    /** 好评数（4-5星） */
    private Integer positiveCount;

    /** 中评数（3星） */
    private Integer neutralCount;

    /** 差评数（1-2星） */
    private Integer negativeCount;

    /** 口碑排名（同商圈同类目） */
    private Integer rankingInCategory;

    /** 商圈总商户数 */
    private Integer totalInCategory;

    /** 今日新增评价数 */
    private Integer todayNewReviews;

    /** 本周新增评价数 */
    private Integer weekNewReviews;

    /** 本月新增评价数 */
    private Integer monthNewReviews;

    /** 最后更新时间 */
    private LocalDateTime lastUpdatedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
