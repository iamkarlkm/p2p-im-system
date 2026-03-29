package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼团活动实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuying {

    /** 拼团ID */
    private Long id;

    /** 活动ID */
    private Long activityId;

    /** 团长用户ID */
    private Long leaderId;

    /** 成团人数要求 */
    private Integer requiredMembers;

    /** 当前参与人数 */
    private Integer currentMembers;

    /** 拼团状态: 0-拼团中 1-成团成功 2-成团失败 */
    private Integer status;

    /** 拼团有效期(小时) */
    private Integer validHours;

    /** 拼团开始时间 */
    private LocalDateTime startTime;

    /** 拼团截止时间 */
    private LocalDateTime expireTime;

    /** 拼团价格 */
    private BigDecimal groupPrice;

    /** 商品ID */
    private Long productId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
