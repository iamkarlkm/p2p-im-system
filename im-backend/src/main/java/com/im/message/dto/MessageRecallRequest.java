package com.im.message.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 消息撤回请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecallRequest {
    
    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
    
    /**
     * 消息UUID(可选，用于幂等)
     */
    private String messageUuid;
    
    /**
     * 操作者ID
     */
    @NotNull(message = "操作者ID不能为空")
    private Long operatorId;
    
    /**
     * 撤回原因
     */
    private String reason;
    
    /**
     * 是否静默撤回(不通知对方)
     */
    @Builder.Default
    private Boolean silent = false;
    
    /**
     * 客户端设备信息
     */
    private String deviceInfo;
    
    // ============ 便捷构造方法 ============
    
    public static MessageRecallRequest of(Long messageId, Long operatorId) {
        return MessageRecallRequest.builder()
                .messageId(messageId)
                .operatorId(operatorId)
                .build();
    }
}
