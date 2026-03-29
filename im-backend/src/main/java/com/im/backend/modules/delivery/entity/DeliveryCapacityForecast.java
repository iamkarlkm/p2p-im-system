package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运力预测与调度策略实体
 * 本地物流配送智能调度引擎 - 运力预测
 */
@Data
@Accessors(chain = true)
@TableName("delivery_capacity_forecast")
public class DeliveryCapacityForecast {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 站点ID */
    private Long stationId;

    /** 预测日期 */
    private String forecastDate;

    /** 时段: 08:00-10:00 格式 */
    private String timeSlot;

    /** 预测订单量 */
    private Integer predictedOrders;

    /** 实际订单量 */
    private Integer actualOrders;

    /** 预测准确率 */
    private BigDecimal accuracy;

    /** 建议骑手数 */
    private Integer suggestedRiders;

    /** 实际在线骑手数 */
    private Integer actualRiders;

    /** 预计平均配送时长(分钟) */
    private Integer estimatedAvgTime;

    /** 预测类型: ML-机器学习, RULE-规则预测, MANUAL-人工调整 */
    private String forecastType;

    /** 天气因素: SUNNY-晴天, RAINY-雨天, SNOWY-雪天 */
    private String weather;

    /** 是否节假日 */
    private Boolean isHoliday;

    /** 置信度(0-1) */
    private BigDecimal confidence;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
