package com.im.backend.modules.merchant.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 评价统计VO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "评价统计VO")
public class ReviewStatisticsVO {

    @Schema(description = "总评价数")
    private Long totalCount;

    @Schema(description = "平均评分")
    private BigDecimal avgRating;

    @Schema(description = "五星评价数")
    private Long fiveStarCount;

    @Schema(description = "四星评价数")
    private Long fourStarCount;

    @Schema(description = "三星评价数")
    private Long threeStarCount;

    @Schema(description = "二星评价数")
    private Long twoStarCount;

    @Schema(description = "一星评价数")
    private Long oneStarCount;

    @Schema(description = "平均口味评分")
    private BigDecimal avgTaste;

    @Schema(description = "平均环境评分")
    private BigDecimal avgEnvironment;

    @Schema(description = "平均服务评分")
    private BigDecimal avgService;

    @Schema(description = "平均性价比评分")
    private BigDecimal avgValue;

    /**
     * 获取五星率
     */
    public BigDecimal getFiveStarRate() {
        if (totalCount == null || totalCount == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(fiveStarCount)
            .multiply(new BigDecimal("100"))
            .divide(BigDecimal.valueOf(totalCount), 1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取好评率（4-5星）
     */
    public BigDecimal getPositiveRate() {
        if (totalCount == null || totalCount == 0) return BigDecimal.ZERO;
        long positiveCount = (fiveStarCount != null ? fiveStarCount : 0) +
                            (fourStarCount != null ? fourStarCount : 0);
        return BigDecimal.valueOf(positiveCount)
            .multiply(new BigDecimal("100"))
            .divide(BigDecimal.valueOf(totalCount), 1, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 获取评分分布百分比
     */
    public int[] getRatingDistribution() {
        if (totalCount == null || totalCount == 0) return new int[]{0, 0, 0, 0, 0};
        return new int[]{
            (int) Math.round((fiveStarCount != null ? fiveStarCount : 0) * 100.0 / totalCount),
            (int) Math.round((fourStarCount != null ? fourStarCount : 0) * 100.0 / totalCount),
            (int) Math.round((threeStarCount != null ? threeStarCount : 0) * 100.0 / totalCount),
            (int) Math.round((twoStarCount != null ? twoStarCount : 0) * 100.0 / totalCount),
            (int) Math.round((oneStarCount != null ? oneStarCount : 0) * 100.0 / totalCount)
        };
    }
}
