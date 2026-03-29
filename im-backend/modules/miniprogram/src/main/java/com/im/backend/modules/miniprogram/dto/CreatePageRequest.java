package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 创建页面请求
 */
@Data
public class CreatePageRequest {

    @NotBlank(message = "页面名称不能为空")
    private String pageName;

    private String pageTitle;

    @NotNull(message = "所属项目不能为空")
    private Long projectId;

    @NotNull(message = "页面类型不能为空")
    private Integer pageType;

    /**
     * 是否是首页
     */
    private Boolean isHomePage;

    /**
     * 组件树配置
     */
    private Map<String, Object> componentTree;

    /**
     * 页面样式
     */
    private Map<String, Object> pageStyle;

    /**
     * 数据绑定配置
     */
    private Map<String, Object> dataBinding;
}
