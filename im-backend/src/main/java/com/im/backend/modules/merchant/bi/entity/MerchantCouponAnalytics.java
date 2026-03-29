package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商家优惠券效果分析实体
 * 记录优惠券发放、使用及效果数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_coupon_analytics")
public class MerchantCouponAnalytics {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;
    
    /**
     * 优惠券ID
     */
    @TableField("coupon_id")
    private Long couponId;
    
    /**
     * 优惠券名称
     */
    @TableField("coupon_name")
    private String couponName;
    
    /**
     * 优惠券类型
     */
    @TableField("coupon_type")
    private String couponType;
    
    /**
     * 统计日期
     */
    @TableField("report_date")
    private LocalDate reportDate;
    
    /**
     * 发放总量
     */
    @TableField("total_issued")
    private Integer totalIssued;
    
    /**
     * 已领取数量
     */
    @TableField("total_claimed")
    private Integer totalClaimed;
    
    /**
     * 已使用数量
     */
    @TableField("total_used")
    private Integer totalUsed;
    
    /**
     * 已过期数量
     */
    @TableField("total_expired")
    private Integer totalExpired;
    
    /**
     * 领取率(%)
     */
    @TableField("claim_rate")
    private BigDecimal claimRate;
    
    /**
     * 使用率(%)
     */
    @TableField("usage_rate")
    private BigDecimal usageRate;
    
    /**
     * 核销率(%)
     */
    @TableField("redemption_rate")
    private BigDecimal redemptionRate;
    
    /**
     * 优惠券面额
     */
    @TableField("face_value")
    private BigDecimal faceValue;
    
    /**
     * 优惠总金额
     */
    @TableField("total_discount_amount")
    private BigDecimal totalDiscountAmount;
    
    /**
     * 带动订单金额
     */
    @TableField("driven_revenue")
    private BigDecimal drivenRevenue;
    
    /**
     * 带动订单数量
     */
    @TableField("driven_orders")
    private Integer drivenOrders;
    
    /**
     * 新客使用数量
     */
    @TableField("new_customer_usage")
    private Integer newCustomerUsage;
    
    /**
     * 老客使用数量
     */
    @TableField("returning_customer_usage")
    private Integer returningCustomerUsage;
    
    /**
     * 人均消费
     */
    @TableField("avg_order_value")
    private BigDecimal avgOrderValue;
    
    /**
     * 优惠券成本
     */
    @TableField("coupon_cost")
    private BigDecimal couponCost;
    
    /**
     * ROI = 带动营收 / 优惠券成本
     */
    @TableField("roi")
    private BigDecimal roi;
    
    /**
     * 拉新成本(单个新客)
     */
    @TableField("customer_acquisition_cost")
    private BigDecimal customerAcquisitionCost;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
