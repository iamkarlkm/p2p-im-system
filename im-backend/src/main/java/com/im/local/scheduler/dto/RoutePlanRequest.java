package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 路径规划请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutePlanRequest {
    
    /** 批次ID */
    private Long batchId;
    
    /** 骑手ID */
    private Long staffId;
    
    /** 起点经度 */
    private BigDecimal startLng;
    
    /** 起点纬度 */
    private BigDecimal startLat;
    
    /** 配送点列表(经度,纬度,订单ID) */
    private List<DeliveryPoint> points;
    
    /** 优化算法: 1-贪心 2-蚁群 3-遗传 4-模拟退火 */
    private Integer algorithm;
    
    @Data
    @Builder
    public static class DeliveryPoint {
        private Long orderId;
        private BigDecimal lng;
        private BigDecimal lat;
        private String address;
    }
}
