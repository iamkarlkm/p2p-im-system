package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 举报评价请求DTO
 */
@Data
public class ReportReviewRequest {

    @NotBlank(message = "评价ID不能为空")
    private String reviewId;

    @NotNull(message = "举报类型不能为空")
    private Integer reportType;

    @Size(max = 500, message = "举报原因不能超过500字")
    private String reason;

    private String evidenceImages;
}
