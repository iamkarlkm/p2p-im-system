package com.im.presence.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 批量订阅状态请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSubscribeRequest {
    
    /**
     * 订阅者用户ID
     */
    @NotNull(message = "订阅者ID不能为空")
    private Long subscriberId;
    
    /**
     * 要订阅的用户ID列表
     */
    @NotEmpty(message = "订阅列表不能为空")
    private List<Long> targetUserIds;
    
    /**
     * 订阅类型: 1-一次性, 2-持续订阅
     */
    @Builder.Default
    private Integer subscribeType = 2;
    
    /**
     * 过期时间(分钟)，null表示不过期
     */
    private Integer expireMinutes;
}
