package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户评价统计实体类
 * 实时统计商户的各项评分指标
 */
@Data
@TableName("merchant_review_statistic")
public class MerchantReviewStatistic {

    /** 统计ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** POI兴趣点ID */
    private Long poiId;

    /** 综合评分（1-5分） */
    private BigDecimal overallRating;

    /** 口味评分（餐饮类） */
    private BigDecimal tasteRating;

    /** 环境评分 */
    private BigDecimal environmentRating;

    /** 服务评分 */
    private BigDecimal serviceRating;

    /** 性价比评分 */
    private BigDecimal valueRating;

    /** 总评价数 */
    private Integer totalCount;

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

    /** 有图评价数 */
    private Integer withImageCount;

    /** 视频评价数 */
    private Integer withVideoCount;

    /** 好评数（4-5星） */
    private Integer positiveCount;

    /** 中评数（3星） */
    private Integer neutralCount;

    /** 差评数（1-2星） */
    private Integer negativeCount;

    /** 好评率（百分比） */
    private BigDecimal positiveRate;

    /** 最新评价时间 */
    private LocalDateTime latestReviewTime;

    /** 日新增评价数 */
    private Integer dailyNewCount;

    /** 周新增评价数 */
    private Integer weeklyNewCount;

    /** 月新增评价数 */
    private Integer monthlyNewCount;

    /** 口味赞标签数 */
    private Integer tagTasteGoodCount;

    /** 环境好标签数 */
    private Integer tagEnvGoodCount;

    /** 服务好标签数 */
    private Integer tagServiceGoodCount;

    /** 性价比高标签数 */
    private Integer tagValueGoodCount;

    /** 回头客标签数 */
    private Integer tagReturningCount;

    /** 版本号（乐观锁） */
    @Version
    private Integer version;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 计算好评率
     */
    public BigDecimal calculatePositiveRate() {
        if (totalCount == null || totalCount == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal positive = new BigDecimal(positiveCount != null ? positiveCount : 0);
        BigDecimal total = new BigDecimal(totalCount);
        return positive.multiply(new BigDecimal(100))
                      .divide(total, 1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 根据评分更新星级计数
     */
    public void updateStarCount(BigDecimal rating, boolean increment) {
        int change = increment ? 1 : -1;
        double r = rating != null ? rating.doubleValue() : 0;
        
        if (r >= 4.5) {
            fiveStarCount = (fiveStarCount != null ? fiveStarCount : 0) + change;
            positiveCount = (positiveCount != null ? positiveCount : 0) + change;
        } else if (r >= 4.0) {
            fourStarCount = (fourStarCount != null ? fourStarCount : 0) + change;
            positiveCount = (positiveCount != null ? positiveCount : 0) + change;
        } else if (r >= 3.0) {
            threeStarCount = (threeStarCount != null ? threeStarCount : 0) + change;
            neutralCount = (neutralCount != null ? neutralCount : 0) + change;
        } else if (r >= 2.0) {
            twoStarCount = (twoStarCount != null ? twoStarCount : 0) + change;
            negativeCount = (negativeCount != null ? negativeCount : 0) + change;
        } else {
            oneStarCount = (oneStarCount != null ? oneStarCount : 0) + change;
            negativeCount = (negativeCount != null ? negativeCount : 0) + change;
        }
        
        totalCount = (totalCount != null ? totalCount : 0) + change;
        this.positiveRate = calculatePositiveRate();
    }
}
