package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 用户等级成长请求DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户等级成长请求")
public class UserGrowthRequest {
    
    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "user_123456", required = true)
    private String userId;
    
    @Schema(description = "行为类型：browse/search/checkin/consume/review/share", example = "consume")
    private String actionType;
    
    @Schema(description = "行为数值", example = "100")
    private Integer actionValue;
    
    @Schema(description = "关联POI ID", example = "poi_123456")
    private String poiId;
    
    @Schema(description = "关联订单ID", example = "order_123456")
    private String orderId;
    
    @Schema(description = "扩展参数")
    private Map<String, Object> extraParams;
}
