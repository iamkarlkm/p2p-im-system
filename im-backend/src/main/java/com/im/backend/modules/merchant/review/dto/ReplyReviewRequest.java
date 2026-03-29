package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 回复评价请求DTO
 */
@Data
public class ReplyReviewRequest {

    @NotBlank(message = "评价ID不能为空")
    private String reviewId;

    private String parentReplyId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容不能超过500字")
    private String content;

    private String images;
}
