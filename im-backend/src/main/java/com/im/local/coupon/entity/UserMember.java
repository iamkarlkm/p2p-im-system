package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户会员信息实体
 * 记录用户在商户的会员状态和积分信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMember {

    /** 会员记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 商户ID(0表示平台会员) */
    private Long merchantId;

    /** 当前等级ID */
    private Long currentLevelId;

    /** 当前等级级别 */
    private Integer currentLevel;

    /** 会员成长值 */
    private Integer growthValue;

    /** 会员积分余额 */
    private Integer pointsBalance;

    /** 累计获得积分 */
    private Integer totalPoints;

    /** 累计消费金额 */
    private BigDecimal totalSpend;

    /** 累计订单数量 */
    private Integer totalOrders;

    /** 会员状态: 0-冻结 1-正常 2-过期 */
    private Integer status;

    /** 入会时间 */
    private LocalDateTime joinTime;

    /** 到期时间(年卡等需要) */
    private LocalDate expireDate;

    /** 最近消费时间 */
    private LocalDateTime lastConsumeTime;

    /** 最近消费金额 */
    private BigDecimal lastConsumeAmount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
