package com.im.local.review.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建评价请求DTO
 */
@Data
public class CreateReviewRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "综合评分不能为空")
    @DecimalMin(value = "1.0", message = "评分最低1分")
    @DecimalMax(value = "5.0", message = "评分最高5分")
    private BigDecimal overallRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal tasteRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal environmentRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal serviceRating;

    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private BigDecimal valueRating;

    @Size(max = 2000, message = "评价内容最多2000字")
    private String content;

    /** 评价类型：1-文字 2-图文 3-视频 */
    private Integer reviewType;

    /** 是否匿名：0-实名 1-匿名 */
    private Integer isAnonymous;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 消费时间 */
    private LocalDateTime consumptionTime;

    /** 媒体文件列表 */
    private List<ReviewMediaDTO> mediaList;
}
