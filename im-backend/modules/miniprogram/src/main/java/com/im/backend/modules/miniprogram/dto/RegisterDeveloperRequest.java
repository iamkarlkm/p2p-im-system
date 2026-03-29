package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 注册开发者请求
 */
@Data
public class RegisterDeveloperRequest {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotNull(message = "开发者类型不能为空")
    private Integer developerType;

    private String realName;

    private String identityNumber;

    private String bio;

    private String website;

    private String githubUrl;
}
