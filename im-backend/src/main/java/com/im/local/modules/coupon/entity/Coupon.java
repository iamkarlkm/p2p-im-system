package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 * 支持满减券、折扣券、无门槛券等多种类型
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_coupon")
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券模板ID
     */
    private Long templateId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 优惠券描述
     */
    private String description;

    /**
     * 优惠券类型: 1-满减券 2-折扣券 3-无门槛券 4-兑换券
     */
    private Integer type;

    /**
     * 优惠券面值（满减金额或折扣比例）
     */
    private BigDecimal value;

    /**
     * 使用门槛（满多少可用，0表示无门槛）
     */
    private BigDecimal minSpend;

    /**
     * 最高抵扣金额（折扣券专用）
     */
    private BigDecimal maxDiscount;

    /**
     * 总发放数量
     */
    private Integer totalQuantity;

    /**
     * 已领取数量
     */
    private Integer receivedQuantity;

    /**
     * 已使用数量
     */
    private Integer usedQuantity;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;

    /**
     * 生效时间
     */
    private LocalDateTime startTime;

    /**
     * 过期时间
     */
    private LocalDateTime endTime;

    /**
     * 使用范围: 1-全场通用 2-指定分类 3-指定商品
     */
    private Integer scopeType;

    /**
     * 使用范围IDs（JSON数组）
     */
    private String scopeIds;

    /**
     * 适用商户类型: 1-全部 2-指定商户
     */
    private Integer merchantScope;

    /**
     * 适用商户IDs（JSON数组）
     */
    private String merchantIds;

    /**
     * 是否仅限新用户
     */
    private Boolean newUserOnly;

    /**
     * 是否可叠加使用
     */
    private Boolean stackable;

    /**
     * 状态: 0-未开始 1-进行中 2-已结束 3-已停发
     */
    private Integer status;

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
     * 创建者
     */
    private Long createBy;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    private Integer version;

    // === 业务方法 ===

    /**
     * 检查优惠券是否有效
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 
            && now.isAfter(startTime) 
            && now.isBefore(endTime)
            && !deleted;
    }

    /**
     * 检查是否还有库存
     */
    public boolean hasStock() {
        return receivedQuantity < totalQuantity;
    }

    /**
     * 检查用户是否达到领取上限
     */
    public boolean canReceive(int userReceivedCount) {
        return userReceivedCount < limitPerUser;
    }

    /**
     * 计算优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minSpend) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        switch (type) {
            case 1: // 满减券
                discount = value;
                break;
            case 2: // 折扣券
                discount = orderAmount.multiply(BigDecimal.ONE.subtract(value));
                if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                    discount = maxDiscount;
                }
                break;
            case 3: // 无门槛券
                discount = value.min(orderAmount);
                break;
            default:
                discount = BigDecimal.ZERO;
        }
        return discount;
    }

    /**
     * 增加领取数量
     */
    public void incrementReceived() {
        this.receivedQuantity++;
    }

    /**
     * 增加使用数量
     */
    public void incrementUsed() {
        this.usedQuantity++;
    }
}
