package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 围栏运力需求预测实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofenceDemandForecast {
    
    /** 预测ID */
    private Long forecastId;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 预测日期 */
    private String forecastDate;
    
    /** 预测时段(小时: 0-23) */
    private Integer forecastHour;
    
    /** 预测订单量 */
    private Integer predictedOrders;
    
    /** 实际需求订单量 */
    private Integer actualOrders;
    
    /** 预测骑手需求数 */
    private Integer predictedStaffNeed;
    
    /** 建议预调度骑手数 */
    private Integer suggestedPreDispatch;
    
    /** 预测准确率 */
    private BigDecimal accuracy;
    
    /** 置信度 */
    private BigDecimal confidence;
    
    /** 预测模型版本 */
    private String modelVersion;
    
    /** 预测时间 */
    private LocalDateTime forecastTime;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
