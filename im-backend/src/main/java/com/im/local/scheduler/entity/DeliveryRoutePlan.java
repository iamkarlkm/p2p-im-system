package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 配送路径规划实体
 * TSP最短路径优化
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRoutePlan {
    
    /** 路径ID */
    private Long routeId;
    
    /** 批次ID */
    private Long batchId;
    
    /** 骑手ID */
    private Long staffId;
    
    /** 起点经度 */
    private BigDecimal startLng;
    
    /** 起点纬度 */
    private BigDecimal startLat;
    
    /** 路径点序列(JSON) */
    private String routePoints;
    
    /** 总距离(米) */
    private Integer totalDistance;
    
    /** 预计总时长(分钟) */
    private Integer estimatedDuration;
    
    /** 途经订单数 */
    private Integer orderCount;
    
    /** 优化算法: 1-贪心 2-蚁群 3-遗传 4-模拟退火 */
    private Integer algorithm;
    
    /** 路径状态: 0-规划中 1-已优化 2-执行中 3-已完成 */
    private Integer status;
    
    /** 实际执行路径 */
    private String actualRoute;
    
    /** 实际距离(米) */
    private Integer actualDistance;
    
    /** 实际时长(分钟) */
    private Integer actualDuration;
    
    /** 路径效率评分 */
    private BigDecimal efficiencyScore;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
