package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.DeveloperType;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 注册开发者请求
 */
@Data
public class RegisterDeveloperRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "开发者类型不能为空")
    private DeveloperType developerType;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "身份证号不能为空")
    private String identityNumber;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
