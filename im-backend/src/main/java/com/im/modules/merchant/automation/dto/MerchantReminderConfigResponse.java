package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商户智能提醒配置DTO
 */
@Data
@Builder
public class MerchantReminderConfigResponse {
    
    private String configId;
    
    private String merchantId;
    
    private List<ReminderRule> reminderRules;
    
    private String notifyChannel;
    
    private Boolean pushEnabled;
    
    private Boolean smsEnabled;
    
    private Boolean emailEnabled;
    
    private String dailyReportTime;
    
    private String weeklyReportDay;
    
    private String weeklyReportTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Data
    @Builder
    public static class ReminderRule {
        private String ruleId;
        private String ruleName;
        private String ruleType;
        private Boolean enabled;
        private String condition;
        private String messageTemplate;
        private List<String> notifyChannels;
    }
}
