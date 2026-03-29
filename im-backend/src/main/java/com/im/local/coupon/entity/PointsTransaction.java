package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 积分流水记录实体
 * 记录用户积分的获取和消耗明细
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransaction {

    /** 流水ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    private Long merchantId;

    /** 变动积分(正数为增加,负数为减少) */
    private Integer points;

    /** 变动前余额 */
    private Integer balanceBefore;

    /** 变动后余额 */
    private Integer balanceAfter;

    /** 变动类型: 1-消费获得 2-签到获得 3-任务获得 4-兑换消耗 5-过期清零 */
    private Integer changeType;

    /** 变动来源: 1-订单 2-签到 3-任务 4-兑换 5-系统调整 */
    private Integer sourceType;

    /** 来源ID(订单ID/任务ID等) */
    private Long sourceId;

    /** 来源描述 */
    private String sourceDesc;

    /** 关联订单号 */
    private String orderNo;

    /** 关联订单金额 */
    private BigDecimal orderAmount;

    /** 过期时间(积分有效期) */
    private LocalDateTime expireTime;

    /** 变动时间 */
    private LocalDateTime createTime;

    /** 备注 */
    private String remark;
}
