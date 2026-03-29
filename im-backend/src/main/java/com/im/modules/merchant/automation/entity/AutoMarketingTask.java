package com.im.modules.merchant.automation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 自动化营销任务实体
 * 存储商户的自动化营销任务配置
 */
@Data
@TableName("auto_marketing_task")
public class AutoMarketingTask {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String taskId;
    
    private String merchantId;
    
    private String taskName;
    
    private String description;
    
    private Integer taskType;
    
    private Integer status;
    
    private String triggerCondition;
    
    private LocalDateTime scheduleTime;
    
    private String cronExpression;
    
    private String targetFilter;
    
    private String messageTemplate;
    
    private String mediaUrls;
    
    private String variables;
    
    private Integer targetUserCount;
    
    private Integer sentCount;
    
    private Integer successCount;
    
    private Integer readCount;
    
    private Integer priority;
    
    private LocalDateTime lastExecuteTime;
    
    private LocalDateTime nextExecuteTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Boolean deleted;
}
