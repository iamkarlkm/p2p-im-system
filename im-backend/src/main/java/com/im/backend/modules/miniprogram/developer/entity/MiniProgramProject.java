package com.im.backend.modules.miniprogram.developer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序项目实体
 * 低代码平台创建的小程序项目
 */
@Data
@TableName("mini_program_project")
public class MiniProgramProject {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 项目唯一标识 */
    private String projectKey;
    
    /** 项目名称 */
    private String projectName;
    
    /** 项目描述 */
    private String description;
    
    /** 所属开发者ID */
    private Long developerId;
    
    /** 商户ID（关联本地生活商户） */
    private Long merchantId;
    
    /** 项目模板类型 */
    private String templateType;
    
    /** 项目状态：0-草稿 1-开发中 2-审核中 3-已发布 4-已下线 */
    private Integer status;
    
    /** 项目版本号 */
    private String version;
    
    /** 页面配置JSON */
    private String pageConfig;
    
    /** 全局配置JSON */
    private String globalConfig;
    
    /** 发布配置 */
    private String publishConfig;
    
    /** 编译后代码包URL */
    private String buildPackageUrl;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 发布时间 */
    private LocalDateTime publishTime;
}
