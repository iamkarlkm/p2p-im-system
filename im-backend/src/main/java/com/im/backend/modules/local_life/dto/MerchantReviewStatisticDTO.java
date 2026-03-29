package com.im.backend.modules.local_life.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户评价统计DTO
 */
@Data
public class MerchantReviewStatisticDTO {

    /** 商户ID */
    private Long merchantId;

    /** 综合评分 */
    private BigDecimal overallRating;

    /** 口味评分 */
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

    /** 好评数 */
    private Integer positiveCount;

    /** 中评数 */
    private Integer neutralCount;

    /** 差评数 */
    private Integer negativeCount;

    /** 好评率 */
    private BigDecimal positiveRate;

    /** 日新增评价数 */
    private Integer dailyNewCount;

    /** 周新增评价数 */
    private Integer weeklyNewCount;

    /** 月新增评价数 */
    private Integer monthlyNewCount;

    /** 星级分布百分比 */
    private StarDistributionDTO starDistribution;

    /** 标签统计 */
    private TagStatisticDTO tagStatistic;

    @Data
    public static class StarDistributionDTO {
        private Double fiveStarPercent;
        private Double fourStarPercent;
        private Double threeStarPercent;
        private Double twoStarPercent;
        private Double oneStarPercent;
    }

    @Data
    public static class TagStatisticDTO {
        private Integer tasteGoodCount;
        private Integer envGoodCount;
        private Integer serviceGoodCount;
        private Integer valueGoodCount;
        private Integer returningCount;
    }
}
