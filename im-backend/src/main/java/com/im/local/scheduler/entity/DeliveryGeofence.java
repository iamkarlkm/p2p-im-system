package com.im.local.scheduler.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 配送围栏实体
 * 动态围栏边界调整
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGeofence {
    
    /** 围栏ID */
    private Long geofenceId;
    
    /** 围栏名称 */
    private String name;
    
    /** 围栏类型: 1-商圈 2-写字楼 3-小区 4-学校 5-医院 6-自定义 */
    private Integer type;
    
    /** 围栏形状: 1-圆形 2-多边形 */
    private Integer shapeType;
    
    /** 圆心经度 */
    private BigDecimal centerLng;
    
    /** 圆心纬度 */
    private BigDecimal centerLat;
    
    /** 半径(米) - 圆形围栏 */
    private Integer radius;
    
    /** 多边形顶点坐标 JSON */
    private String polygonPoints;
    
    /** GeoHash网格列表 */
    private String geohashGrids;
    
    /** 所属城市 */
    private String cityCode;
    
    /** 所属区域 */
    private String districtCode;
    
    /** 当前订单数 */
    private Integer currentOrderCount;
    
    /** 当前骑手数 */
    private Integer currentStaffCount;
    
    /** 运力饱和度: 0-100 */
    private Integer saturationRate;
    
    /** 动态半径(米) - 根据运力调整 */
    private Integer dynamicRadius;
    
    /** 基础半径(米) */
    private Integer baseRadius;
    
    /** 高峰时段扩展比例 */
    private BigDecimal peakExpansionRatio;
    
    /** 是否启用动态调整 */
    private Boolean dynamicAdjustEnabled;
    
    /** 状态: 0-禁用 1-启用 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
