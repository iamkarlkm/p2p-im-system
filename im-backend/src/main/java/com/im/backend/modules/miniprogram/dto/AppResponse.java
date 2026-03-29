package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.AppCategory;
import com.im.backend.modules.miniprogram.enums.AppStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用信息响应
 */
@Data
public class AppResponse {

    private Long id;
    private String appId;
    private String appName;
    private String appIcon;
    private String description;
    private AppCategory category;
    private String categoryDesc;
    private AppStatus status;
    private String statusDesc;
    private String currentVersion;
    private Integer grayReleasePercent;
    private String serverDomains;
    private String businessDomains;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
