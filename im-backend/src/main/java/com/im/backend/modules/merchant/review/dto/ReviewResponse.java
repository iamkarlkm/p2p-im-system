package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价响应DTO - 功能#310: 本地商户评价口碑
 */
@Data
public class ReviewResponse {

    private Long id;
    private Long merchantId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private Long orderId;
    private Integer rating;
    private String content;
    private List<String> tags;
    private List<String> images;
    private String videoUrl;
    private Boolean anonymous;
    private Integer likeCount;
    private Integer replyCount;
    private Integer viewCount;
    private String merchantReply;
    private LocalDateTime merchantReplyTime;
    private Boolean recommended;
    private LocalDateTime createTime;
    private Boolean likedByCurrentUser;
    private BigDecimal consumeAmount;
    private String consumeItems;
}
