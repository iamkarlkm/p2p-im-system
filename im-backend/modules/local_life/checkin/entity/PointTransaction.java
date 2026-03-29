package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分交易记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("point_transaction")
public class PointTransaction extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型: EARN-获得, USE-使用
     */
    private String transactionType;

    /**
     * 积分变动数量(正数增加,负数减少)
     */
    private Integer points;

    /**
     * 变动前余额
     */
    private Integer balanceBefore;

    /**
     * 变动后余额
     */
    private Integer balanceAfter;

    /**
     * 积分类型
     */
    private String pointType;

    /**
     * 关联业务ID(如签到ID)
     */
    private Long businessId;

    /**
     * 关联业务类型
     */
    private String businessType;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
