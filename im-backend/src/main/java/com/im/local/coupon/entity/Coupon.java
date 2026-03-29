package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 * 本地生活优惠券与精准营销系统 - 核心优惠券模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    /** 优惠券ID */
    private Long id;

    /** 优惠券模板ID */
    private Long templateId;

    /** 商户ID */
    private Long merchantId;

    /** 优惠券名称 */
    private String name;

    /** 优惠券描述 */
    private String description;

    /** 优惠券类型: 1-满减券 2-折扣券 3-代金券 4-兑换券 */
    private Integer type;

    /** 面额/折扣值(满减券为金额,折扣券为折扣率如0.85) */
    private BigDecimal value;

    /** 使用门槛(0表示无门槛) */
    private BigDecimal minSpend;

    /** 最大优惠金额(折扣券使用) */
    private BigDecimal maxDiscount;

    /** 发放总量(-1表示不限量) */
    private Integer totalQuantity;

    /** 已领取数量 */
    private Integer receivedQuantity;

    /** 已使用数量 */
    private Integer usedQuantity;

    /** 每人限领数量 */
    private Integer limitPerUser;

    /** 有效期类型: 1-固定日期 2-领取后生效 */
    private Integer validityType;

    /** 有效期开始时间(固定日期类型) */
    private LocalDateTime validStartTime;

    /** 有效期结束时间(固定日期类型) */
    private LocalDateTime validEndTime;

    /** 领取后有效天数(领取后生效类型) */
    private Integer validDays;

    /** 适用商品类型: 0-全部商品 1-指定分类 2-指定商品 */
    private Integer applyScope;

    /** 适用分类ID列表(逗号分隔) */
    private String applyCategoryIds;

    /** 适用商品ID列表(逗号分隔) */
    private String applyProductIds;

    /** LBS地理围栏: 1-启用 0-禁用 */
    private Integer geofenceEnabled;

    /** 围栏中心经度 */
    private Double fenceLongitude;

    /** 围栏中心纬度 */
    private Double fenceLatitude;

    /** 围栏半径(米) */
    private Integer fenceRadius;

    /** 状态: 0-草稿 1-未开始 2-进行中 3-已结束 4-已作废 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 创建人ID */
    private Long createBy;

    /** 是否删除: 0-否 1-是 */
    private Integer deleted;
}
