package com.im.local.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 骑手上报位置请求
 */
@Data
public class RiderLocationUploadRequest {
    
    /** 骑手ID */
    private Long riderId;
    
    /** 当前配送订单ID */
    private Long deliveryOrderId;
    
    /** 纬度 */
    private BigDecimal lat;
    
    /** 经度 */
    private BigDecimal lng;
    
    /** 精度(米) */
    private Double accuracy;
    
    /** 海拔(米) */
    private Double altitude;
    
    /** 速度(m/s) */
    private Double speed;
    
    /** 方向(0-360度) */
    private Double direction;
    
    /** 定位类型：1-GPS, 2-网络, 3-基站 */
    private Integer locationType;
    
    /** 电池电量(%) */
    private Integer batteryLevel;
}
