package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 组件市场响应
 */
@Data
public class ComponentResponse {
    
    private Long id;
    private String componentKey;
    private String componentName;
    private String description;
    private String category;
    private String categoryDesc;
    private String icon;
    private String previewImages;
    private String defaultConfig;
    private String propsConfig;
    private Long developerId;
    private String developerName;
    private String version;
    private BigDecimal price;
    private Integer downloadCount;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer status;
    private LocalDateTime createTime;
}
