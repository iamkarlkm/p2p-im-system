package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.im.backend.modules.miniprogram.enums.DeveloperStatus;
import com.im.backend.modules.miniprogram.enums.DeveloperType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序开发者实体
 */
@Data
@TableName("mini_program_developer")
public class MiniProgramDeveloper {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 开发者用户ID */
    private Long userId;

    /** 开发者昵称 */
    private String nickname;

    /** 开发者类型: INDIVIDUAL-个人, ENTERPRISE-企业 */
    private DeveloperType developerType;

    /** 真实姓名/企业名称 */
    private String realName;

    /** 身份证号/营业执照号 */
    private String identityNumber;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 开发者状态 */
    private DeveloperStatus status;

    /** 认证状态 */
    private Boolean verified;

    /** 认证时间 */
    private LocalDateTime verifiedTime;

    /** 拒绝原因 */
    private String rejectReason;

    /** 创建的应用数量 */
    private Integer appCount;

    /** API调用配额(次/天) */
    private Integer apiQuota;

    /** 已使用配额 */
    private Integer usedQuota;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
