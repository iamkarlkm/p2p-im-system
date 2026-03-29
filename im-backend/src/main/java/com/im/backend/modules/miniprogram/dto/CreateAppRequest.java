package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.AppCategory;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建应用请求
 */
@Data
public class CreateAppRequest {

    @NotBlank(message = "应用名称不能为空")
    private String appName;

    private String appIcon;

    private String description;

    @NotNull(message = "应用分类不能为空")
    private AppCategory category;

    /** 服务器域名列表(逗号分隔) */
    private String serverDomains;

    /** 业务域名列表(逗号分隔) */
    private String businessDomains;
}
