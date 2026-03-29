package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 模板响应
 */
@Data
public class TemplateResponse {

    private Long id;
    private String templateKey;
    private String templateName;
    private String description;
    private Integer category;
    private String categoryDesc;
    private String thumbnail;
    private List<String> previewImages;
    private Long authorId;
    private String authorName;
    private String version;
    private Integer status;
    private Long usageCount;
    private Boolean isOfficial;
    private Boolean isVip;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 页面列表（详情时返回）
     */
    private List<Map<String, Object>> pages;
}
