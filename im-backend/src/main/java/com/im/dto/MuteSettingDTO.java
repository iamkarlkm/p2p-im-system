package com.im.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 免打扰设置数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuteSettingDTO {
    
    /**
     * 设置ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话ID (null表示全局设置)
     */
    private Long conversationId;
    
    /**
     * 会话类型 (PERSONAL/GROUP)
     */
    private String conversationType;
    
    /**
     * 是否静音
     */
    private Boolean isMuted;
    
    /**
     * 免打扰开始时间
     */
    private String dndStartTime;
    
    /**
     * 免打扰结束时间
     */
    private String dndEndTime;
    
    /**
     * 是否启用免打扰
     */
    private Boolean dndEnabled;
    
    /**
     * 免打扰重复周期
     */
    private String dndRepeatDays;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 当前是否在免打扰时段内
     */
    private Boolean inDndPeriod;
}
