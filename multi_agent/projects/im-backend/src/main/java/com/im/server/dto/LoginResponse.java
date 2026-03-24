package com.im.server.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
}
