package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * 商户经营日报实体 - 功能#312: 商家BI数据智能平台
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_bi_daily_report")
public class MerchantBiDailyReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 统计日期 */
    private LocalDate reportDate;

    /** 订单数 */
    private Integer orderCount;

    /** 订单金额 */
    private BigDecimal orderAmount;

    /** 实收金额 */
    private BigDecimal actualAmount;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 访客数 */
    private Integer visitorCount;

    /** 下单转化率 */
    private BigDecimal conversionRate;

    /** 客单价 */
    private BigDecimal avgOrderValue;

    /** 新客数 */
    private Integer newCustomerCount;

    /** 老客数 */
    private Integer oldCustomerCount;

    /** 好评数 */
    private Integer goodReviewCount;

    /** 差评数 */
    private Integer badReviewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createTime;
}
