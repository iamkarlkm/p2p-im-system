package com.im.local.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 商家回复评价请求DTO
 */
@Data
public class MerchantReplyRequest {

    @NotNull(message = "评价ID不能为空")
    private Long reviewId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容最多500字")
    private String content;
}
