package com.im.presence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户状态变更记录 - 用于状态历史追踪和分析
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_presence_history")
public class PresenceHistory {
    
    /**
     * 记录ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 变更前状态
     */
    private Integer oldStatus;
    
    /**
     * 变更后状态
     */
    private Integer newStatus;
    
    /**
     * 变更原因: 1-主动变更, 2-超时离线, 3-登出, 4-被踢出, 5-系统恢复
     */
    private Integer changeReason;
    
    /**
     * 变更时间
     */
    private LocalDateTime changeTime;
    
    /**
     * 设备类型
     */
    private Integer deviceType;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 地理位置
     */
    private String location;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // ============ 变更原因常量 ============
    
    public static final int REASON_MANUAL = 1;
    public static final int REASON_TIMEOUT = 2;
    public static final int REASON_LOGOUT = 3;
    public static final int REASON_KICKED = 4;
    public static final int REASON_SYSTEM = 5;
}
