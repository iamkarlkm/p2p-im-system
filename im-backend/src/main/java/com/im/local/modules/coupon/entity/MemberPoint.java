package com.im.local.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 会员积分实体类
 * 记录用户积分变动
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_member_point")
public class MemberPoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 积分变动数量（正数为增加，负数为减少）
     */
    private Integer points;

    /**
     * 变动后余额
     */
    private Integer balance;

    /**
     * 变动类型: 1-签到获得 2-消费获得 3-评价获得 4-分享获得 
     * 5-兑换优惠券 6-兑换商品 7-过期清零 8-系统调整
     */
    private Integer changeType;

    /**
     * 变动描述
     */
    private String description;

    /**
     * 关联业务ID
     */
    private Long bizId;

    /**
     * 关联业务类型
     */
    private String bizType;

    /**
     * 商户ID（平台积分时为null）
     */
    private Long merchantId;

    /**
     * 是否过期（针对有有效期的积分）
     */
    private Boolean expired;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
