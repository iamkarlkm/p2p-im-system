package com.im.backend.modules.delivery.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送任务实体
 */
@Data
@TableName("delivery_task")
public class DeliveryTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskNo;
    
    private Long orderId;
    
    private Long riderId;
    
    private Long merchantId;
    
    private Long userId;
    
    private String merchantName;
    
    private String merchantAddress;
    
    private BigDecimal merchantLat;
    
    private BigDecimal merchantLng;
    
    private String userAddress;
    
    private BigDecimal userLat;
    
    private BigDecimal userLng;
    
    private String userPhone;
    
    private String userName;
    
    private Integer status;
    
    private Integer priority;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal tipAmount;
    
    private Integer estimatedTime;
    
    private LocalDateTime pickupTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime completedTime;
    
    private String remark;
    
    private String cancelReason;
    
    private Integer alertCount;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
