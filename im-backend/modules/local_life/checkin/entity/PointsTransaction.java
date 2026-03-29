package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分交易记录实体
 */
@Data
@TableName("points_transaction")
public class PointsTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型: EARN-获得, SPEND-消费, EXPIRE-过期
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
     * 来源类型: CHECKIN-签到, TASK-任务, EXCHANGE-兑换, ACTIVITY-活动
     */
    private String sourceType;

    /**
     * 来源ID(关联业务ID)
     */
    private String sourceId;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 过期时间(积分有效期)
     */
    private LocalDateTime expireAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
