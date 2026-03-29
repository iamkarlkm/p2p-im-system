package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消息统计VO
 */
@Data
@Schema(description = "消息统计")
public class MessageStatisticsVO {
    
    @Schema(description = "总发送数")
    private Integer totalSent;
    
    @Schema(description = "送达数")
    private Integer delivered;
    
    @Schema(description = "已读数")
    private Integer read;
    
    @Schema(description = "失败数")
    private Integer failed;
    
    @Schema(description = "送达率")
    private BigDecimal deliveryRate;
    
    @Schema(description = "阅读率")
    private BigDecimal readRate;
    
    @Schema(description = "点击率")
    private BigDecimal clickRate;
    
    @Schema(description = "转化率")
    private BigDecimal conversionRate;
}
