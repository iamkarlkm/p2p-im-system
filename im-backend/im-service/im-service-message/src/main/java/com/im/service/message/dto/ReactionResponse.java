package com.im.service.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息反应响应 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactionResponse {

    /**
     * 反应ID
     */
    private String id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 反应类型 - emoji 字符
     */
    private String reactionType;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否当前用户的反应
     */
    private Boolean isCurrentUser;
}
