package com.im.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Token 刷新请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;

    /**
     * 设备ID（用于验证一致性）
     */
    private String deviceId;
}
