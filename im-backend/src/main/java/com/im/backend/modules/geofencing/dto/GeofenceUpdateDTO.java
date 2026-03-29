package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏更新DTO
 */
@Data
@Schema(description = "地理围栏更新请求")
public class GeofenceUpdateDTO {
    
    @Size(max = 100, message = "围栏名称最多100字符")
    @Schema(description = "围栏名称")
    private String name;
    
    @Size(max = 500, message = "描述最多500字符")
    @Schema(description = "围栏描述")
    private String description;
    
    @Min(value = 1, message = "层级最小为1")
    @Max(value = 3, message = "层级最大为3")
    @Schema(description = "围栏层级: 1-商圈, 2-商场, 3-店铺")
    private Integer level;
    
    @Schema(description = "父围栏ID")
    private Long parentId;
    
    @Schema(description = "关联POI ID")
    private Long poiId;
    
    @Schema(description = "圆形围栏中心经度")
    private BigDecimal centerLongitude;
    
    @Schema(description = "圆形围栏中心纬度")
    private BigDecimal centerLatitude;
    
    @Min(value = 10, message = "半径最小10米")
    @Max(value = 10000, message = "半径最大10000米")
    @Schema(description = "圆形围栏半径（米）")
    private Integer radius;
    
    @Schema(description = "多边形围栏坐标点列表")
    private List<Map<String, BigDecimal>> polygonPoints;
    
    @Pattern(regexp = "^(ENTER|EXIT|DWELL)$", message = "触发条件必须是ENTER、EXIT或DWELL")
    @Schema(description = "触发条件: ENTER-进入, EXIT-离开, DWELL-停留")
    private String triggerCondition;
    
    @Schema(description = "停留触发时长（分钟）")
    private Integer dwellTime;
    
    @Schema(description = "生效开始时间")
    private LocalDateTime effectiveStartTime;
    
    @Schema(description = "生效结束时间")
    private LocalDateTime effectiveEndTime;
    
    @Schema(description = "生效星期")
    private String effectiveWeekdays;
    
    @Schema(description = "是否节假日生效")
    private Boolean holidayEffective;
    
    @Schema(description = "用户等级限制")
    private Integer userLevelLimit;
    
    @Schema(description = "最少到访次数要求")
    private Integer minVisitCount;
    
    @Schema(description = "最大触发次数限制")
    private Integer maxTriggerCount;
    
    @Schema(description = "触发冷却时间（小时）")
    private Integer cooldownHours;
    
    @Schema(description = "营业时间范围JSON")
    private String businessHours;
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "状态: ACTIVE-激活, PAUSED-暂停, EXPIRED-过期")
    private String status;
}
