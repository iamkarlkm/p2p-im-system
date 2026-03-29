package com.im.backend.modules.geofencing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏实体类 - 智能到店服务核心实体
 * 支持多边形、圆形、不规则形状围栏定义
 * 支持层级嵌套（商圈→商场→店铺）
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("geofence_zone")
public class GeofenceZone {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 围栏名称 */
    private String name;
    
    /** 围栏描述 */
    private String description;
    
    /** 围栏类型: CIRCLE-圆形, POLYGON-多边形, RECTANGLE-矩形, IRREGULAR-不规则 */
    private String type;
    
    /** 围栏层级: 1-商圈, 2-商场, 3-店铺 */
    private Integer level;
    
    /** 父围栏ID，支持层级嵌套 */
    private Long parentId;
    
    /** 关联POI ID */
    private Long poiId;
    
    /** 关联商户ID */
    private Long merchantId;
    
    /** 圆形围栏中心经度 */
    private BigDecimal centerLongitude;
    
    /** 圆形围栏中心纬度 */
    private BigDecimal centerLatitude;
    
    /** 圆形围栏半径（米） */
    private Integer radius;
    
    /** 多边形围栏坐标点JSON数组 [{"lng": 116.0, "lat": 39.9}, ...] */
    private String polygonPoints;
    
    /** 地理哈希，用于快速空间索引 */
    private String geoHash;
    
    /** 最小地理哈希精度 */
    private Integer minGeoHashPrecision;
    
    /** 最大地理哈希精度 */
    private Integer maxGeoHashPrecision;
    
    /** 围栏边界矩形最小经度 */
    private BigDecimal minLongitude;
    
    /** 围栏边界矩形最大经度 */
    private BigDecimal maxLongitude;
    
    /** 围栏边界矩形最小纬度 */
    private BigDecimal minLatitude;
    
    /** 围栏边界矩形最大纬度 */
    private BigDecimal maxLatitude;
    
    /** 围栏面积（平方米） */
    private BigDecimal area;
    
    /** 围栏周长（米） */
    private BigDecimal perimeter;
    
    /** 触发条件: ENTER-进入, EXIT-离开, DWELL-停留 */
    private String triggerCondition;
    
    /** 停留触发时长（分钟） */
    private Integer dwellTime;
    
    /** 生效开始时间 */
    private LocalDateTime effectiveStartTime;
    
    /** 生效结束时间 */
    private LocalDateTime effectiveEndTime;
    
    /** 营业时间范围JSON {"weekdays": "09:00-22:00", "weekend": "10:00-23:00"} */
    private String businessHours;
    
    /** 生效星期: 0-周日, 1-周一... 多选逗号分隔 */
    private String effectiveWeekdays;
    
    /** 是否节假日生效 */
    private Boolean holidayEffective;
    
    /** 用户等级限制: 0-所有用户, 1-新用户, 2-常客, 3-会员 */
    private Integer userLevelLimit;
    
    /** 最少到访次数要求 */
    private Integer minVisitCount;
    
    /** 最大触发次数限制 */
    private Integer maxTriggerCount;
    
    /** 触发冷却时间（小时） */
    private Integer cooldownHours;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 状态: ACTIVE-激活, PAUSED-暂停, EXPIRED-过期 */
    private String status;
    
    /** 围栏创建来源: SYSTEM-系统自动, MERCHANT-商户创建, ADMIN-管理员 */
    private String source;
    
    /** 创建人ID */
    private Long creatorId;
    
    /** 更新人ID */
    private Long updaterId;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /** 删除标记 */
    @TableLogic
    private Boolean deleted;
    
    /** 扩展属性 */
    private String extraProperties;
}
