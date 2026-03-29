package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 骑手位置上报请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStaffLocationRequest {
    
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
    
    /** 位置来源: 1-GPS 2-WiFi 3-基站 4-混合 */
    private Integer sourceType;
}
