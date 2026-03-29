package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 骑手实时轨迹实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffLocationTrack {
    
    /** 轨迹ID */
    private Long trackId;
    
    /** 骑手ID */
    private Long staffId;
    
    /** 经度 */
    private BigDecimal lng;
    
    /** 纬度 */
    private BigDecimal lat;
    
    /** 精度(米) */
    private BigDecimal accuracy;
    
    /** 海拔高度 */
    private BigDecimal altitude;
    
    /** 速度(m/s) */
    private BigDecimal speed;
    
    /** 方向角 */
    private BigDecimal direction;
    
    /** GeoHash */
    private String geohash;
    
    /** 围栏ID列表(逗号分隔) */
    private String geofenceIds;
    
    /** 位置来源: 1-GPS 2-WiFi 3-基站 4-混合 */
    private Integer sourceType;
    
    /** 上报时间 */
    private LocalDateTime reportTime;
    
    /** 设备时间 */
    private LocalDateTime deviceTime;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}
