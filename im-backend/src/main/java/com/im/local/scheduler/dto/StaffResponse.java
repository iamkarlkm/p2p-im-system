package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 骑手信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponse {
    
    /** 骑手ID */
    private Long staffId;
    
    /** 骑手姓名 */
    private String staffName;
    
    /** 手机号 */
    private String phone;
    
    /** 骑手类型 */
    private String staffType;
    
    /** 当前状态 */
    private String status;
    
    /** 当前经度 */
    private BigDecimal currentLng;
    
    /** 当前纬度 */
    private BigDecimal currentLat;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdatedAt;
    
    /** 今日已完成订单数 */
    private Integer todayCompletedOrders;
    
    /** 平均配送时长 */
    private Integer avgDeliveryTime;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 当前携带订单数 */
    private Integer currentOrderCount;
    
    /** 最大接单量 */
    private Integer maxOrderCapacity;
    
    /** 剩余接单容量 */
    private Integer remainingCapacity;
}
