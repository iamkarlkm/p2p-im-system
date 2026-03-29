package com.im.backend.modules.miniprogram.developer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开发者信息实体
 * 小程序开发者认证信息
 */
@Data
@TableName("mini_program_developer")
public class MiniProgramDeveloper {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 开发者类型：personal-个人 enterprise-企业 */
    private String developerType;
    
    /** 开发者名称 */
    private String developerName;
    
    /** 联系电话 */
    private String contactPhone;
    
    /** 联系邮箱 */
    private String contactEmail;
    
    /** 实名认证状态：0-未认证 1-认证中 2-已认证 */
    private Integer verifyStatus;
    
    /** 认证资料 */
    private String verifyInfo;
    
    /** 开发者等级：1-初级 2-中级 3-高级 4-专家 */
    private Integer level;
    
    /** 积分 */
    private Integer points;
    
    /** 收益余额 */
    private BigDecimal balance;
    
    /** 累计收益 */
    private BigDecimal totalEarnings;
    
    /** 发布组件数 */
    private Integer componentCount;
    
    /** 发布小程序数 */
    private Integer programCount;
    
    /** 状态：0-禁用 1-正常 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
