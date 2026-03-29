package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 轨迹上传请求
 */
@Data
public class TrajectoryUploadRequest {
    
    @NotNull
    private Long riderId;
    
    private Long taskId;
    
    @NotNull
    private BigDecimal latitude;
    
    @NotNull
    private BigDecimal longitude;
    
    private BigDecimal accuracy;
    
    private BigDecimal altitude;
    
    private BigDecimal speed;
    
    private BigDecimal direction;
    
    private Integer locationType;
    
    private String deviceInfo;
    
    private Integer batteryLevel;
    
    private String networkType;
    
    private Long timestamp;
}
