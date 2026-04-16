package com.im.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 刷新响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {

    /**
     * 新的 Access Token
     */
    private String accessToken;

    /**
     * 新的 Refresh Token
     */
    private String refreshToken;

    /**
     * Token 类型（Bearer）
     */
    private String tokenType;

    /**
     * Access Token 过期时间（秒）
     */
    private Long expiresIn;
}
