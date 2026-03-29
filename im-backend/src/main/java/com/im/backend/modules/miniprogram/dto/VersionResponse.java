package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.VersionStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 版本信息响应
 */
@Data
public class VersionResponse {

    private Long id;
    private Long appId;
    private String version;
    private VersionStatus status;
    private String statusDesc;
    private Long codeSize;
    private String commitLog;
    private String auditResult;
    private String rejectReason;
    private LocalDateTime auditTime;
    private LocalDateTime submitAuditTime;
    private LocalDateTime releaseTime;
    private LocalDateTime createTime;
}
