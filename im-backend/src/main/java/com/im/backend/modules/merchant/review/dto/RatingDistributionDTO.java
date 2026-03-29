package com.im.backend.modules.merchant.review.dto;

import lombok.Data;

/**
 * 评分分布DTO
 */
@Data
public class RatingDistributionDTO {

    private Integer fiveStar;
    private Integer fourStar;
    private Integer threeStar;
    private Integer twoStar;
    private Integer oneStar;
}
