package com.im.service.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 重置密码请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    /**
     * 重置令牌
     */
    @NotBlank(message = "Reset token cannot be empty")
    private String token;

    /**
     * 新密码
     */
    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", 
             message = "Password must contain at least one lowercase letter, one uppercase letter, and one number")
    private String newPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "Confirm password cannot be empty")
    private String confirmPassword;
}
