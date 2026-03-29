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
 * 商户经营统计数据实体
 * 记录商户每日/时段的经营核心指标
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_business_stats")
public class MerchantBusinessStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 统计日期 */
    private LocalDate statsDate;

    /** 统计时段 (0-23, -1表示全天) */
    private Integer statsHour;

    /** 营业额 */
    private BigDecimal revenue;

    /** 订单数量 */
    private Integer orderCount;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 客单价 */
    private BigDecimal avgOrderValue;

    /** 到店客流量 */
    private Integer visitorCount;

    /** 新增顾客数 */
    private Integer newCustomerCount;

    /** 老顾客数 */
    private Integer returningCustomerCount;

    /** 收藏/关注数 */
    private Integer favoriteCount;

    /** 支付订单数 */
    private Integer paidOrderCount;

    /** 取消订单数 */
    private Integer cancelledOrderCount;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 优惠券使用数量 */
    private Integer couponUsedCount;

    /** 优惠券抵扣金额 */
    private BigDecimal couponDiscountAmount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
