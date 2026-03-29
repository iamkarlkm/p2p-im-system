package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.im.backend.modules.miniprogram.enums.VersionStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序版本实体
 */
@Data
@TableName("mini_program_version")
public class MiniProgramVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用ID */
    private Long appId;

    /** 版本号 */
    private String version;

    /** 版本状态 */
    private VersionStatus status;

    /** 代码包下载地址 */
    private String codeUrl;

    /** 代码包MD5 */
    private String codeMd5;

    /** 代码包大小(字节) */
    private Long codeSize;

    /** 提交日志 */
    private String commitLog;

    /** 代码审核结果 */
    private String auditResult;

    /** 审核拒绝原因 */
    private String rejectReason;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 提交审核时间 */
    private LocalDateTime submitAuditTime;

    /** 发布时间 */
    private LocalDateTime releaseTime;

    /** 灰度发布配置 */
    private String grayConfig;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
