package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏详情VO
 */
@Data
@Schema(description = "地理围栏详情")
public class GeofenceDetailVO {
    
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
    
    @Schema(description = "多边形坐标点列表")
    private List<Map<String, Double>> polygonCoordinates;
    
    @Schema(description = "地理哈希")
    private String geoHash;
    
    @Schema(description = "围栏面积（平方米）")
    private BigDecimal area;
    
    @Schema(description = "围栏周长（米）")
    private BigDecimal perimeter;
    
    @Schema(description = "触发条件")
    private String triggerCondition;
    
    @Schema(description = "停留触发时长（分钟）")
    private Integer dwellTime;
    
    @Schema(description = "生效开始时间")
    private LocalDateTime effectiveStartTime;
    
    @Schema(description = "生效结束时间")
    private LocalDateTime effectiveEndTime;
    
    @Schema(description = "营业时间范围")
    private String businessHours;
    
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
    
    @Schema(description = "是否启用")
    private Boolean enabled;
    
    @Schema(description = "状态")
    private String status;
    
    @Schema(description = "围栏来源")
    private String source;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "子围栏列表")
    private List<GeofenceListVO> subGeofences;
}
