package com.im.local.review.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价响应DTO
 */
@Data
public class ReviewResponse {

    private Long id;
    private Long merchantId;
    private Long userId;
    private String userNickname;
    private String userAvatar;

    /** 评分 */
    private BigDecimal overallRating;
    private BigDecimal tasteRating;
    private BigDecimal environmentRating;
    private BigDecimal serviceRating;
    private BigDecimal valueRating;

    /** 评价内容 */
    private String content;
    private Integer reviewType;
    private String reviewTypeDesc;

    /** 状态 */
    private Integer status;
    private String statusDesc;

    /** 是否匿名 */
    private Integer isAnonymous;

    /** 互动数据 */
    private Integer likeCount;
    private Integer replyCount;
    private Integer viewCount;
    private Boolean hasLiked;

    /** 优质评价 */
    private Integer isRecommended;

    /** 消费信息 */
    private BigDecimal consumptionAmount;
    private LocalDateTime consumptionTime;

    /** 媒体列表 */
    private List<ReviewMediaDTO> mediaList;

    /** 商家回复 */
    private String merchantReply;
    private LocalDateTime merchantReplyTime;

    /** 时间 */
    private LocalDateTime createdAt;
    private String timeDesc;
}
