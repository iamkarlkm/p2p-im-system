package com.im.backend.modules.local.life.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路线规划请求DTO
 * Route Planning Request DTO
 */
@Data
public class RoutePlanningRequestDTO {

    /**
     * 起点经度
     */
    @NotNull(message = "起点经度不能为空")
    private BigDecimal originLng;

    /**
     * 起点纬度
     */
    @NotNull(message = "起点纬度不能为空")
    private BigDecimal originLat;

    /**
     * 起点名称（可选）
     */
    private String originName;

    /**
     * 终点经度
     */
    @NotNull(message = "终点经度不能为空")
    private BigDecimal destinationLng;

    /**
     * 终点纬度
     */
    @NotNull(message = "终点纬度不能为空")
    private BigDecimal destinationLat;

    /**
     * 终点名称（可选）
     */
    private String destinationName;

    /**
     * 导航模式：DRIVING-驾车, WALKING-步行, RIDING-骑行, TRANSIT-公交, TRUCK-货车
     */
    @NotBlank(message = "导航模式不能为空")
    private String navMode;

    /**
     * 路线策略：FASTEST-最快, SHORTEST-最短, AVOID_CONGESTION-避堵, ECONOMIC-经济, HIGHWAY_FIRST-高速优先
     */
    private String routeStrategy;

    /**
     * 途经点列表
     */
    private List<WaypointDTO> waypoints;

    /**
     * 是否避开高速
     */
    private Boolean avoidHighway;

    /**
     * 是否避开收费道路
     */
    private Boolean avoidToll;

    /**
     * 是否避开拥堵
     */
    private Boolean avoidCongestion;

    /**
     * 货车参数（货车导航时使用）
     */
    private TruckParamsDTO truckParams;

    /**
     * 地图服务商：GAODE-高德, TENCET-腾讯, BAIDU-百度
     */
    private String mapProvider;

    @Data
    public static class WaypointDTO {
        private BigDecimal lng;
        private BigDecimal lat;
        private String name;
    }

    @Data
    public static class TruckParamsDTO {
        /**
         * 车高（米）
         */
        private BigDecimal height;

        /**
         * 车宽（米）
         */
        private BigDecimal width;

        /**
         * 车长（米）
         */
        private BigDecimal length;

        /**
         * 总重量（吨）
         */
        private BigDecimal weight;

        /**
         * 轴数
         */
        private Integer axleCount;

        /**
         * 是否装载危险品
         */
        private Boolean hazardous;
    }
}
