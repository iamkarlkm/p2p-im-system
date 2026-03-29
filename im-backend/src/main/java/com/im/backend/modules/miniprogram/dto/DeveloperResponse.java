package com.im.backend.modules.miniprogram.dto;

import com.im.backend.modules.miniprogram.enums.DeveloperStatus;
import com.im.backend.modules.miniprogram.enums.DeveloperType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开发者信息响应
 */
@Data
public class DeveloperResponse {

    private Long id;
    private String nickname;
    private DeveloperType developerType;
    private String developerTypeDesc;
    private String realName;
    private String phone;
    private String email;
    private DeveloperStatus status;
    private String statusDesc;
    private Boolean verified;
    private LocalDateTime verifiedTime;
    private Integer appCount;
    private Integer apiQuota;
    private Integer usedQuota;
    private LocalDateTime createTime;
}
