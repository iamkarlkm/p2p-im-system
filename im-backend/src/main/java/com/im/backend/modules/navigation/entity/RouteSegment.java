package com.im.backend.modules.navigation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 路线分段详情实体类
 * 存储导航路线的各个分段信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_route_segment")
public class RouteSegment {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属路线ID
     */
    private Long routeId;

    /**
     * 分段序号
     */
    private Integer segmentIndex;

    /**
     * 分段类型：START-起点 SEGMENT-途经段 END-终点
     */
    private String segmentType;

    /**
     * 起点经度
     */
    private BigDecimal startLongitude;

    /**
     * 起点纬度
     */
    private BigDecimal startLatitude;

    /**
     * 起点POI名称
     */
    private String startPoiName;

    /**
     * 终点经度
     */
    private BigDecimal endLongitude;

    /**
     * 终点纬度
     */
    private BigDecimal endLatitude;

    /**
     * 终点POI名称
     */
    private String endPoiName;

    /**
     * 分段距离(米)
     */
    private Integer distance;

    /**
     * 预计时间(秒)
     */
    private Integer duration;

    /**
     * 行驶速度(米/秒)
     */
    private Integer speed;

    /**
     * 道路类型：HIGHWAY-高速 MAIN_ROAD-主干道 SECONDARY-次干道 STREET-街道
     */
    private String roadType;

    /**
     * 道路名称
     */
    private String roadName;

    /**
     * 分段坐标点串
     */
    private String polyline;

    /**
     * 路况信息：SMOOTH-畅通 SLOW-缓慢 CONGESTED-拥堵 SEVERE-严重拥堵
     */
    private String trafficStatus;

    /**
     * 是否收费路段
     */
    private Boolean tollRoad;

    /**
     * 收费金额(元)
     */
    private BigDecimal tollFee;

    /**
     * 是否限行路段
     */
    private Boolean restricted;

    /**
     * 限行说明
     */
    private String restrictionInfo;

    /**
     * 红绿灯数量
     */
    private Integer trafficLightCount;

    /**
     * 电子眼数量
     */
    private Integer cameraCount;

    /**
     * 转向类型：STRAIGHT-直行 LEFT-左转 RIGHT-右转 U_TURN-掉头
     */
    private String turnType;

    /**
     * 转向说明
     */
    private String turnInstruction;

    /**
     * 导航图标类型
     */
    private String naviIcon;

    /**
     * 分段描述
     */
    private String description;

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
