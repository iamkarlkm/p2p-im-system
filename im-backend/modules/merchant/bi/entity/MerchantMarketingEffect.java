package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营销效果追踪数据实体
 * 记录优惠券、活动的营销效果数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_marketing_effect")
public class MerchantMarketingEffect {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 营销类型 (COUPON-优惠券/ACTIVITY-活动) */
    private String marketingType;

    /** 营销ID (优惠券ID或活动ID) */
    private Long marketingId;

    /** 营销名称 */
    private String marketingName;

    /** 统计日期 */
    private LocalDate statsDate;

    /** 曝光次数 */
    private Integer exposureCount;

    /** 领取次数 */
    private Integer receiveCount;

    /** 使用次数 */
    private Integer useCount;

    /** 领取率 */
    private BigDecimal receiveRate;

    /** 使用率 */
    private BigDecimal useRate;

    /** 转化订单数 */
    private Integer conversionOrderCount;

    /** 转化订单金额 */
    private BigDecimal conversionOrderAmount;

    /** 营销成本 */
    private BigDecimal marketingCost;

    /** 营销收益 */
    private BigDecimal marketingRevenue;

    /** ROI */
    private BigDecimal roi;

    /** 拉新用户数 */
    private Integer newUserCount;

    /** 拉新成本 */
    private BigDecimal newUserCost;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
