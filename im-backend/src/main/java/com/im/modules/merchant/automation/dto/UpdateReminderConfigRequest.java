package com.im.modules.merchant.automation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 更新商户智能提醒配置请求DTO
 */
@Data
public class UpdateReminderConfigRequest {
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    private List<ReminderRuleConfig> reminderRules;
    
    private Boolean pushEnabled = true;
    
    private Boolean smsEnabled = false;
    
    private Boolean emailEnabled = false;
    
    private String dailyReportTime = "09:00";
    
    private String weeklyReportDay = "MON";
    
    private String weeklyReportTime = "09:00";
    
    @Data
    public static class ReminderRuleConfig {
        private String ruleId;
        private String ruleName;
        private String ruleType;
        private Boolean enabled;
        private String condition;
        private String messageTemplate;
        private List<String> notifyChannels;
    }
}
