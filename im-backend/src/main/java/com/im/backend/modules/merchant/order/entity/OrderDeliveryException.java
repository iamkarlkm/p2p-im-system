package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单配送异常记录实体
 */
@Data
@TableName("im_order_delivery_exception")
public class OrderDeliveryException {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 异常类型: 1-联系不上顾客, 2-地址错误, 3-顾客拒收, 4-车辆故障, 5-交通事故, 6-天气原因, 7-其他
     */
    private Integer exceptionType;

    /**
     * 异常描述
     */
    private String description;

    /**
     * 异常照片URL
     */
    private String exceptionPhotos;

    /**
     * 处理状态: 0-待处理, 1-处理中, 2-已解决, 3-已转单
     */
    private Integer handleStatus;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 上报时间
     */
    private LocalDateTime reportTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
