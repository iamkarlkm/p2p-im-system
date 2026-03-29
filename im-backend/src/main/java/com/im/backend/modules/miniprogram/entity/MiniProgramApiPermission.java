package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.im.backend.modules.miniprogram.enums.ApiPermissionStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API权限申请实体
 */
@Data
@TableName("mini_program_api_permission")
public class MiniProgramApiPermission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用ID */
    private Long appId;

    /** API接口标识 */
    private String apiCode;

    /** API名称 */
    private String apiName;

    /** API分类 */
    private String apiCategory;

    /** 申请理由 */
    private String applyReason;

    /** 申请状态 */
    private ApiPermissionStatus status;

    /** 审核备注 */
    private String auditRemark;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
