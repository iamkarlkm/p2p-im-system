package com.im.local.review.dto;

import lombok.Data;

/**
 * 评价点赞请求DTO
 */
@Data
public class LikeReviewRequest {

    private Long reviewId;

    /** 1-点赞 0-取消点赞 */
    private Integer action;
}
