package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动参与记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipation {

    /** 参与记录ID */
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 用户ID */
    private Long userId;

    /** 参与类型: 1-秒杀 2-拼团 3-砍价 */
    private Integer participateType;

    /** 参与状态: 0-进行中 1-成功 2-失败 3-取消 */
    private Integer status;

    /** 原始金额 */
    private BigDecimal originalAmount;

    /** 活动优惠金额 */
    private BigDecimal discountAmount;

    /** 实付金额 */
    private BigDecimal finalAmount;

    /** 购买数量 */
    private Integer quantity;

    /** 拼团ID(拼团活动使用) */
    private Long groupId;

    /** 砍价ID(砍价活动使用) */
    private Long bargainId;

    /** 邀请人ID(被邀请参与) */
    private Long inviterId;

    /** 参与时间 */
    private LocalDateTime participateTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 订单ID */
    private Long orderId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
