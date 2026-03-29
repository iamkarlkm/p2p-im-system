package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 小程序项目响应
 */
@Data
public class ProjectResponse {

    private Long id;
    private String projectKey;
    private String projectName;
    private String description;
    private Long developerId;
    private Integer projectType;
    private Integer status;
    private String statusDesc;
    private String version;
    private String previewQrCode;
    private String thumbnail;
    private Long templateId;
    private Boolean enabled;
    private Long visitCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 页面列表
     */
    private List<PageResponse> pages;

    /**
     * 项目统计
     */
    private ProjectStatistics statistics;
}
