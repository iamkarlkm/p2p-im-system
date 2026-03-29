package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏事件VO
 */
@Data
@Schema(description = "围栏触发事件")
public class GeofenceEventVO {
    
    @Schema(description = "事件ID")
    private String eventId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "围栏ID")
    private Long geofenceId;
    
    @Schema(description = "围栏名称")
    private String geofenceName;
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "商户名称")
    private String merchantName;
    
    @Schema(description = "事件类型: ENTER-进入, EXIT-离开, DWELL-停留")
    private String eventType;
    
    @Schema(description = "触发时间")
    private LocalDateTime triggerTime;
    
    @Schema(description = "触发位置经度")
    private BigDecimal longitude;
    
    @Schema(description = "触发位置纬度")
    private BigDecimal latitude;
    
    @Schema(description = "停留时长（分钟）")
    private Integer dwellMinutes;
    
    @Schema(description = "会话ID")
    private String sessionId;
    
    @Schema(description = "置信度评分")
    private Integer confidenceScore;
    
    @Schema(description = "是否发送消息")
    private Boolean messageSent;
    
    @Schema(description = "关联消息ID")
    private Long messageId;
}
