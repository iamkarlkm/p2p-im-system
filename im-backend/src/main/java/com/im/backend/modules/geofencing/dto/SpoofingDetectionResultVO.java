package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 位置作弊检测结果VO
 */
@Data
@Schema(description = "位置作弊检测结果")
public class SpoofingDetectionResultVO {
    
    @Schema(description = "是否怀疑作弊")
    private Boolean suspectedSpoofing;
    
    @Schema(description = "作弊类型")
    private String spoofingType;
    
    @Schema(description = "置信度")
    private BigDecimal confidence;
    
    @Schema(description = "检测原因")
    private String reason;
    
    @Schema(description = "建议操作")
    private String suggestedAction;
}
