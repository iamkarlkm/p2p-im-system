package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 模板响应
 */
@Data
public class TemplateResponse {
    
    private Long id;
    private String templateKey;
    private String templateName;
    private String description;
    private String industry;
    private String industryDesc;
    private String previewImages;
    private String templateConfig;
    private Integer usageCount;
    private Integer sortOrder;
    private Boolean isRecommended;
    private LocalDateTime createTime;
}
