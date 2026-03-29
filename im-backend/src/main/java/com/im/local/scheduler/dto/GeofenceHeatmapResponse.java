package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * 围栏热力图响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofenceHeatmapResponse {
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 围栏名称 */
    private String name;
    
    /** 骑手分布热力点 */
    private List<HeatPoint> staffHeatPoints;
    
    /** 订单分布热力点 */
    private List<HeatPoint> orderHeatPoints;
    
    /** 总骑手数 */
    private Integer totalStaff;
    
    /** 空闲骑手数 */
    private Integer idleStaff;
    
    /** 总订单数 */
    private Integer totalOrders;
    
    /** 待分配订单数 */
    private Integer pendingOrders;
    
    /** 运力饱和度 */
    private Integer saturationRate;
    
    /** 数据更新时间 */
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    public static class HeatPoint {
        private BigDecimal lng;
        private BigDecimal lat;
        private Integer count;
        private BigDecimal weight;
    }
}
