package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 创建组件请求
 */
@Data
public class CreateComponentRequest {

    @NotBlank(message = "组件名称不能为空")
    private String componentName;

    private String description;

    @NotNull(message = "组件分类不能为空")
    private Integer category;

    @NotBlank(message = "组件类型不能为空")
    private String componentType;

    /**
     * 组件代码
     */
    private String templateCode;

    /**
     * 样式代码
     */
    private String styleCode;

    /**
     * 脚本代码
     */
    private String scriptCode;

    /**
     * 属性配置Schema
     */
    private Map<String, Object> propsSchema;

    /**
     * 默认数据
     */
    private Map<String, Object> defaultData;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 标签
     */
    private List<String> tags;
}
