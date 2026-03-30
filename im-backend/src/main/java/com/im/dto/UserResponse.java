package com.im.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 功能ID: #2
 */
@Data
public class UserResponse {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private Integer status;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
