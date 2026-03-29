package com.im.local.scheduler.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 围栏创建请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGeofenceRequest {
    
    /** 围栏名称 */
    private String name;
    
    /** 围栏类型: 1-商圈 2-写字楼 3-小区 4-学校 5-医院 6-自定义 */
    private Integer type;
    
    /** 围栏形状: 1-圆形 2-多边形 */
    private Integer shapeType;
    
    /** 圆心经度(圆形围栏) */
    private BigDecimal centerLng;
    
    /** 圆心纬度(圆形围栏) */
    private BigDecimal centerLat;
    
    /** 半径(米) - 圆形围栏 */
    private Integer radius;
    
    /** 多边形顶点坐标 JSON(多边形围栏) */
    private String polygonPoints;
    
    /** 所属城市代码 */
    private String cityCode;
    
    /** 所属区域代码 */
    private String districtCode;
    
    /** 是否启用动态调整 */
    private Boolean dynamicAdjustEnabled;
    
    /** 高峰时段扩展比例(默认1.2) */
    private BigDecimal peakExpansionRatio;
}
