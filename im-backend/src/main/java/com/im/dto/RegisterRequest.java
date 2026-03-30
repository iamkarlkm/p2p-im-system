package com.im.dto;

import lombok.Data;

/**
 * 注册请求DTO
 * 功能ID: #2
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
}
