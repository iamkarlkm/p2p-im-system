package com.im.backend.modules.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POI途经点实体类
 * 存储导航路线中的途经点信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_poi_waypoint")
public class POIWaypoint {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属路线ID
     */
    private Long routeId;

    /**
     * 途经点序号
     */
    private Integer waypointIndex;

    /**
     * POI ID
     */
    private Long poiId;

    /**
     * POI名称
     */
    private String poiName;

    /**
     * POI分类ID
     */
    private Long categoryId;

    /**
     * POI分类名称
     */
    private String categoryName;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 所在区县
     */
    private String district;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 距起点的距离(米)
     */
    private Integer distanceFromStart;

    /**
     * 预计到达时间(秒)
     */
    private Integer estimatedArrivalTime;

    /**
     * 停留时长(分钟)
     */
    private Integer stayDuration;

    /**
     * 途经点类型：START-起点 WAYPOINT-途经点 END-终点
     */
    private String waypointType;

    /**
     * 是否必须到达
     */
    private Boolean required;

    /**
     * 时间窗开始时间(用于预约场景)
     */
    private LocalDateTime timeWindowStart;

    /**
     * 时间窗结束时间
     */
    private LocalDateTime timeWindowEnd;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 图标URL
     */
    private String iconUrl;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
