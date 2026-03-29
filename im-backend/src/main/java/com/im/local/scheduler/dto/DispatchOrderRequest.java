package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.math.BigDecimal;

/**
 * 智能派单请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchOrderRequest {
    
    /** 订单ID列表 */
    private List<Long> orderIds;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 取货点经度 */
    private BigDecimal pickupLng;
    
    /** 取货点纬度 */
    private BigDecimal pickupLat;
    
    /** 配送点列表 */
    private List<DeliveryPoint> deliveryPoints;
    
    /** 调度策略: 1-最近优先 2-负载均衡 3-运力优先 4-评分优先 5-智能调度 */
    private Integer strategy;
    
    /** 期望分配骑手ID(可选) */
    private Long preferredStaffId;
    
    /** 是否允许批量聚合 */
    private Boolean allowBatch;
    
    @Data
    @Builder
    public static class DeliveryPoint {
        private Long orderId;
        private BigDecimal lng;
        private BigDecimal lat;
        private String address;
    }
}
