package com.im.backend.modules.merchant.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单出餐确认记录实体
 */
@Data
@TableName("im_order_meal_ready_record")
public class OrderMealReadyRecord {

    @TableId(type = IdType.ASSIGN_ID)
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
     * 操作员工ID
     */
    private Long operatorId;

    /**
     * 出餐照片URL(多图逗号分隔)
     */
    private String mealPhotos;

    /**
     * 出餐备注
     */
    private String remark;

    /**
     * 预计出餐时间(分钟)
     */
    private Integer estimatedWaitMinutes;

    /**
     * 出餐状态: 0-制作中, 1-已出餐
     */
    private Integer status;

    /**
     * 出餐时间
     */
    private LocalDateTime mealReadyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
