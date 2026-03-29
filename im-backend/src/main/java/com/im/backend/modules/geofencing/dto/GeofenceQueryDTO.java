package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 地理围栏查询DTO
 */
@Data
@Schema(description = "地理围栏查询条件")
public class GeofenceQueryDTO {
    
    @Min(value = 1, message = "页码最小为1")
    @Schema(description = "页码", defaultValue = "1")
    private Integer page = 1;
    
    @Min(value = 1, message = "每页大小最小为1")
    @Schema(description = "每页大小", defaultValue = "20")
    private Integer size = 20;
    
    @Schema(description = "商户ID")
    private Long merchantId;
    
    @Schema(description = "POI ID")
    private Long poiId;
    
    @Schema(description = "围栏类型")
    private String type;
    
    @Schema(description = "围栏层级")
    private Integer level;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "经度（附近查询）")
    private BigDecimal longitude;
    
    @Schema(description = "纬度（附近查询）")
    private BigDecimal latitude;
    
    @Schema(description = "查询半径（米）")
    private Integer radius;
}
