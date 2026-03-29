package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * 配送订单聚合实体
 * 围栏内订单聚合与批量派单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOrderBatch {
    
    /** 批次ID */
    private Long batchId;
    
    /** 批次号 */
    private String batchNo;
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 围栏名称 */
    private String geofenceName;
    
    /** 骑手ID */
    private Long staffId;
    
    /** 订单ID列表 */
    private List<Long> orderIds;
    
    /** 订单数量 */
    private Integer orderCount;
    
    /** 预计总距离(米) */
    private Integer estimatedTotalDistance;
    
    /** 预计总时长(分钟) */
    private Integer estimatedTotalTime;
    
    /** 最优路径序列 */
    private String optimalRoute;
    
    /** 批次状态: 0-待分配 1-已分配 2-取餐中 3-配送中 4-已完成 5-已取消 */
    private Integer status;
    
    /** 分配时间 */
    private LocalDateTime assignedAt;
    
    /** 开始配送时间 */
    private LocalDateTime startedAt;
    
    /** 完成时间 */
    private LocalDateTime completedAt;
    
    /** 实际配送距离(米) */
    private Integer actualDistance;
    
    /** 实际配送时长(分钟) */
    private Integer actualTime;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
