package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 商户评分统计DTO - 功能#310: 本地商户评价口碑
 */
@Data
public class ReviewStatsResponse {

    private Long merchantId;

    /** 总评价数 */
    private Integer totalCount;

    /** 平均评分 */
    private BigDecimal averageRating;

    /** 5星数量 */
    private Integer fiveStarCount;

    /** 4星数量 */
    private Integer fourStarCount;

    /** 3星数量 */
    private Integer threeStarCount;

    /** 2星数量 */
    private Integer twoStarCount;

    /** 1星数量 */
    private Integer oneStarCount;

    /** 好评率 */
    private BigDecimal goodRate;

    /** 有图评价数 */
    private Integer withImageCount;

    /** 追评数 */
    private Integer withReplyCount;
}
