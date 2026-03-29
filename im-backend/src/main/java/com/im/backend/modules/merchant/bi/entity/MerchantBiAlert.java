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
 * 商家预警规则与触发记录实体
 * 用于关键指标异常预警
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_bi_alert")
public class MerchantBiAlert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;
    
    /**
     * 预警类型
     */
    @TableField("alert_type")
    private String alertType;
    
    /**
     * 预警级别: info/warning/critical
     */
    @TableField("alert_level")
    private String alertLevel;
    
    /**
     * 预警标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 预警内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 触发日期
     */
    @TableField("trigger_date")
    private LocalDate triggerDate;
    
    /**
     * 触发时间
     */
    @TableField("trigger_time")
    private LocalDateTime triggerTime;
    
    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;
    
    /**
     * 当前值
     */
    @TableField("current_value")
    private BigDecimal currentValue;
    
    /**
     * 阈值
     */
    @TableField("threshold_value")
    private BigDecimal thresholdValue;
    
    /**
     * 变化率(%)
     */
    @TableField("change_rate")
    private BigDecimal changeRate;
    
    /**
     * 环比/同比类型
     */
    @TableField("compare_type")
    private String compareType;
    
    /**
     * 建议措施
     */
    @TableField("suggestion")
    private String suggestion;
    
    /**
     * 处理状态: pending/processed/ignored
     */
    @TableField("status")
    private String status;
    
    /**
     * 处理时间
     */
    @TableField("process_time")
    private LocalDateTime processTime;
    
    /**
     * 处理备注
     */
    @TableField("process_remark")
    private String processRemark;
    
    /**
     * 是否已读
     */
    @TableField("is_read")
    private Boolean isRead;
    
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
    
    // 预警类型常量
    public static final String TYPE_REVENUE_DROP = "REVENUE_DROP";           // 营收下降
    public static final String TYPE_ORDER_DROP = "ORDER_DROP";               // 订单下降
    public static final String TYPE_RATING_DROP = "RATING_DROP";             // 评分下降
    public static final String TYPE_NEGATIVE_REVIEW_SPIKE = "NEGATIVE_REVIEW_SPIKE"; // 差评激增
    public static final String TYPE_INVENTORY_LOW = "INVENTORY_LOW";         // 库存不足
    public static final String TYPE_PEAK_HOUR_CONGESTION = "PEAK_HOUR_CONGESTION"; // 高峰拥堵
}
