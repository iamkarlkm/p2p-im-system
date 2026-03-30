package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 提交评价请求DTO - 功能#310: 本地商户评价口碑
 */
@Data
public class ReviewSubmitRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;

    @Size(max = 500, message = "评价内容最多500字")
    private String content;

    /** 评价标签 */
    private List<String> tags;

    /** 评价图片 */
    private List<String> images;

    /** 评价视频 */
    private String videoUrl;

    /** 是否匿名 */
    private Boolean anonymous;

    /** 消费金额 */
    private BigDecimal consumeAmount;

    /** 消费项目 */
    private String consumeItems;
}
