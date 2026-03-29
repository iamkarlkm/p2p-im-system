package com.im.entity.geofence;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 地理围栏实体类
 * 用于定义POI周边的场景化围栏区域
 */
@Data
public class GeoFence {
    
    /** 围栏ID */
    private String fenceId;
    
    /** 围栏名称 */
    private String fenceName;
    
    /** 关联POI ID */
    private String poiId;
    
    /** POI名称 */
    private String poiName;
    
    /** 围栏类型: CIRCLE-圆形, POLYGON-多边形, LINE-线性 */
    private String fenceType;
    
    /** 圆形围栏中心点经度 */
    private Double centerLongitude;
    
    /** 圆形围栏中心点纬度 */
    private Double centerLatitude;
    
    /** 圆形围栏半径(米) */
    private Integer radius;
    
    /** 多边形围栏顶点坐标列表 [[lon,lat],[lon,lat],...] */
    private List<List<Double>> polygonPoints;
    
    /** 线性围栏路径坐标列表 */
    private List<List<Double>> linePoints;
    
    /** 围栏层级: 1-商圈, 2-商场, 3-店铺 */
    private Integer fenceLevel;
    
    /** 父围栏ID(支持嵌套) */
    private String parentFenceId;
    
    /** 生效时间规则 */
    private String timeRule;
    
    /** 生效星期: [1,2,3,4,5,6,7] */
    private List<Integer> activeDays;
    
    /** 每日生效时间段: [{"start":"09:00","end":"22:00"}] */
    private List<Map<String, String>> activeTimeRanges;
    
    /** 特殊日期规则 */
    private Map<String, Object> specialDateRules;
    
    /** 触发条件配置 */
    private Map<String, Object> triggerConditions;
    
    /** 进入围栏触发消息模板ID */
    private String enterMessageTemplateId;
    
    /** 停留超时触发消息模板ID */
    private String dwellMessageTemplateId;
    
    /** 离开围栏触发消息模板ID */
    private String exitMessageTemplateId;
    
    /** 停留超时时间(分钟) */
    private Integer dwellTimeout;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 创建者 */
    private String createBy;
    
    /** 备注 */
    private String remark;
}
