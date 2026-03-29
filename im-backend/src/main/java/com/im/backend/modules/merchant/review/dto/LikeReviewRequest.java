package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 点赞评价请求DTO
 */
@Data
public class LikeReviewRequest {

    @NotBlank(message = "评价ID不能为空")
    private String reviewId;

    @NotNull(message = "操作类型不能为空")
    private Boolean like; // true-点赞 false-取消点赞
}
