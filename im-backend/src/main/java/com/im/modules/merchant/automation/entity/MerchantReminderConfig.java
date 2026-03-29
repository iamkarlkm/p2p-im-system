package com.im.modules.merchant.automation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商户智能提醒配置实体
 */
@Data
@TableName("merchant_reminder_config")
public class MerchantReminderConfig {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String configId;
    
    private String merchantId;
    
    private String reminderRules;
    
    private Boolean pushEnabled;
    
    private Boolean smsEnabled;
    
    private Boolean emailEnabled;
    
    private String dailyReportTime;
    
    private String weeklyReportDay;
    
    private String weeklyReportTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Boolean deleted;
}
