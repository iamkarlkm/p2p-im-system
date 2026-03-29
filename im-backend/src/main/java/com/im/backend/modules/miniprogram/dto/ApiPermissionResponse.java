package com.im.backend.modules.miniprogram.dto;

import lombok.Data;

/**
 * API权限信息响应
 */
@Data
public class ApiPermissionResponse {

    private Long id;
    private Long appId;
    private String apiCode;
    private String apiName;
    private String apiCategory;
    private String applyReason;
    private String status;
    private String statusDesc;
    private String auditRemark;
    private String createTime;
}
