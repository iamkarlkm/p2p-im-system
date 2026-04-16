package com.im.service.auth.dto;

import lombok.Data;
import java.util.List;

/**
 * 登录响应 DTO
 */
@Data
public class LoginResponse {
    private String userId;
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
    private List<String> permissions;
}
