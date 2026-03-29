package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.im.backend.modules.miniprogram.enums.SandboxStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 沙箱环境实例实体
 */
@Data
@TableName("mini_program_sandbox")
public class MiniProgramSandbox {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 应用ID */
    private Long appId;

    /** 沙箱环境ID */
    private String sandboxId;

    /** 沙箱状态 */
    private SandboxStatus status;

    /** 沙箱容器地址 */
    private String containerUrl;

    /** 调试二维码 */
    private String debugQrCode;

    /** 启动时间 */
    private LocalDateTime startTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 日志收集地址 */
    private String logEndpoint;

    /** 性能监控数据 */
    private String performanceData;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
