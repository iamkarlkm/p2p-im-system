package com.im.local.coupon.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户会员信息响应DTO
 */
@Data
public class UserMemberResponse {

    /** 会员记录ID */
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 商户名称 */
    private String merchantName;

    /** 当前等级ID */
    private Long currentLevelId;

    /** 当前等级级别 */
    private Integer currentLevel;

    /** 等级名称 */
    private String levelName;

    /** 等级图标 */
    private String levelIcon;

    /** 会员成长值 */
    private Integer growthValue;

    /** 升级还需成长值 */
    private Integer needGrowth;

    /** 升级进度百分比 */
    private Integer progressPercent;

    /** 会员积分余额 */
    private Integer pointsBalance;

    /** 累计获得积分 */
    private Integer totalPoints;

    /** 累计消费金额 */
    private BigDecimal totalSpend;

    /** 累计订单数量 */
    private Integer totalOrders;

    /** 入会时间 */
    private LocalDateTime joinTime;

    /** 最近消费时间 */
    private LocalDateTime lastConsumeTime;

    /** 是否今日已签到 */
    private Boolean todaySigned;
}
