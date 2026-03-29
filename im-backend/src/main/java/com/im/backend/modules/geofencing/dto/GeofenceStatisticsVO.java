package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 围栏统计VO
 */
@Data
@Schema(description = "围栏统计信息")
public class GeofenceStatisticsVO {
    
    @Schema(description = "围栏总数")
    private Integer totalCount;
    
    @Schema(description = "激活围栏数")
    private Integer activeCount;
    
    @Schema(description = "暂停围栏数")
    private Integer pausedCount;
    
    @Schema(description = "今日触发次数")
    private Integer todayTriggerCount;
    
    @Schema(description = "本周触发次数")
    private Integer weeklyTriggerCount;
    
    @Schema(description = "本月触发次数")
    private Integer monthlyTriggerCount;
    
    @Schema(description = "平均每日触发次数")
    private BigDecimal avgDailyTriggers;
    
    @Schema(description = "覆盖用户数量")
    private Integer coveredUserCount;
    
    @Schema(description = "消息发送成功率")
    private BigDecimal messageSuccessRate;
}
