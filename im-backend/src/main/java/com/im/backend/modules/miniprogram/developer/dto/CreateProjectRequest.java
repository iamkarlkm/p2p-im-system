package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建小程序项目请求
 */
@Data
public class CreateProjectRequest {
    
    @NotBlank(message = "项目名称不能为空")
    private String projectName;
    
    private String description;
    
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;
    
    /** 模板类型 */
    private String templateType;
    
    /** 初始页面配置 */
    private String pageConfig;
}
