package com.im.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Pattern;

/**
 * 免打扰设置请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MuteSettingRequest {
    
    /**
     * 会话ID (null表示全局设置)
     */
    private Long conversationId;
    
    /**
     * 是否静音
     */
    private Boolean isMuted;
    
    /**
     * 免打扰开始时间 (HH:mm格式)
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式必须为 HH:mm")
    private String dndStartTime;
    
    /**
     * 免打扰结束时间 (HH:mm格式)
     */
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "时间格式必须为 HH:mm")
    private String dndEndTime;
    
    /**
     * 是否启用免打扰
     */
    private Boolean dndEnabled;
    
    /**
     * 免打扰重复周期 (e.g., "MON,TUE,WED" 或 "daily")
     */
    private String dndRepeatDays;
}
