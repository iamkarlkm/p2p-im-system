package com.im.presence.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户状态更新请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceUpdateRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 状态: 0-离线, 1-在线, 2-忙碌, 3-离开, 4-隐身
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
    
    /**
     * 自定义状态文本
     */
    private String customStatus;
    
    /**
     * 设备类型: 1-iOS, 2-Android, 3-Web, 4-PC, 5-Mac, 6-iPad
     */
    @NotNull(message = "设备类型不能为空")
    private Integer deviceType;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 设备信息(JSON)
     */
    private String deviceInfo;
    
    /**
     * 是否多端同步
     */
    @Builder.Default
    private Boolean syncToAllDevices = true;
    
    // ============ 便捷构造方法 ============
    
    public static PresenceUpdateRequest online(Long userId, Integer deviceType) {
        return PresenceUpdateRequest.builder()
                .userId(userId)
                .status(1)
                .deviceType(deviceType)
                .build();
    }
    
    public static PresenceUpdateRequest offline(Long userId) {
        return PresenceUpdateRequest.builder()
                .userId(userId)
                .status(0)
                .deviceType(0)
                .build();
    }
}
