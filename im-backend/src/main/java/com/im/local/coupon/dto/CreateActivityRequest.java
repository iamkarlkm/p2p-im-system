package com.im.local.coupon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建营销活动请求DTO
 */
@Data
public class CreateActivityRequest {

    /** 商户ID */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /** 活动名称 */
    @NotBlank(message = "活动名称不能为空")
    private String name;

    /** 活动类型: 1-满减 2-折扣 3-秒杀 4-拼团 5-砍价 */
    @NotNull(message = "活动类型不能为空")
    private Integer type;

    /** 活动描述 */
    private String description;

    /** 活动封面图 */
    private String coverImage;

    /** 活动开始时间 */
    private String startTime;

    /** 活动结束时间 */
    private String endTime;

    /** 活动规则配置(JSON) */
    private String rules;

    /** 参与门槛 */
    private Integer participationThreshold;

    /** 门槛金额 */
    private BigDecimal thresholdAmount;

    /** 库存数量 */
    private Integer stockQuantity;

    /** 是否需分享 */
    private Integer requireShare;

    /** LBS围栏启用 */
    private Integer geofenceEnabled;

    /** 围栏经度 */
    private Double fenceLongitude;

    /** 围栏纬度 */
    private Double fenceLatitude;

    /** 围栏半径 */
    private Integer fenceRadius;
}
