package com.im.service.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 竞品对标分析响应DTO
 */
@Data
public class CompetitorBenchmarkResponse {

    /** 商户排名 */
    private Integer ranking;

    /** 商圈商户总数 */
    private Integer totalMerchants;

    /** 商户评分 */
    private BigDecimal merchantRating;

    /** 商圈平均评分 */
    private BigDecimal avgRating;

    /** 评分超越百分比 */
    private BigDecimal ratingPercentile;

    /** 商户月销量 */
    private Integer merchantMonthlySales;

    /** 商圈平均销量 */
    private Integer avgMonthlySales;

    /** 销量超越百分比 */
    private BigDecimal salesPercentile;

    /** 商户客单价 */
    private BigDecimal merchantAvgPrice;

    /** 商圈平均客单价 */
    private BigDecimal avgPrice;

    /** 评价数量 */
    private Integer reviewCount;

    /** 好评率 */
    private BigDecimal positiveRate;

    /** 商圈排名趋势 */
    private List<RankingTrend> rankingTrend;

    @Data
    public static class RankingTrend {
        private String date;
        private Integer ranking;
        private BigDecimal rating;
    }
}
