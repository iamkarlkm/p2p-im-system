package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 创建小程序项目请求
 */
@Data
public class CreateProjectRequest {

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    private String description;

    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    /**
     * 基于模板创建时传入
     */
    private Long templateId;

    /**
     * 页面配置
     */
    private Map<String, Object> pageConfig;

    /**
     * 全局样式
     */
    private Map<String, Object> globalStyle;
}
