package com.im.backend.modules.merchant.dispatch.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运力资源实体
 * Feature #309: Instant Delivery Capacity Dispatch (即时配送运力调度)
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("delivery_capacity_resource")
public class DeliveryCapacityResource {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 骑手名称
     */
    private String riderName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 当前位置经度
     */
    private Double currentLng;

    /**
     * 当前位置纬度
     */
    private Double currentLat;

    /**
     * 位置更新时间
     */
    private LocalDateTime locationUpdateTime;

    /**
     * 运力状态: 0-离线, 1-空闲, 2-接单中, 3-配送中, 4-休息中
     */
    private Integer status;

    /**
     * 当前订单数
     */
    private Integer currentOrders;

    /**
     * 最大接单数
     */
    private Integer maxOrders;

    /**
     * 服务区域ID
     */
    private Long serviceAreaId;

    /**
     * 配送类型: 1-即时送, 2-预约送
     */
    private Integer deliveryType;

    /**
     * 今日完成订单数
     */
    private Integer todayCompletedOrders;

    /**
     * 今日配送里程(公里)
     */
    private BigDecimal todayMileage;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 在线时长(分钟)
     */
    private Integer onlineMinutes;

    /**
     * 上次派单时间
     */
    private LocalDateTime lastDispatchTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
