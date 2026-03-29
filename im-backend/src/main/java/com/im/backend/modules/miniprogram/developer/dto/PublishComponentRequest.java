package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 发布组件请求
 */
@Data
public class PublishComponentRequest {
    
    @NotBlank(message = "组件名称不能为空")
    private String componentName;
    
    private String description;
    
    @NotBlank(message = "组件分类不能为空")
    private String category;
    
    private String icon;
    private String previewImages;
    
    @NotBlank(message = "组件代码不能为空")
    private String codePackage;
    
    private String defaultConfig;
    private String propsConfig;
    private String eventConfig;
    
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
}
