package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开发者实体
 * 小程序开发者认证信息
 */
@Data
@TableName("mini_program_developer")
public class MiniProgramDeveloper {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 开发者昵称
     */
    private String nickname;

    /**
     * 开发者头像
     */
    private String avatar;

    /**
     * 开发者类型：1-个人 2-企业
     */
    private Integer developerType;

    /**
     * 真实姓名/企业名称
     */
    private String realName;

    /**
     * 身份证号/营业执照号
     */
    private String identityNumber;

    /**
     * 认证状态：0-未认证 1-认证中 2-已认证 3-认证失败
     */
    private Integer authStatus;

    /**
     * 开发者等级：1-初级 2-中级 3-高级 4-专家
     */
    private Integer level;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 信誉分
     */
    private Integer creditScore;

    /**
     * 发布组件数量
     */
    private Integer componentCount;

    /**
     * 发布模板数量
     */
    private Integer templateCount;

    /**
     * 发布小程序数量
     */
    private Integer programCount;

    /**
     * 收益余额
     */
    private BigDecimal balance;

    /**
     * 累计收益
     */
    private BigDecimal totalIncome;

    /**
     * 简介
     */
    private String bio;

    /**
     * 个人网站
     */
    private String website;

    /**
     * GitHub地址
     */
    private String githubUrl;

    /**
     * 技能标签JSON
     */
    private String skills;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
