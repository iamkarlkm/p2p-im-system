package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏创建DTO
 */
@Data
@Schema(description = "地理围栏创建请求")
public class GeofenceCreateDTO {
    
    @NotBlank(message = "围栏名称不能为空")
    @Size(max = 100, message = "围栏名称最多100字符")
    @Schema(description = "围栏名称")
    private String name;
    
    @Size(max = 500, message = "描述最多500字符")
    @Schema(description = "围栏描述")
    private String description;
    
    @NotBlank(message = "围栏类型不能为空")
    @Pattern(regexp = "^(CIRCLE|POLYGON|RECTANGLE|IRREGULAR)$", message = "类型必须是CIRCLE、POLYGON、RECTANGLE或IRREGULAR")
    @Schema(description = "围栏类型: CIRCLE-圆形, POLYGON-多边形, RECTANGLE-矩形, IRREGULAR-不规则")
    private String type;
    
    @Min(value = 1, message = "层级最小为1")
    @Max(value = 3, message = "层级最大为3")
    @Schema(description = "围栏层级: 1-商圈, 2-商场, 3-店铺")
    private Integer level;
    
    @Schema(description = "父围栏ID，支持层级嵌套")
    private Long parentId;
    
    @Schema(description = "关联POI ID")
    private Long poiId;
    
    @Schema(description = "关联商户ID")
    private Long merchantId;
    
    @Schema(description = "圆形围栏中心经度")
    private BigDecimal centerLongitude;
    
    @Schema(description = "圆形围栏中心纬度")
    private BigDecimal centerLatitude;
    
    @Min(value = 10, message = "半径最小10米")
    @Max(value = 10000, message = "半径最大10000米")
    @Schema(description = "圆形围栏半径（米）")
    private Integer radius;
    
    @Schema(description = "多边形围栏坐标点列表 [{lng, lat}, ...]")
    private List<Map<String, BigDecimal>> polygonPoints;
    
    @Pattern(regexp = "^(ENTER|EXIT|DWELL)$", message = "触发条件必须是ENTER、EXIT或DWELL")
    @Schema(description = "触发条件: ENTER-进入, EXIT-离开, DWELL-停留")
    private String triggerCondition;
    
    @Min(value = 1, message = "停留时长最小1分钟")
    @Schema(description = "停留触发时长（分钟）")
    private Integer dwellTime;
    
    @Schema(description = "生效开始时间")
    private LocalDateTime effectiveStartTime;
    
    @Schema(description = "生效结束时间")
    private LocalDateTime effectiveEndTime;
    
    @Schema(description = "生效星期: 0-周日, 1-周一... 多选逗号分隔")
    private String effectiveWeekdays;
    
    @Schema(description = "是否节假日生效")
    private Boolean holidayEffective;
    
    @Schema(description = "用户等级限制: 0-所有用户, 1-新用户, 2-常客, 3-会员")
    private Integer userLevelLimit;
    
    @Schema(description = "最少到访次数要求")
    private Integer minVisitCount;
    
    @Schema(description = "最大触发次数限制")
    private Integer maxTriggerCount;
    
    @Schema(description = "触发冷却时间（小时）")
    private Integer cooldownHours;
    
    @Schema(description = "营业时间范围JSON")
    private String businessHours;
    
    @Schema(description = "扩展属性JSON")
    private String extraProperties;
}
