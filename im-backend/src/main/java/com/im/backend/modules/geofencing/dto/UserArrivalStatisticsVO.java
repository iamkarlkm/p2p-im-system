package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户到店统计VO
 */
@Data
@Schema(description = "用户到店统计")
public class UserArrivalStatisticsVO {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "统计天数")
    private Integer statisticsDays;
    
    @Schema(description = "到店总次数")
    private Integer totalArrivalCount;
    
    @Schema(description = "到店商户数量")
    private Integer uniqueMerchantCount;
    
    @Schema(description = "总停留时长（分钟）")
    private Integer totalDwellMinutes;
    
    @Schema(description = "平均停留时长（分钟）")
    private BigDecimal avgDwellMinutes;
    
    @Schema(description = "最常去商户")
    private List<FrequentMerchantVO> frequentMerchants;
    
    @Schema(description = "到店时间分布")
    private Map<String, Integer> arrivalTimeDistribution;
    
    @Schema(description = "到店星期分布")
    private Map<String, Integer> arrivalWeekdayDistribution;
    
    @Schema(description = "到店类型偏好")
    private Map<String, Integer> merchantTypePreference;
    
    @Schema(description = "收到的消息数")
    private Integer receivedMessageCount;
    
    @Schema(description = "使用的优惠券数")
    private Integer usedCouponCount;
}
