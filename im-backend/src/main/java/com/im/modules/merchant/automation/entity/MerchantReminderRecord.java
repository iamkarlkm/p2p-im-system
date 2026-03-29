package com.im.modules.merchant.automation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 智能提醒记录实体
 */
@Data
@TableName("merchant_reminder_record")
public class MerchantReminderRecord {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String recordId;
    
    private String merchantId;
    
    private String ruleId;
    
    private String ruleName;
    
    private String ruleType;
    
    private String reminderTitle;
    
    private String reminderContent;
    
    private Integer priority;
    
    private String notifyChannel;
    
    private Integer status;
    
    private String relatedData;
    
    private LocalDateTime sendTime;
    
    private LocalDateTime readTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Boolean deleted;
}
