package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * 派单结果响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResultResponse {
    
    /** 批次ID */
    private Long batchId;
    
    /** 批次号 */
    private String batchNo;
    
    /** 分配的骑手ID */
    private Long staffId;
    
    /** 骑手姓名 */
    private String staffName;
    
    /** 骑手手机号 */
    private String staffPhone;
    
    /** 订单ID列表 */
    private List<Long> orderIds;
    
    /** 订单数量 */
    private Integer orderCount;
    
    /** 预计总距离(米) */
    private Integer estimatedTotalDistance;
    
    /** 预计总时长(分钟) */
    private Integer estimatedTotalTime;
    
    /** 最优路径序列 */
    private List<RouteNode> optimalRoute;
    
    /** 分配时间 */
    private LocalDateTime assignedAt;
    
    /** 调度成功 */
    private Boolean success;
    
    /** 消息 */
    private String message;
    
    @Data
    @Builder
    public static class RouteNode {
        private Integer sequence;
        private Long orderId;
        private String address;
        private BigDecimal lng;
        private BigDecimal lat;
        private String action;
        private Integer estimatedArrivalMinutes;
    }
}
