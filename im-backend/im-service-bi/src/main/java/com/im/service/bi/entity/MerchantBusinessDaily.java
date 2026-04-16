package com.im.service.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商户经营日报实体
 * 存储每日经营统计数据
 */
@Data
@TableName("merchant_business_daily")
public class MerchantBusinessDaily {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 统计日期 */
    private LocalDate statDate;

    /** 营业额 */
    private BigDecimal revenue;

    /** 订单数量 */
    private Integer orderCount;

    /** 客流人数 */
    private Integer customerCount;

    /** 客单价 */
    private BigDecimal avgOrderValue;

    /** 新客数量 */
    private Integer newCustomerCount;

    /** 老客数量 */
    private Integer oldCustomerCount;

    /** 优惠券使用量 */
    private Integer couponUsedCount;

    /** 优惠券核销金额 */
    private BigDecimal couponUsedAmount;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
