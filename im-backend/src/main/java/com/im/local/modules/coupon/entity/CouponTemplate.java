package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体类
 * 用于批量生成优惠券
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_coupon_template")
public class CouponTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板类型: 1-满减券 2-折扣券 3-无门槛券 4-兑换券
     */
    private Integer type;

    /**
     * 优惠券面值
     */
    private BigDecimal value;

    /**
     * 使用门槛
     */
    private BigDecimal minSpend;

    /**
     * 最高抵扣金额
     */
    private BigDecimal maxDiscount;

    /**
     * 有效期类型: 1-固定日期 2-领取后生效
     */
    private Integer validityType;

    /**
     * 固定开始日期
     */
    private LocalDateTime fixedStartTime;

    /**
     * 固定结束日期
     */
    private LocalDateTime fixedEndTime;

    /**
     * 领取后生效天数
     */
    private Integer validDaysAfterReceive;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;

    /**
     * 总发放数量上限
     */
    private Integer maxQuantity;

    /**
     * 使用范围类型
     */
    private Integer scopeType;

    /**
     * 是否仅限新用户
     */
    private Boolean newUserOnly;

    /**
     * 是否可叠加
     */
    private Boolean stackable;

    /**
     * 模板状态: 0-草稿 1-已发布 2-已停用
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
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
