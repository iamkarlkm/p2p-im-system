package com.im.backend.modules.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置轨迹实体类
 * 用于记录骑手实时位置轨迹
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rider_location_trace")
public class RiderLocationTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 记录ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 骑手ID */
    private Long riderId;

    /** 订单ID */
    private Long orderId;

    /** 经度 */
    private BigDecimal longitude;

    /** 纬度 */
    private BigDecimal latitude;

    /** 速度(km/h) */
    private BigDecimal speed;

    /** 方向(0-360度) */
    private Integer direction;

    /** 精度(米) */
    private BigDecimal accuracy;

    /** 海拔(米) */
    private BigDecimal altitude;

    /** 地址描述 */
    private String address;

    /** 上报时间 */
    private LocalDateTime reportTime;

    /** 上报方式: 1-GPS 2-基站 3-WiFi */
    private Integer reportType;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
