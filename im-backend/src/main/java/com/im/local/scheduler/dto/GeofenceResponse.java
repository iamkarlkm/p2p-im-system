package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 围栏信息响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofenceResponse {
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 围栏名称 */
    private String name;
    
    /** 围栏类型 */
    private String type;
    
    /** 圆心经度 */
    private BigDecimal centerLng;
    
    /** 圆心纬度 */
    private BigDecimal centerLat;
    
    /** 当前半径(米) */
    private Integer currentRadius;
    
    /** 基础半径(米) */
    private Integer baseRadius;
    
    /** 当前订单数 */
    private Integer currentOrderCount;
    
    /** 当前骑手数 */
    private Integer currentStaffCount;
    
    /** 运力饱和度 */
    private Integer saturationRate;
    
    /** 饱和度等级 */
    private String saturationLevel;
    
    /** 是否动态调整 */
    private Boolean dynamicAdjustEnabled;
    
    /** 状态 */
    private String status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
}
