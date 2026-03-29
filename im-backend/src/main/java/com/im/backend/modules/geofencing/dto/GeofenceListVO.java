package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地理围栏列表项VO
 */
@Data
@Schema(description = "地理围栏列表项")
public class GeofenceListVO {
    
    @Schema(description = "围栏ID")
    private Long id;
    
    @Schema(description = "围栏名称")
    private String name;
    
    @Schema(description = "围栏描述")
    private String description;
    
    @Schema(description = "围栏类型")
    private String type;
    
    @Schema(description = "围栏层级")
    private Integer level;
    
    @Schema(description = "父围栏ID")
    private Long parentId;
    
    @Schema(description = "关联POI ID")
    private Long poiId;
    
    @Schema(description = "关联商户ID")
    private Long merchantId;
    
    @Schema(description = "圆形围栏中心经度")
    private BigDecimal centerLongitude;
    
    @Schema(description = "圆形围栏中心纬度")
    private BigDecimal centerLatitude;
    
    @Schema(description = "圆形围栏半径（米）")
    private Integer radius;
    
    @Schema(description = "触发条件")
    private String triggerCondition;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "围栏来源")
    private String source;
    
    @Schema(description = "围栏面积（平方米）")
    private BigDecimal area;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
