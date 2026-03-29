package com.im.backend.modules.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建沙箱环境请求
 */
@Data
public class CreateSandboxRequest {

    @NotNull(message = "应用ID不能为空")
    private Long appId;

    /** 版本ID(可选，默认使用当前开发版本) */
    private Long versionId;

    /** 过期时间(小时，默认24小时) */
    private Integer expireHours;
}
