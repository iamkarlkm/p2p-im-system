package com.im.backend.modules.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时经营指标实体
 * 存储实时经营数据（用于大屏展示）
 */
@Data
@TableName("realtime_business_metrics")
public class RealtimeBusinessMetrics {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 今日营业额 */
    private BigDecimal todayRevenue;

    /** 今日订单数 */
    private Integer todayOrderCount;

    /** 今日客流 */
    private Integer todayCustomerCount;

    /** 当前在线人数 */
    private Integer currentOnlineCount;

    /** 待处理订单数 */
    private Integer pendingOrderCount;

    /** 退款中订单数 */
    private Integer refundingOrderCount;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 新客占比 */
    private BigDecimal newCustomerRatio;

    /** 老客占比 */
    private BigDecimal oldCustomerRatio;

    /** 峰值时段 */
    private String peakHours;

    /** 环比昨日 */
    private BigDecimal revenueMom;

    /** 数据更新时间 */
    private LocalDateTime metricsUpdatedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
