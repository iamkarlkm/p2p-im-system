package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建页面请求
 */
@Data
public class CreatePageRequest {
    
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    
    @NotBlank(message = "页面名称不能为空")
    private String pageName;
    
    private String pageTitle;
    
    @NotBlank(message = "页面类型不能为空")
    private String pageType;
    
    /** 页面布局配置 */
    private String layoutConfig;
    
    /** 组件列表 */
    private String components;
    
    /** 是否设为首页 */
    private Boolean isHome;
}
