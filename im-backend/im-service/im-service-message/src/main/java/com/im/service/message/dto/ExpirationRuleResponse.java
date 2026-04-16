package com.im.service.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 过期规则响应 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationRuleResponse {

    /** 规则ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 会话ID */
    private String conversationId;

    /** 规则类型 */
    private String ruleType;

    /** 过期时间(秒) */
    private Integer expirationSeconds;

    /** 是否启用 */
    private Boolean enabled;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
