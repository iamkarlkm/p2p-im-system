package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 自动化营销任务响应DTO
 */
@Data
@Builder
public class AutoMarketingTaskResponse {
    
    private String taskId;
    
    private String merchantId;
    
    private String taskName;
    
    private String description;
    
    private Integer taskType;
    
    private String taskTypeName;
    
    private Integer status;
    
    private String statusName;
    
    private String triggerCondition;
    
    private LocalDateTime scheduleTime;
    
    private String cronExpression;
    
    private Integer targetUserCount;
    
    private Integer sentCount;
    
    private Integer successCount;
    
    private Integer readCount;
    
    private String messageTemplate;
    
    private List<String> imageUrls;
    
    private Integer priority;
    
    private LocalDateTime lastExecuteTime;
    
    private LocalDateTime nextExecuteTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Map<String, Object> statistics;
}
