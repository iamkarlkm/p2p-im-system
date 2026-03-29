package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.im.backend.modules.miniprogram.enums.AppCategory;
import com.im.backend.modules.miniprogram.enums.AppStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序应用实体
 */
@Data
@TableName("mini_program_app")
public class MiniProgramApp {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用ID(唯一标识) */
    private String appId;

    /** 应用密钥 */
    private String appSecret;

    /** 开发者ID */
    private Long developerId;

    /** 应用名称 */
    private String appName;

    /** 应用图标 */
    private String appIcon;

    /** 应用描述 */
    private String description;

    /** 应用分类 */
    private AppCategory category;

    /** 应用状态 */
    private AppStatus status;

    /** 当前版本号 */
    private String currentVersion;

    /** 当前版本ID */
    private Long currentVersionId;

    /** 灰度发布比例(0-100) */
    private Integer grayReleasePercent;

    /** 沙箱环境配置 */
    private String sandboxConfig;

    /** 生产环境配置 */
    private String productionConfig;

    /** API权限列表(逗号分隔) */
    private String apiPermissions;

    /** 服务器域名白名单 */
    private String serverDomains;

    /** 业务域名白名单 */
    private String businessDomains;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
