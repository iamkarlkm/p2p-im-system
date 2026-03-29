package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 骑手/服务人员实体
 * 基于地理围栏的智能调度系统
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStaff {
    
    /** 骑手ID */
    private Long staffId;
    
    /** 骑手姓名 */
    private String staffName;
    
    /** 手机号 */
    private String phone;
    
    /** 骑手类型: 1-专职骑手 2-兼职骑手 3-众包骑手 */
    private Integer staffType;
    
    /** 当前状态: 0-离线 1-空闲 2-取餐中 3-配送中 4-休息中 */
    private Integer status;
    
    /** 当前经度 */
    private BigDecimal currentLng;
    
    /** 当前纬度 */
    private BigDecimal currentLat;
    
    /** 当前位置GeoHash */
    private String currentGeohash;
    
    /** 当前所在围栏ID */
    private Long currentGeofenceId;
    
    /** 位置更新时间 */
    private LocalDateTime locationUpdatedAt;
    
    /** 今日已完成订单数 */
    private Integer todayCompletedOrders;
    
    /** 今日配送距离(米) */
    private Integer todayDeliveryDistance;
    
    /** 平均配送时长(分钟) */
    private Integer avgDeliveryTime;
    
    /** 评分 */
    private BigDecimal rating;
    
    /** 当前携带订单数 */
    private Integer currentOrderCount;
    
    /** 最大接单量 */
    private Integer maxOrderCapacity;
    
    /** 所属配送区域ID */
    private Long deliveryAreaId;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
