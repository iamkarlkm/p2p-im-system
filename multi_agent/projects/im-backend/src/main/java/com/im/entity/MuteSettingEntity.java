package com.im.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息免打扰设置实体
 * 支持单会话静音和全局免打扰时段控制
 */
@Data
@Entity
@Table(name = "mute_settings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "conversation_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuteSettingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 会话ID (null表示全局设置)
     */
    @Column(name = "conversation_id")
    private Long conversationId;
    
    /**
     * 是否静音
     */
    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted;
    
    /**
     * 免打扰开始时间 (HH:mm格式)
     */
    @Column(name = "dnd_start_time")
    private String dndStartTime;
    
    /**
     * 免打扰结束时间 (HH:mm格式)
     */
    @Column(name = "dnd_end_time")
    private String dndEndTime;
    
    /**
     * 是否启用全局免打扰
     */
    @Column(name = "dnd_enabled", nullable = false)
    private Boolean dndEnabled;
    
    /**
     * 免打扰重复周期 (e.g., "MON,TUE,WED" 或 "daily")
     */
    @Column(name = "dnd_repeat_days")
    private String dndRepeatDays;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isMuted == null) isMuted = false;
        if (dndEnabled == null) dndEnabled = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 判断当前时间是否在免打扰时段内
     */
    public boolean isInDndPeriod() {
        if (!Boolean.TRUE.equals(dndEnabled)) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        String currentTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
        
        if (dndStartTime != null && dndEndTime != null) {
            // 处理跨天情况
            if (dndStartTime.compareTo(dndEndTime) > 0) {
                // 跨天: 如 22:00 - 08:00
                return currentTime.compareTo(dndStartTime) >= 0 || 
                       currentTime.compareTo(dndEndTime) <= 0;
            } else {
                // 同一天
                return currentTime.compareTo(dndStartTime) >= 0 && 
                       currentTime.compareTo(dndEndTime) <= 0;
            }
        }
        
        return false;
    }
}
