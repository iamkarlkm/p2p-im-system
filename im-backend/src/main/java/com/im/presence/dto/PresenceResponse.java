package com.im.presence.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户状态响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 状态码
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 是否在线
     */
    private Boolean online;
    
    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 最后活跃时间格式化
     */
    private String lastActiveTimeFormatted;
    
    /**
     * 最后登录设备类型
     */
    private Integer lastDeviceType;
    
    /**
     * 最后登录设备描述
     */
    private String lastDeviceDesc;
    
    /**
     * 自定义状态
     */
    private String customStatus;
    
    /**
     * 当前会话数
     */
    private Integer sessionCount;
    
    /**
     * 多端在线设备列表
     */
    private List<DeviceInfo> devices;
    
    /**
     * 离线时长(分钟，离线时有效)
     */
    private Long offlineMinutes;
    
    // ============ 嵌套类 ============
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private Integer deviceType;
        private String deviceTypeDesc;
        private String deviceId;
        private LocalDateTime loginTime;
        private String serverNode;
    }
}
