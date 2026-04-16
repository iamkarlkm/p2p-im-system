package com.im.service.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 消息反应请求 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequest {

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private String messageId;

    /**
     * 反应类型 - emoji 字符
     */
    @NotBlank(message = "反应类型不能为空")
    @Size(max = 50, message = "反应类型长度不能超过50")
    private String reactionType;
}
