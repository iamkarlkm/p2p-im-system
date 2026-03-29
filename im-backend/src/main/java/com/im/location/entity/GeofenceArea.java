package com.im.location.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地理围栏实体
 * 定义电子围栏区域和触发规则
 */
@Data
@TableName("geofence_area")
public class GeofenceArea {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 围栏唯一标识
     */
    private String geofenceId;
    
    /**
     * 围栏类型: 1-圆形 2-多边形 3-线性
     */
    private Integer geofenceType;
    
    /**
     * 围栏名称
     */
    private String name;
    
    /**
     * 关联会话ID
     */
    private String sessionId;
    
    /**
     * 关联商户ID(商户围栏时)
     */
    private Long merchantId;
    
    /**
     * 中心点经度(圆形围栏)
     */
    private Double centerLongitude;
    
    /**
     * 中心点纬度(圆形围栏)
     */
    private Double centerLatitude;
    
    /**
     * 半径(米，圆形围栏)
     */
    private Integer radius;
    
    /**
     * 多边形坐标JSON(多边形围栏)
     */
    private String polygonCoordinates;
    
    /**
     * 围栏用途: 1-目的地 2-安全区 3-禁入区 4-提醒点
     */
    private Integer purpose;
    
    /**
     * 触发事件: 1-进入 2-离开 3-停留
     */
    private Integer triggerEvent;
    
    /**
     * 停留触发时长(分钟，停留触发时)
     */
    private Integer dwellTime;
    
    /**
     * 生效开始时间
     */
    private LocalDateTime validStartTime;
    
    /**
     * 生效结束时间
     */
    private LocalDateTime validEndTime;
    
    /**
     * 状态: 0-禁用 1-启用
     */
    private Integer status;
    
    /**
     * 创建者
     */
    private Long creatorId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
