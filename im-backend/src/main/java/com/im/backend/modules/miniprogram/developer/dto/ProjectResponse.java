package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.time.LocalDateTime;

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
    private Long merchantId;
    private String templateType;
    private Integer status;
    private String statusDesc;
    private String version;
    private String buildPackageUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
}
