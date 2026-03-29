package com.im.backend.modules.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 骑手位置上报请求
 */
@Data
public class RiderLocationUploadRequest {
    private Long riderId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String location;
    
    private BigDecimal accuracy;
    private BigDecimal altitude;
    private BigDecimal speed;
    private BigDecimal bearing;
    private Integer batteryLevel;
}
