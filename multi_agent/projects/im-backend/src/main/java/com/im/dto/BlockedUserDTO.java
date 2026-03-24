package com.im.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 黑名单用户数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUserDTO {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 被拉黑用户ID
     */
    private Long blockedId;
    
    /**
     * 被拉黑用户昵称
     */
    private String blockedUsername;
    
    /**
     * 被拉黑用户头像
     */
    private String blockedAvatar;
    
    /**
     * 拉黑原因
     */
    private String reason;
    
    /**
     * 拉黑时间
     */
    private LocalDateTime blockedAt;
    
    /**
     * 是否隐藏在线状态
     */
    private Boolean hideOnlineStatus;
    
    /**
     * 是否静音消息
     */
    private Boolean muteMessages;
}
