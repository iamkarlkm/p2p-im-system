package com.im.backend.modules.delivery.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送调度记录实体
 * 本地物流配送智能调度引擎 - 调度历史记录
 */
@Data
@Accessors(chain = true)
@TableName("delivery_dispatch_record")
public class DeliveryDispatchRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 骑手ID */
    private Long riderId;

    /** 调度类型: AUTO-自动派单, MANUAL-手动派单, REASSIGN-重新分配 */
    private String dispatchType;

    /** 调度状态: SUCCESS-成功, FAILED-失败, PENDING-待确认 */
    private String status;

    /** 调度原因: NEAREST-最近骑手, BATCH-批量顺路, CAPACITY-运力均衡, MANUAL-人工干预 */
    private String reason;

    /** 骑手分配前位置纬度 */
    private BigDecimal riderLatBefore;

    /** 骑手分配前位置经度 */
    private BigDecimal riderLngBefore;

    /** 骑手到商家距离(米) */
    private Integer distanceToMerchant;

    /** 预计到达商家时间(分钟) */
    private Integer etaToMerchant;

    /** 拒绝原因 */
    private String rejectReason;

    /** 操作人ID */
    private Long operatorId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
