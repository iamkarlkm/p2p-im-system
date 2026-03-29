package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户围栏状态VO
 */
@Data
@Schema(description = "用户围栏状态")
public class UserGeofenceStateVO {
    
    @Schema(description = "状态记录ID")
    private Long id;
    
    @Schema(description = "围栏ID")
    private Long geofenceId;
    
    @Schema(description = "围栏名称")
    private String geofenceName;
    
    @Schema(description = "当前状态: OUTSIDE-外部, INSIDE-内部, DWELLING-停留中")
    private String currentState;
    
    @Schema(description = "首次进入时间")
    private LocalDateTime firstEnterTime;
    
    @Schema(description = "最后进入时间")
    private LocalDateTime lastEnterTime;
    
    @Schema(description = "最后离开时间")
    private LocalDateTime lastExitTime;
    
    @Schema(description = "累计停留时间（分钟）")
    private Integer totalDwellMinutes;
    
    @Schema(description = "本次停留时间（分钟）")
    private Integer currentDwellMinutes;
    
    @Schema(description = "进入次数统计")
    private Integer enterCount;
    
    @Schema(description = "当前位置经度")
    private BigDecimal currentLongitude;
    
    @Schema(description = "当前位置纬度")
    private BigDecimal currentLatitude;
    
    @Schema(description = "位置精度（米）")
    private BigDecimal locationAccuracy;
    
    @Schema(description = "置信度评分")
    private Integer confidenceScore;
    
    @Schema(description = "是否订阅")
    private Boolean subscribed;
    
    @Schema(description = "会话ID")
    private String sessionId;
    
    @Schema(description = "会话开始时间")
    private LocalDateTime sessionStartTime;
}
