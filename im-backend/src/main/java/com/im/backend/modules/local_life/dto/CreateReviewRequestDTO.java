package com.im.backend.modules.local_life.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建评价请求DTO
 */
@Data
public class CreateReviewRequestDTO {

    /** 商户ID */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /** POI兴趣点ID */
    private Long poiId;

    /** 订单ID（可选） */
    private Long orderId;

    /** 综合评分（1-5分） */
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "0.5", message = "评分不能低于0.5")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    private BigDecimal overallRating;

    /** 口味评分 */
    @DecimalMin(value = "0.5", message = "评分不能低于0.5")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    private BigDecimal tasteRating;

    /** 环境评分 */
    @DecimalMin(value = "0.5", message = "评分不能低于0.5")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    private BigDecimal environmentRating;

    /** 服务评分 */
    @DecimalMin(value = "0.5", message = "评分不能低于0.5")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    private BigDecimal serviceRating;

    /** 性价比评分 */
    @DecimalMin(value = "0.5", message = "评分不能低于0.5")
    @DecimalMax(value = "5.0", message = "评分不能高于5.0")
    private BigDecimal valueRating;

    /** 评价内容 */
    @NotBlank(message = "评价内容不能为空")
    @Size(max = 2000, message = "评价内容最多2000字")
    private String content;

    /** 评价图片URLs */
    private List<String> images;

    /** 评价视频URL */
    private String videoUrl;

    /** 视频封面图 */
    private String videoCover;

    /** 视频时长（秒） */
    private Integer videoDuration;

    /** 消费金额 */
    private BigDecimal consumptionAmount;

    /** 人均消费 */
    private BigDecimal perCapitaCost;

    /** 是否匿名 */
    private Boolean anonymous = false;

    /** 用餐时间 */
    private LocalDateTime diningTime;

    /** 用餐人数 */
    @Min(value = 1, message = "用餐人数至少1人")
    @Max(value = 50, message = "用餐人数最多50人")
    private Integer diningPeople;

    /** 标签列表 */
    private List<String> tags;

    /** 体验方式：1-到店 2-外卖 3-预约 */
    private Integer experienceType = 1;

    /** 地理位置-经度 */
    private BigDecimal longitude;

    /** 地理位置-纬度 */
    private BigDecimal latitude;

    /** 地理位置名称 */
    private String locationName;
}
