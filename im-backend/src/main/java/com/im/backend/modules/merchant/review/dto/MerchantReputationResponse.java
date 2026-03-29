package com.im.backend.modules.merchant.review.dto;

import lombok.Data;

/**
 * 商户口碑统计响应DTO
 */
@Data
public class MerchantReputationResponse {

    private Long merchantId;
    private Double overallScore;
    private Double tasteScore;
    private Double environmentScore;
    private Double serviceScore;
    private Double valueScore;
    private Integer totalReviews;
    private RatingDistributionDTO ratingDistribution;
    private Integer hasImageCount;
    private Integer hasVideoCount;
    private Double positiveRate;
    private Integer rankingInCategory;
    private Integer totalInCategory;
}
