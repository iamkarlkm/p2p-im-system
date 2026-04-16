package com.im.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Token 验证请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerifyRequest {

    /**
     * 需要验证的 Token
     */
    @NotBlank(message = "Token cannot be empty")
    private String token;
}
