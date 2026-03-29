package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 创建小程序请求DTO
 */
@Data
public class CreateMiniProgramRequest {

    @NotBlank(message = "小程序名称不能为空")
    @Size(max = 50, message = "名称长度不能超过50字符")
    private String appName;

    @NotBlank(message = "描述不能为空")
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;

    @NotBlank(message = "分类编码不能为空")
    private String categoryCode;

    private String subCategoryCode;

    /**
     * 场景标签
     */
    private List<String> sceneTags;

    /**
     * 关联POI ID
     */
    private Long poiId;

    /**
     * 服务城市编码列表
     */
    private List<String> serviceCities;
}
