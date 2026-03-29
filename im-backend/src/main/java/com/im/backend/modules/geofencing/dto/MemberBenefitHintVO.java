package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 会员权益提示VO
 */
@Data
@Schema(description = "会员权益提示")
public class MemberBenefitHintVO {
    
    @Schema(description = "会员等级")
    private Integer memberLevel;
    
    @Schema(description = "会员等级名称")
    private String levelName;
    
    @Schema(description = "当前积分")
    private Integer currentPoints;
    
    @Schema(description = "本次可获得积分")
    private Integer pointsToEarn;
    
    @Schema(description = "会员专属折扣")
    private BigDecimal memberDiscount;
    
    @Schema(description = "会员专属服务")
    private String exclusiveService;
}
