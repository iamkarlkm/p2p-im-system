package com.im.presence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户在线状态实体 - 实时在线状态服务核心实体
 * 
 * 功能: 用户在线/离线状态检测、状态订阅与推送、多端状态同步
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user_presence")
public class UserPresence {
    
    /**
     * 用户ID
     */
    @TableId(type = IdType.INPUT)
    private Long userId;
    
    /**
     * 在线状态: 0-离线, 1-在线, 2-忙碌, 3-离开, 4-隐身
     */
    private Integer status;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 最后活跃时间戳(毫秒)
     */
    private Long lastActiveTimestamp;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 最后登出时间
     */
    private LocalDateTime lastLogoutTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 登录设备类型: 1-iOS, 2-Android, 3-Web, 4-PC, 5-Mac, 6-iPad
     */
    private Integer lastDeviceType;
    
    /**
     * 最后登录设备ID
     */
    private String lastDeviceId;
    
    /**
     * 当前连接的服务器节点
     */
    private String serverNode;
    
    /**
     * 当前会话数(多端登录)
     */
    private Integer sessionCount;
    
    /**
     * 多端在线详情(JSON格式)
     */
    private String deviceDetails;
    
    /**
     * 自定义状态文本
     */
    private String customStatus;
    
    /**
     * 状态过期时间(离开状态自动恢复)
     */
    private LocalDateTime statusExpireTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // ============ 状态常量 ============
    
    public static final int STATUS_OFFLINE = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_BUSY = 2;
    public static final int STATUS_AWAY = 3;
    public static final int STATUS_INVISIBLE = 4;
    
    public static final int DEVICE_IOS = 1;
    public static final int DEVICE_ANDROID = 2;
    public static final int DEVICE_WEB = 3;
    public static final int DEVICE_PC = 4;
    public static final int DEVICE_MAC = 5;
    public static final int DEVICE_IPAD = 6;
    
    // ============ 业务方法 ============
    
    /**
     * 是否在线
     */
    public boolean isOnline() {
        return status != null && (status == STATUS_ONLINE || status == STATUS_BUSY || status == STATUS_AWAY);
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        switch (status != null ? status : STATUS_OFFLINE) {
            case STATUS_ONLINE: return "在线";
            case STATUS_BUSY: return "忙碌";
            case STATUS_AWAY: return "离开";
            case STATUS_INVISIBLE: return "隐身";
            default: return "离线";
        }
    }
    
    /**
     * 获取设备类型描述
     */
    public String getDeviceTypeDesc() {
        switch (lastDeviceType != null ? lastDeviceType : 0) {
            case DEVICE_IOS: return "iPhone";
            case DEVICE_ANDROID: return "Android";
            case DEVICE_WEB: return "Web";
            case DEVICE_PC: return "Windows";
            case DEVICE_MAC: return "Mac";
            case DEVICE_IPAD: return "iPad";
            default: return "未知";
        }
    }
    
    /**
     * 计算离线时长(分钟)
     */
    public long getOfflineMinutes() {
        if (isOnline() || lastActiveTime == null) {
            return 0;
        }
        return java.time.Duration.between(lastActiveTime, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * 更新活跃时间
     */
    public void updateActiveTime() {
        this.lastActiveTime = LocalDateTime.now();
        this.lastActiveTimestamp = System.currentTimeMillis();
    }
}
