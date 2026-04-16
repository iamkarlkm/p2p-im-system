package com.im.service.auth.dto;

import lombok.Data;

/**
 * 刷新Token请求 DTO
 */
@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
