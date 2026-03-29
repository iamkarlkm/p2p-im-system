package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家实时经营数据快照
 * 用于实时数据看板展示
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_realtime_metrics")
public class MerchantRealtimeMetrics {
    
    @TableId(type = IdType.INPUT)
    private Long merchantId;
    
    /**
     * 今日营业额
     */
    @TableField("today_revenue")
    private BigDecimal todayRevenue;
    
    /**
     * 今日订单数
     */
    @TableField("today_orders")
    private Integer todayOrders;
    
    /**
     * 当前在线支付金额
     */
    @TableField("current_online_revenue")
    private BigDecimal currentOnlineRevenue;
    
    /**
     * 当前在线订单数
     */
    @TableField("current_online_orders")
    private Integer currentOnlineOrders;
    
    /**
     * 当前到店客流量
     */
    @TableField("current_foot_traffic")
    private Integer currentFootTraffic;
    
    /**
     * 正在用餐人数
     */
    @TableField("dining_customers")
    private Integer diningCustomers;
    
    /**
     * 排队等待人数
     */
    @TableField("waiting_customers")
    private Integer waitingCustomers;
    
    /**
     * 平均等位时间(分钟)
     */
    @TableField("avg_wait_time")
    private Integer avgWaitTime;
    
    /**
     * 翻台率(次/日)
     */
    @TableField("turnover_rate")
    private BigDecimal turnoverRate;
    
    /**
     * 座位利用率(%)
     */
    @TableField("seat_utilization")
    private BigDecimal seatUtilization;
    
    /**
     * 今日新客数
     */
    @TableField("today_new_customers")
    private Integer todayNewCustomers;
    
    /**
     * 今日老客数
     */
    @TableField("today_returning_customers")
    private Integer todayReturningCustomers;
    
    /**
     * 实时评分
     */
    @TableField("realtime_rating")
    private BigDecimal realtimeRating;
    
    /**
     * 今日新增评价数
     */
    @TableField("today_reviews")
    private Integer todayReviews;
    
    /**
     * 今日好评数
     */
    @TableField("today_positive_reviews")
    private Integer todayPositiveReviews;
    
    /**
     * 今日差评数
     */
    @TableField("today_negative_reviews")
    private Integer todayNegativeReviews;
    
    /**
     * 数据更新时间
     */
    @TableField("metrics_updated_at")
    private LocalDateTime metricsUpdatedAt;
    
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
