package com.im.backend.modules.local.life.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 路线规划结果DTO
 * Route Planning Result DTO
 */
@Data
public class RoutePlanningResultDTO {

    /**
     * 路线ID
     */
    private Long routeId;

    /**
     * 路线名称
     */
    private String routeName;

    /**
     * 起点信息
     */
    private LocationDTO origin;

    /**
     * 终点信息
     */
    private LocationDTO destination;

    /**
     * 导航模式
     */
    private String navMode;

    /**
     * 路线策略
     */
    private String routeStrategy;

    /**
     * 路线距离（米）
     */
    private Integer distance;

    /**
     * 预计耗时（秒）
     */
    private Integer duration;

    /**
     * 预计耗时格式化（如：35分钟）
     */
    private String durationText;

    /**
     * 距离格式化（如：12.5公里）
     */
    private String distanceText;

    /**
     * 路线拥堵程度
     */
    private String trafficCondition;

    /**
     * 拥堵路段信息
     */
    private List<CongestionSegmentDTO> congestionSegments;

    /**
     * 过路费（元）
     */
    private BigDecimal tollFee;

    /**
     * 路线坐标点集合
     */
    private List<List<BigDecimal>> routePoints;

    /**
     * 路线步骤详情
     */
    private List<RouteStepDTO> steps;

    /**
     * 途经点列表
     */
    private List<LocationDTO> waypoints;

    /**
     * 地图服务商
     */
    private String mapProvider;

    /**
     * 是否有实时路况
     */
    private Boolean hasTraffic;

    @Data
    public static class LocationDTO {
        private BigDecimal lng;
        private BigDecimal lat;
        private String name;
        private String address;
    }

    @Data
    public static class RouteStepDTO {
        /**
         * 步骤索引
         */
        private Integer index;

        /**
         * 步骤说明
         */
        private String instruction;

        /**
         * 步骤距离（米）
         */
        private Integer distance;

        /**
         * 步骤耗时（秒）
         */
        private Integer duration;

        /**
         * 转向动作
         */
        private String action;

        /**
         * 道路名称
         */
        private String roadName;

        /**
         * 坐标点集合
         */
        private List<List<BigDecimal>> points;

        /**
         * 语音播报文本
         */
        private String voiceText;

        /**
         * 图标类型
         */
        private String iconType;
    }

    @Data
    public static class CongestionSegmentDTO {
        /**
         * 拥堵路段起点索引
         */
        private Integer startIndex;

        /**
         * 拥堵路段终点索引
         */
        private Integer endIndex;

        /**
         * 拥堵程度
         */
        private String level;

        /**
         * 拥堵路段长度（米）
         */
        private Integer length;

        /**
         * 拥堵速度（km/h）
         */
        private Integer speed;
    }
}
