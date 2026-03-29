package com.im.backend.modules.geofence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地理围栏实体
 * 定义商户或门店的地理围栏区域
 */
@Data
@TableName("im_geofence")
public class Geofence {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联商户ID */
    private Long merchantId;

    /** 关联门店ID */
    private Long storeId;

    /** 围栏名称 */
    private String name;

    /** 围栏类型: CIRCLE-圆形, POLYGON-多边形, LINE-线性 */
    private String type;

    /** 中心点经度(圆形围栏) */
    private Double centerLongitude;

    /** 中心点纬度(圆形围栏) */
    private Double centerLatitude;

    /** 半径(米,圆形围栏) */
    private Double radius;

    /** 多边形顶点坐标JSON(多边形围栏) [{"lng":xx,"lat":xx},...] */
    private String polygonPoints;

    /** 围栏用途: ARRIVAL-到店, DEPARTURE-离店, PROMOTION-营销推送 */
    private String purpose;

    /** 触发条件: ENTER-进入, EXIT-离开, DWELL-停留 */
    private String triggerCondition;

    /** 最小触发距离(米) */
    private Integer minTriggerDistance;

    /** 停留触发时间(秒,用于DWELL条件) */
    private Integer dwellTimeSeconds;

    /** 生效开始时间 */
    private LocalDateTime validStartTime;

    /** 生效结束时间 */
    private LocalDateTime validEndTime;

    /** 状态: ACTIVE-激活, INACTIVE-未激活, DELETED-已删除 */
    private String status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
