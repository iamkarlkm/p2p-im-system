package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 小程序开发者实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_developer")
public class MiniProgramDeveloper {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 开发者名称
     */
    private String developerName;

    /**
     * 开发者类型：1-个人 2-企业
     */
    private Integer developerType;

    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 统一社会信用代码
     */
    private String businessLicense;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 开发者等级：1-普通 2-认证 3-优质 4-金牌
     */
    private Integer developerLevel;

    /**
     * 认证状态：0-未认证 1-认证中 2-已认证 3-认证失败
     */
    private Integer authStatus;

    /**
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 发布的小程序数量
     */
    private Integer appCount;

    /**
     * 总收入（分）
     */
    private Long totalIncome;

    /**
     * 余额（分）
     */
    private Long balance;

    /**
     * 评分（1-5分）
     */
    private Double rating;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
