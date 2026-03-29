package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 * 记录用户领取的优惠券
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_coupon")
public class UserCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券模板ID
     */
    private Long templateId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型
     */
    private Integer couponType;

    /**
     * 优惠券面值
     */
    private BigDecimal couponValue;

    /**
     * 使用门槛
     */
    private BigDecimal minSpend;

    /**
     * 最高抵扣金额
     */
    private BigDecimal maxDiscount;

    /**
     * 有效期开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 状态: 0-未使用 1-已使用 2-已过期 3-已作废
     */
    private Integer status;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用的订单ID
     */
    private Long orderId;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 实际优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 领取时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime receiveTime;

    /**
     * 领取渠道: 1-主动领取 2-系统发放 3-活动赠送 4-分享获得
     */
    private Integer receiveChannel;

    /**
     * 领取来源用户ID（分享获得时）
     */
    private Long sourceUserId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    // === 业务方法 ===

    /**
     * 检查是否可用
     */
    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        return status == 0 
            && now.isAfter(validStartTime) 
            && now.isBefore(validEndTime)
            && !deleted;
    }

    /**
     * 检查是否过期
     */
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(validEndTime);
    }

    /**
     * 标记为已使用
     */
    public void markAsUsed(Long orderId, BigDecimal orderAmount, BigDecimal discountAmount) {
        this.status = 1;
        this.useTime = LocalDateTime.now();
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.discountAmount = discountAmount;
    }

    /**
     * 标记为过期
     */
    public void markAsExpired() {
        this.status = 2;
    }

    /**
     * 检查是否满足使用门槛
     */
    public boolean meetsMinSpend(BigDecimal amount) {
        return amount.compareTo(minSpend) >= 0;
    }

    /**
     * 计算优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (!meetsMinSpend(orderAmount)) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        switch (couponType) {
            case 1: // 满减券
                discount = couponValue;
                break;
            case 2: // 折扣券
                discount = orderAmount.multiply(BigDecimal.ONE.subtract(couponValue));
                if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                    discount = maxDiscount;
                }
                break;
            case 3: // 无门槛券
                discount = couponValue.min(orderAmount);
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        return discount;
    }
}
