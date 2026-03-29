package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 提交评价请求DTO
 */
@Data
public class SubmitReviewRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    private Long orderId;

    @NotNull(message = "综合评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer overallRating;

    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer tasteRating;

    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer environmentRating;

    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer serviceRating;

    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    private Integer valueRating;

    @Size(max = 1000, message = "评价内容不能超过1000字")
    private String content;

    private List<String> images;

    private String videoUrl;

    private Boolean anonymous = false;

    private Integer consumeAmount;

    private Integer dinerCount;

    private Integer perCapitaAmount;
}
