package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏历史记录VO
 */
@Data
@Schema(description = "围栏历史记录")
public class GeofenceHistoryVO {
    
    @Schema(description = "事件ID")
    private String eventId;
    
    @Schema(description = "围栏ID")
    private Long geofenceId;
    
    @Schema(description = "围栏名称")
    private String geofenceName;
    
    @Schema(description = "商户名称")
    private String merchantName;
    
    @Schema(description = "事件类型")
    private String eventType;
    
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;
    
    @Schema(description = "停留时长（分钟）")
    private Integer dwellDuration;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "消息已发送")
    private Boolean messageSent;
    
    @Schema(description = "消息类型")
    private String messageType;
}
