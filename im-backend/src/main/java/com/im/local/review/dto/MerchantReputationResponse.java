package com.im.local.review.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户口碑统计响应DTO
 */
@Data
public class MerchantReputationResponse {

    private Long merchantId;

    /** 综合评分 */
    private BigDecimal overallScore;
    private BigDecimal tasteScore;
    private BigDecimal environmentScore;
    private BigDecimal serviceScore;
    private BigDecimal valueScore;

    /** 评价分布 */
    private Integer totalReviews;
    private Integer fiveStarCount;
    private Integer fourStarCount;
    private Integer threeStarCount;
    private Integer twoStarCount;
    private Integer oneStarCount;

    /** 好评率 */
    private BigDecimal positiveRate;

    /** 有图评价数 */
    private Integer withImageCount;

    /** 优质评价数 */
    private Integer recommendedCount;

    /** 排名 */
    private Integer districtRank;
    private Integer categoryRank;

    /** 口碑标签 */
    private List<String> tags;

    /** 评分分布百分比 */
    private List<RatingDistributionDTO> ratingDistribution;
}
