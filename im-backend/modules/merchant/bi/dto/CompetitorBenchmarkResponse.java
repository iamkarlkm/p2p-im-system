package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 竞品对标响应DTO
 */
@Data
public class CompetitorBenchmarkResponse {

    /** 商户ID */
    private Long merchantId;

    /** 对标类型 */
    private String benchmarkType;

    /** 对标对象名称 */
    private String benchmarkTargetName;

    /** 商户排名 */
    private Integer merchantRank;

    /** 总商户数 */
    private Integer totalMerchantCount;

    /** 综合得分 */
    private BigDecimal compositeScore;

    /** 各项指标对比 */
    private List<BenchmarkItem> benchmarkItems;

    /** 趋势对比 */
    private List<TrendComparison> trendComparison;

    /**
     * 对标项
     */
    @Data
    public static class BenchmarkItem {
        /** 指标名称 */
        private String metricName;
        /** 商户数值 */
        private BigDecimal merchantValue;
        /** 平均值 */
        private BigDecimal avgValue;
        /** 差距百分比 */
        private BigDecimal gapPercentage;
        /** 是否领先 */
        private Boolean isLeading;
        /** 排名 */
        private Integer rank;
    }

    /**
     * 趋势对比
     */
    @Data
    public static class TrendComparison {
        private String date;
        private BigDecimal merchantValue;
        private BigDecimal avgValue;
        private BigDecimal topValue;
    }
}
