package com.im.service.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息过期时间响应 DTO
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpirationTimeResponse {

    /** 消息ID */
    private String messageId;

    /** 是否启用过期 */
    private Boolean expirationEnabled;

    /** 过期时间 */
    private Long remainingSeconds;

    /** 过期时间戳 */
    private Long expirationTimestamp;
}
