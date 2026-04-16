package com.im.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Token 验证响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerifyResponse {

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限列表
     */
    private List<String> authorities;

    /**
     * 过期时间（时间戳）
     */
    private Long expirationTime;
}
