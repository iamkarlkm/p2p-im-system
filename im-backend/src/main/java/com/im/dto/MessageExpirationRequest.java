package com.im.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

/**
 * 消息过期策略请求 DTO
 */
@Data
@Builder
public class MessageExpirationRequest {

    /** 会话ID (群聊) */
    private String conversationId;

    /** 接收者用户ID (私聊) */
    private String receiverId;

    /** 过期类型: DURATION / READ_ONCE / SCHEDULE / OFF */
    @NotBlank(message = "过期类型不能为空")
    private String expirationType;

    /** 过期时长 (秒)，用于 DURATION 和 READ_ONCE 类型 */
    @Min(value = 1, message = "过期时长至少1秒")
    private Long durationSeconds;

    /** 过期时间点，用于 SCHEDULE 类型 */
    private String expireAt;

    /** 是否启用 */
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;
}
