package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 围栏层级树VO
 */
@Data
@Schema(description = "围栏层级树")
public class GeofenceTreeVO {
    
    @Schema(description = "围栏ID")
    private Long id;
    
    @Schema(description = "围栏名称")
    private String name;
    
    @Schema(description = "围栏层级")
    private Integer level;
    
    @Schema(description = "围栏类型")
    private String type;
    
    @Schema(description = "子围栏列表")
    private List<GeofenceTreeVO> children;
}
