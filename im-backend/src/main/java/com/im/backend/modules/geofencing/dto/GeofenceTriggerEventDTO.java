package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 围栏触发事件DTO
 */
@Data
@Schema(description = "围栏触发事件")
public class GeofenceTriggerEventDTO {
    
    @Schema(description = "事件ID")
    private String eventId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "围栏ID")
    private Long geofenceId;
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "事件类型")
    private String eventType;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "停留时长")
    private Integer dwellMinutes;
    
    @Schema(description = "会话ID")
    private String sessionId;
}
