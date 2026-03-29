package com.im.local.coupon.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建优惠券请求DTO
 */
@Data
public class CreateCouponRequest {

    /** 优惠券模板ID */
    private Long templateId;

    /** 商户ID */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /** 优惠券名称 */
    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    /** 优惠券描述 */
    private String description;

    /** 优惠券类型: 1-满减券 2-折扣券 3-代金券 4-兑换券 */
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;

    /** 面额/折扣值 */
    @NotNull(message = "优惠面值不能为空")
    private BigDecimal value;

    /** 使用门槛(0表示无门槛) */
    private BigDecimal minSpend;

    /** 最大优惠金额(折扣券使用) */
    private BigDecimal maxDiscount;

    /** 发放总量(-1表示不限量) */
    private Integer totalQuantity;

    /** 每人限领数量 */
    private Integer limitPerUser;

    /** 有效期类型: 1-固定日期 2-领取后生效 */
    private Integer validityType;

    /** 有效期开始时间 */
    private String validStartTime;

    /** 有效期结束时间 */
    private String validEndTime;

    /** 领取后有效天数 */
    private Integer validDays;

    /** 适用商品类型: 0-全部商品 1-指定分类 2-指定商品 */
    private Integer applyScope;

    /** 适用分类ID列表 */
    private String applyCategoryIds;

    /** 适用商品ID列表 */
    private String applyProductIds;

    /** LBS地理围栏: 1-启用 0-禁用 */
    private Integer geofenceEnabled;

    /** 围栏中心经度 */
    private Double fenceLongitude;

    /** 围栏中心纬度 */
    private Double fenceLatitude;

    /** 围栏半径(米) */
    private Integer fenceRadius;
}
