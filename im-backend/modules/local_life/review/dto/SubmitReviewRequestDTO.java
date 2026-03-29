package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提交评价请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class SubmitReviewRequestDTO {
    
    /** 商户ID */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;
    
    /** 订单ID (可选) */
    private Long orderId;
    
    /** 总体评分 (1-5) */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "最低1分")
    @Max(value = 5, message = "最高5分")
    private Integer overallRating;
    
    /** 口味评分 (1-5) */
    @Min(value = 1)
    @Max(value = 5)
    private Integer tasteRating;
    
    /** 环境评分 (1-5) */
    @Min(value = 1)
    @Max(value = 5)
    private Integer environmentRating;
    
    /** 服务评分 (1-5) */
    @Min(value = 1)
    @Max(value = 5)
    private Integer serviceRating;
    
    /** 性价比评分 (1-5) */
    @Min(value = 1)
    @Max(value = 5)
    private Integer valueRating;
    
    /** 评价内容 */
    @Size(max = 2000, message = "评价内容最多2000字")
    private String content;
    
    /** 评价图片列表 */
    private List<String> images;
    
    /** 评价视频URL */
    private String videoUrl;
    
    /** 视频封面 */
    private String videoCover;
    
    /** 消费金额 */
    private BigDecimal consumeAmount;
    
    /** 人均消费 */
    private BigDecimal perCapita;
    
    /** 是否匿名 */
    private Boolean anonymous;
    
    /** 是否推荐 */
    private Boolean recommended;
    
    /** 就餐日期 */
    private LocalDateTime diningDate;
    
    /** 评价标签 */
    private List<String> tags;
}
