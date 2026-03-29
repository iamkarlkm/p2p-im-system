package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置轨迹实体
 * 本地物流配送智能调度引擎 - 实时位置追踪
 */
@Data
@Accessors(chain = true)
@TableName("delivery_rider_location")
public class DeliveryRiderLocation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 骑手ID */
    private Long riderId;

    /** 订单ID */
    private Long orderId;

    /** 纬度 */
    private BigDecimal lat;

    /** 经度 */
    private BigDecimal lng;

    /** 精度(米) */
    private BigDecimal accuracy;

    /** 海拔高度 */
    private BigDecimal altitude;

    /** 速度(m/s) */
    private BigDecimal speed;

    /** 方向(0-360度) */
    private BigDecimal bearing;

    /** GeoHash */
    private String geoHash;

    /** 位置来源: GPS-卫星定位, NETWORK-网络定位, PASSIVE-被动定位 */
    private String source;

    /** 设备电量百分比 */
    private Integer batteryLevel;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
