package com.im.backend.modules.merchant.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 创建小程序请求DTO - 功能#313: 小程序开发者生态
 */
@Data
public class MiniProgramCreateRequest {

    @NotBlank(message = "小程序名称不能为空")
    private String appName;

    private String iconUrl;

    private String description;

    private String category;

    private String configJson;
}
