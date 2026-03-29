package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 页面响应
 */
@Data
public class PageResponse {

    private Long id;
    private String pageKey;
    private String pageName;
    private String pageTitle;
    private Long projectId;
    private String pagePath;
    private Integer pageType;
    private String pageTypeDesc;
    private Integer status;
    private Boolean isHomePage;
    private String thumbnail;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 组件树
     */
    private Map<String, Object> componentTree;

    /**
     * 页面样式
     */
    private Map<String, Object> pageStyle;
}
