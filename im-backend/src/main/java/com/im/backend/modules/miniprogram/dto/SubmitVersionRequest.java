package com.im.backend.modules.miniprogram.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 提交版本审核请求
 */
@Data
public class SubmitVersionRequest {

    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @NotBlank(message = "版本号不能为空")
    private String version;

    /** 代码包Base64 */
    private String codeBase64;

    /** 代码包下载地址 */
    private String codeUrl;

    @NotBlank(message = "提交日志不能为空")
    private String commitLog;
}
