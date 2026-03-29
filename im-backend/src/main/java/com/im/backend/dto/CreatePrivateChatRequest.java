package com.im.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 创建单聊会话请求DTO
 */
@Schema(description = "创建单聊会话请求")
public class CreatePrivateChatRequest {

    @NotNull(message = "目标用户ID不能为空")
    @Schema(description = "目标用户ID", required = true, example = "2")
    private Long targetUserId;

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
