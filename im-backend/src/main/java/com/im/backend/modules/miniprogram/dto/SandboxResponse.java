package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.SandboxStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 沙箱环境响应
 */
@Data
public class SandboxResponse {

    private Long id;
    private Long appId;
    private String sandboxId;
    private SandboxStatus status;
    private String statusDesc;
    private String containerUrl;
    private String debugQrCode;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private String logEndpoint;
    private String performanceData;
    private LocalDateTime createTime;
}
