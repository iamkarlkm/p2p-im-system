package com.im.dto;

import lombok.Data;

/**
 * 登录请求DTO
 * 功能ID: #2
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
