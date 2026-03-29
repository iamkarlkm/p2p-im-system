package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评价回复DTO
 */
@Data
public class ReviewReplyDTO {

    private String replyId;
    private String parentReplyId;
    private Long replierId;
    private String replierName;
    private String replierAvatar;
    private Integer replierType; // 1-商户 2-用户
    private String content;
    private List<String> images;
    private Integer likeCount;
    private LocalDateTime createdAt;
}
