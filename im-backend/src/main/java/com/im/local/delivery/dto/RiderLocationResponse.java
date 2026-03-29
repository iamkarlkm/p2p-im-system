package com.im.local.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置轨迹点响应
 */
@Data
public class RiderLocationResponse {
    
    /** 纬度 */
    private BigDecimal lat;
    
    /** 经度 */
    private BigDecimal lng;
    
    /** 速度(m/s) */
    private Double speed;
    
    /** 方向 */
    private Double direction;
    
    /** 定位时间 */
    private LocalDateTime locatedAt;
    
    /** 地址描述 */
    private String address;
}
