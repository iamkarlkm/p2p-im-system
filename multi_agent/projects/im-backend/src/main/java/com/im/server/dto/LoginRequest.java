package com.im.server.dto;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
