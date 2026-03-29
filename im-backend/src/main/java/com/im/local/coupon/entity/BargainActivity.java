package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 砍价活动实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BargainActivity {

    /** 砍价ID */
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 发起用户ID */
    private Long userId;

    /** 商品原价 */
    private BigDecimal originalPrice;

    /** 底价 */
    private BigDecimal floorPrice;

    /** 当前价格 */
    private BigDecimal currentPrice;

    /** 已砍金额 */
    private BigDecimal bargainedAmount;

    /** 砍价状态: 0-砍价中 1-砍价成功 2-砍价失败 3-已购买 */
    private Integer status;

    /** 帮砍人数 */
    private Integer helperCount;

    /** 有效期(小时) */
    private Integer validHours;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 截止时间 */
    private LocalDateTime expireTime;

    /** 购买截止时间 */
    private LocalDateTime purchaseDeadline;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
