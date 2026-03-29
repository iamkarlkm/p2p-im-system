package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券详情响应DTO
 */
@Data
public class CouponDetailResponse {

    /** 优惠券ID */
    private Long id;

    /** 优惠券模板ID */
    private Long templateId;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 优惠券名称 */
    private String name;

    /** 优惠券描述 */
    private String description;

    /** 优惠券类型 */
    private Integer type;

    /** 优惠券类型名称 */
    private String typeName;

    /** 面额/折扣值 */
    private BigDecimal value;

    /** 使用门槛 */
    private BigDecimal minSpend;

    /** 最大优惠金额 */
    private BigDecimal maxDiscount;

    /** 发放总量 */
    private Integer totalQuantity;

    /** 已领取数量 */
    private Integer receivedQuantity;

    /** 剩余数量 */
    private Integer remainingQuantity;

    /** 每人限领数量 */
    private Integer limitPerUser;

    /** 有效期类型 */
    private Integer validityType;

    /** 有效期开始时间 */
    private LocalDateTime validStartTime;

    /** 有效期结束时间 */
    private LocalDateTime validEndTime;

    /** 领取后有效天数 */
    private Integer validDays;

    /** 适用商品类型 */
    private Integer applyScope;

    /** 是否启用地理围栏 */
    private Integer geofenceEnabled;

    /** 围栏中心经度 */
    private Double fenceLongitude;

    /** 围栏中心纬度 */
    private Double fenceLatitude;

    /** 围栏半径 */
    private Integer fenceRadius;

    /** 状态 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 创建时间 */
    private LocalDateTime createTime;
}
