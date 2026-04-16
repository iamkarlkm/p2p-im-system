package com.im.service.user.dto;

import lombok.Data;

/**
 * 密码修改请求DTO
 */
@Data
public class ChangePasswordRequest {

    private String oldPassword;
    private String newPassword;
}
