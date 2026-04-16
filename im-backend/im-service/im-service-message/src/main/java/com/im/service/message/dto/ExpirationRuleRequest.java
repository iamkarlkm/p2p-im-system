package com.im.service.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 创建过期规则请求 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleRequest {

    /**
     * 会话ID - 可选，为空表示全局规则
     */
    private String conversationId;

    /**
     * 规则类型: GLOBAL, CONVERSATION, MESSAGE
     */
    @NotNull(message = "规则类型不能为空")
    private String ruleType;

    /**
     * 过期时间(秒) - 0 表示不自动销毁
     */
    @NotNull(message = "过期时间不能为空")
    @PositiveOrZero(message = "过期时间不能为负数")
    private Integer expirationSeconds;
}
