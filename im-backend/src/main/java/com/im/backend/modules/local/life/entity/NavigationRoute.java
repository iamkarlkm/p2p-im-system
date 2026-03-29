package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 导航路线规划实体类
 * Navigation Route Planning Entity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_navigation_route")
public class NavigationRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 路线名称
     */
    @TableField("route_name")
    private String routeName;

    /**
     * 起点名称
     */
    @TableField("origin_name")
    private String originName;

    /**
     * 起点经度
     */
    @TableField("origin_lng")
    private BigDecimal originLng;

    /**
     * 起点纬度
     */
    @TableField("origin_lat")
    private BigDecimal originLat;

    /**
     * 终点名称
     */
    @TableField("destination_name")
    private String destinationName;

    /**
     * 终点经度
     */
    @TableField("destination_lng")
    private BigDecimal destinationLng;

    /**
     * 终点纬度
     */
    @TableField("destination_lat")
    private BigDecimal destinationLat;

    /**
     * 导航模式：DRIVING-驾车, WALKING-步行, RIDING-骑行, TRANSIT-公交, TRUCK-货车
     */
    @TableField("nav_mode")
    private String navMode;

    /**
     * 路线策略：FASTEST-最快, SHORTEST-最短, AVOID_CONGESTION-避堵, ECONOMIC-经济, HIGHWAY_FIRST-高速优先
     */
    @TableField("route_strategy")
    private String routeStrategy;

    /**
     * 路线距离（米）
     */
    @TableField("distance")
    private Integer distance;

    /**
     * 预计耗时（秒）
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 路线拥堵程度：SMOOTH-畅通, SLOW-缓行, CONGESTED-拥堵, SEVERE-严重拥堵
     */
    @TableField("traffic_condition")
    private String trafficCondition;

    /**
     * 过路费（元）
     */
    @TableField("toll_fee")
    private BigDecimal tollFee;

    /**
     * 路线坐标点集合（JSON格式：[[lng,lat],[lng,lat]...]）
     */
    @TableField("route_points")
    private String routePoints;

    /**
     * 路线步骤详情（JSON格式）
     */
    @TableField("route_steps")
    private String routeSteps;

    /**
     * 途经点坐标（JSON格式）
     */
    @TableField("waypoints")
    private String waypoints;

    /**
     * 地图服务商：GAODE-高德, TENCENT-腾讯, BAIDU-百度
     */
    @TableField("map_provider")
    private String mapProvider;

    /**
     * 路线状态：ACTIVE-有效, EXPIRED-过期, FAVORITE-收藏
     */
    @TableField("status")
    private String status;

    /**
     * 是否收藏
     */
    @TableField("is_favorite")
    private Boolean isFavorite;

    /**
     * 收藏时间
     */
    @TableField("favorite_time")
    private LocalDateTime favoriteTime;

    /**
     * 路线创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 路线更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}
