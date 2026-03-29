package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 提交评分评论请求DTO
 */
@Data
public class SubmitReviewRequest {

    @NotNull(message = "小程序ID不能为空")
    private Long appId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    /**
     * 评论图片
     */
    private java.util.List<String> images;
}
