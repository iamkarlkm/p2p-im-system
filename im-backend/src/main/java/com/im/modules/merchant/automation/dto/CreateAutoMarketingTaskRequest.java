package com.im.modules.merchant.automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 创建自动化营销任务请求DTO
 */
@Data
public class CreateAutoMarketingTaskRequest {
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    @NotBlank(message = "任务名称不能为空")
    private String taskName;
    
    private String description;
    
    @NotNull(message = "任务类型不能为空")
    private Integer taskType;
    
    private String triggerCondition;
    
    private LocalDateTime scheduleTime;
    
    private String cronExpression;
    
    private List<String> targetUserIds;
    
    private String targetFilter;
    
    private String messageTemplate;
    
    private List<String> imageUrls;
    
    private Map<String, Object> variables;
    
    private Integer priority = 5;
}
