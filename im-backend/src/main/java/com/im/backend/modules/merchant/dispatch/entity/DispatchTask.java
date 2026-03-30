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
 * 调度任务实体
 * Feature #309: Instant Delivery Capacity Dispatch (即时配送运力调度)
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("dispatch_task")
public class DispatchTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 骑手ID
     */
    private Long riderId;

    /**
     * 取货地址
     */
    private String pickupAddress;

    /**
     * 取货经度
     */
    private Double pickupLng;

    /**
     * 取货纬度
     */
    private Double pickupLat;

    /**
     * 送货地址
     */
    private String deliveryAddress;

    /**
     * 送货经度
     */
    private Double deliveryLng;

    /**
     * 送货纬度
     */
    private Double deliveryLat;

    /**
     * 配送距离(公里)
     */
    private BigDecimal distance;

    /**
     * 预估配送时间(分钟)
     */
    private Integer estimatedTime;

    /**
     * 调度状态: 0-待分配, 1-已分配, 2-已接单, 3-配送中, 4-已完成, 5-已取消
     */
    private Integer dispatchStatus;

    /**
     * 调度类型: 1-系统派单, 2-骑手抢单
     */
    private Integer dispatchType;

    /**
     * 优先级: 1-普通, 2-紧急, 3-VIP
     */
    private Integer priority;

    /**
     * 调度时间
     */
    private LocalDateTime dispatchTime;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 调度策略
     */
    private String dispatchStrategy;

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
