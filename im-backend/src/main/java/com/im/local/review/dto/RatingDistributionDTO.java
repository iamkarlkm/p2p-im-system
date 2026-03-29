package com.im.local.review.dto;

import lombok.Data;

/**
 * 评分分布DTO
 */
@Data
public class RatingDistributionDTO {

    /** 星级（1-5） */
    private Integer star;

    /** 数量 */
    private Integer count;

    /** 百分比 */
    private Double percentage;
}
