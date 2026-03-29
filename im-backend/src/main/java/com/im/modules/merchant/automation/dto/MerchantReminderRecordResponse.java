package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商户提醒记录响应DTO
 */
@Data
@Builder
public class MerchantReminderRecordResponse {
    
    private String recordId;
    
    private String merchantId;
    
    private String ruleId;
    
    private String ruleName;
    
    private String ruleType;
    
    private String ruleTypeName;
    
    private String reminderTitle;
    
    private String reminderContent;
    
    private Integer priority;
    
    private String notifyChannel;
    
    private Integer status;
    
    private String statusName;
    
    private String relatedData;
    
    private LocalDateTime sendTime;
    
    private LocalDateTime readTime;
    
    private LocalDateTime createTime;
}
