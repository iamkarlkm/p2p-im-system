package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 审核评价请求DTO
 */
@Data
public class AuditReviewRequest {

    @NotBlank(message = "评价ID不能为空")
    private String reviewId;

    @NotNull(message = "审核结果不能为空")
    private Integer status; // 1-通过 2-拒绝

    private String reason;
}
