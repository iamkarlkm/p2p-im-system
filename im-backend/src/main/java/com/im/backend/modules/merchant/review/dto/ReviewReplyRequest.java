package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 评价回复请求DTO - 功能#310: 本地商户评价口碑
 */
@Data
public class ReviewReplyRequest {

    @NotNull(message = "评价ID不能为空")
    private Long reviewId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容最多500字")
    private String content;

    /** 父回复ID (用于回复其他回复) */
    private Long parentId;
}
