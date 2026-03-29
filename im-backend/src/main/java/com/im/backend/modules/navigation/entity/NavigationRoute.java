package com.im.backend.modules.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 导航路线实体类
 * 存储用户规划的导航路线信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_navigation_route")
public class NavigationRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 起点POI ID
     */
    private Long startPoiId;

    /**
     * 起点名称
     */
    private String startName;

    /**
     * 起点经度
     */
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    private BigDecimal startLatitude;

    /**
     * 终点POI ID
     */
    private Long endPoiId;

    /**
     * 终点名称
     */
    private String endName;

    /**
     * 终点经度
     */
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     */
    private BigDecimal endLatitude;

    /**
     * 出行方式：DRIVE-驾车 WALK-步行 RIDE-骑行 BUS-公交
     */
    private String travelMode;

    /**
     * 路线策略：FASTEST-最快 SHORTEST-最短 AVOID_TRAFFIC-避堵 ECONOMIC-经济
     */
    private String routeStrategy;

    /**
     * 总距离(米)
     */
    private Integer totalDistance;

    /**
     * 预计时间(秒)
     */
    private Integer estimatedDuration;

    /**
     * 预计费用(元)
     */
    private BigDecimal estimatedCost;

    /**
     * 路线坐标点串(经度,纬度;经度,纬度...)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String routePolyline;

    /**
     * 路线步骤详情(JSON数组)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<RouteStep> routeSteps;

    /**
     * 途经点数量
     */
    private Integer waypointCount;

    /**
     * 实时路况信息(JSON)
     */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private TrafficInfo trafficInfo;

    /**
     * 是否收藏
     */
    private Boolean isFavorite;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedTime;

    /**
     * 路线状态：ACTIVE-有效 EXPIRED-过期
     */
    private String status;

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

    /**
     * 路线步骤内部类
     */
    @Data
    public static class RouteStep {
        /**
         * 步骤序号
         */
        private Integer stepIndex;

        /**
         * 步骤说明
         */
        private String instruction;

        /**
         * 距离(米)
         */
        private Integer distance;

        /**
         * 预计时间(秒)
         */
        private Integer duration;

        /**
         * 转向类型
         */
        private String turnType;

        /**
         * 道路名称
         */
        private String roadName;

        /**
         * 动作图标
         */
        private String actionIcon;

        /**
         * 步骤坐标点串
         */
        private String polyline;
    }

    /**
     * 实时路况信息内部类
     */
    @Data
    public static class TrafficInfo {
        /**
         * 畅通路段距离(米)
         */
        private Integer smoothDistance;

        /**
         * 缓慢路段距离(米)
         */
        private Integer slowDistance;

        /**
         * 拥堵路段距离(米)
         */
        private Integer congestedDistance;

        /**
         * 严重拥堵路段距离(米)
         */
        private Integer severelyCongestedDistance;

        /**
         * 路况更新时间
         */
        private LocalDateTime updateTime;

        /**
         * 路况状态：SMOOTH-畅通 SLOW-缓慢 CONGESTED-拥堵 SEVERE-严重拥堵
         */
        private String overallStatus;
    }
}
