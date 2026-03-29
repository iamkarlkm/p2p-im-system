package com.im.backend.modules.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 加入位置共享请求DTO
 */
@Data
public class JoinLocationShareRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 邀请码(可选)
     */
    private String inviteCode;
}
