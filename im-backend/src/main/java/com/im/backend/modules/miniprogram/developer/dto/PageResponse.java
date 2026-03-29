package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 页面配置响应
 */
@Data
public class PageResponse {
    
    private Long id;
    private Long projectId;
    private String pageKey;
    private String pageName;
    private String pageTitle;
    private String pagePath;
    private String pageType;
    private String layoutConfig;
    private String components;
    private Integer sortOrder;
    private Boolean isHome;
    private Boolean isEnabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
