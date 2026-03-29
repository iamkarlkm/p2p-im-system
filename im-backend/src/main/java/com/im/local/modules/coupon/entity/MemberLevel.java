package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 会员等级实体类
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_member_level")
public class MemberLevel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID（平台会员等级时为null）
     */
    private Long merchantId;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级编码
     */
    private String code;

    /**
     * 等级排序
     */
    private Integer levelOrder;

    /**
     * 等级图标
     */
    private String icon;

    /**
     * 所需成长值
     */
    private Integer requiredGrowth;

    /**
     * 等级权益（JSON格式）
     */
    private String benefits;

    /**
     * 消费折扣比例
     */
    private Double discountRate;

    /**
     * 生日倍数
     */
    private Integer birthdayMultiplier;

    /**
     * 是否包邮
     */
    private Boolean freeShipping;

    /**
     * 专属客服
     */
    private Boolean exclusiveService;

    /**
     * 优先发货
     */
    private Boolean priorityShipping;

    /**
     * 等级描述
     */
    private String description;

    /**
     * 状态: 0-禁用 1-启用
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
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
