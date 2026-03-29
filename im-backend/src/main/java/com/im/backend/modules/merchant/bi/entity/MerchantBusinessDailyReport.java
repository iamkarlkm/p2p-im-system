package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商家经营日报表实体
 * 记录商户每日核心经营指标
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_business_daily_report")
public class MerchantBusinessDailyReport {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;
    
    /**
     * 统计日期
     */
    @TableField("report_date")
    private LocalDate reportDate;
    
    /**
     * 营业总额
     */
    @TableField("total_revenue")
    private BigDecimal totalRevenue;
    
    /**
     * 订单总数
     */
    @TableField("total_orders")
    private Integer totalOrders;
    
    /**
     * 有效订单数
     */
    @TableField("valid_orders")
    private Integer validOrders;
    
    /**
     * 取消订单数
     */
    @TableField("cancelled_orders")
    private Integer cancelledOrders;
    
    /**
     * 退款订单数
     */
    @TableField("refunded_orders")
    private Integer refundedOrders;
    
    /**
     * 退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;
    
    /**
     * 客单价
     */
    @TableField("average_order_value")
    private BigDecimal averageOrderValue;
    
    /**
     * 到店客流量
     */
    @TableField("foot_traffic")
    private Integer footTraffic;
    
    /**
     * 新客数量
     */
    @TableField("new_customers")
    private Integer newCustomers;
    
    /**
     * 老客数量
     */
    @TableField("returning_customers")
    private Integer returningCustomers;
    
    /**
     * 老客复购率(%)
     */
    @TableField("returning_rate")
    private BigDecimal returningRate;
    
    /**
     * 支付订单数 - 微信支付
     */
    @TableField("wxpay_orders")
    private Integer wxpayOrders;
    
    /**
     * 支付订单数 - 支付宝
     */
    @TableField("alipay_orders")
    private Integer alipayOrders;
    
    /**
     * 支付订单数 - 其他
     */
    @TableField("other_pay_orders")
    private Integer otherPayOrders;
    
    /**
     * 营业时间(分钟)
     */
    @TableField("business_duration")
    private Integer businessDuration;
    
    /**
     * 高峰时段开始
     */
    @TableField("peak_start_hour")
    private Integer peakStartHour;
    
    /**
     * 高峰时段结束
     */
    @TableField("peak_end_hour")
    private Integer peakEndHour;
    
    /**
     * 高峰时段订单占比(%)
     */
    @TableField("peak_order_ratio")
    private BigDecimal peakOrderRatio;
    
    /**
     * 环比增长率 - 营业额
     */
    @TableField("revenue_growth_rate")
    private BigDecimal revenueGrowthRate;
    
    /**
     * 同比增长率 - 营业额
     */
    @TableField("revenue_yoy_rate")
    private BigDecimal revenueYoyRate;
    
    /**
     * 商圈排名
     */
    @TableField("district_rank")
    private Integer districtRank;
    
    /**
     * 同品类排名
     */
    @TableField("category_rank")
    private Integer categoryRank;
    
    /**
     * 数据版本
     */
    @Version
    @TableField("version")
    private Integer version;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
