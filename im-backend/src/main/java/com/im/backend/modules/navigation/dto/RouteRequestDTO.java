package com.im.backend.modules.navigation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路线规划请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {

    /**
     * 起点经度
     */
    @NotNull(message = "起点经度不能为空")
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    @NotNull(message = "起点纬度不能为空")
    private BigDecimal startLatitude;

    /**
     * 起点名称
     */
    private String startName;

    /**
     * 终点经度
     */
    @NotNull(message = "终点经度不能为空")
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     */
    @NotNull(message = "终点纬度不能为空")
    private BigDecimal endLatitude;

    /**
     * 终点名称
     */
    private String endName;

    /**
     * 出行方式：DRIVE-驾车 WALK-步行 RIDE-骑行 BUS-公交
     */
    @NotBlank(message = "出行方式不能为空")
    private String travelMode;

    /**
     * 路线策略：FASTEST-最快 SHORTEST-最短 AVOID_TRAFFIC-避堵 ECONOMIC-经济
     */
    private String routeStrategy;

    /**
     * 途经点列表
     */
    @Valid
    private List<WaypointDTO> waypoints;

    /**
     * 是否避让高速
     */
    private Boolean avoidHighway;

    /**
     * 是否避让收费路段
     */
    private Boolean avoidToll;

    /**
     * 是否避让拥堵
     */
    private Boolean avoidCongestion;

    /**
     * 车牌号(用于限行判断)
     */
    private String plateNumber;

    /**
     * 车牌颜色
     */
    private String plateColor;

    /**
     * 出发时间(用于预测路况)
     */
    private String departureTime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 途经点DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaypointDTO {
        /**
         * 经度
         */
        @NotNull(message = "途经点经度不能为空")
        private BigDecimal longitude;

        /**
         * 纬度
         */
        @NotNull(message = "途经点纬度不能为空")
        private BigDecimal latitude;

        /**
         * 名称
         */
        private String name;

        /**
         * 是否必须到达
         */
        private Boolean required;

        /**
         * 停留时长(分钟)
         */
        private Integer stayDuration;
    }
}
