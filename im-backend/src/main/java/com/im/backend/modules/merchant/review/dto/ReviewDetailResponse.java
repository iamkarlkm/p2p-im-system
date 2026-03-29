package com.im.backend.modules.merchant.review.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价详情响应DTO
 */
@Data
public class ReviewDetailResponse {

    private String reviewId;
    private Long merchantId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer overallRating;
    private Integer tasteRating;
    private Integer environmentRating;
    private Integer serviceRating;
    private Integer valueRating;
    private String content;
    private List<String> images;
    private String videoUrl;
    private Boolean anonymous;
    private Integer consumeAmount;
    private Integer dinerCount;
    private Integer perCapitaAmount;
    private Integer likeCount;
    private Integer replyCount;
    private Boolean hasLiked;
    private Boolean eliteReview;
    private LocalDateTime createdAt;
    private List<ReviewReplyDTO> replies;
}
