package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序项目实体
 * 低代码搭建平台的核心项目实体
 */
@Data
@TableName("mini_program_project")
public class MiniProgramProject {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 项目唯一标识
     */
    private String projectKey;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 开发者ID
     */
    private Long developerId;

    /**
     * 项目类型：1-餐饮 2-零售 3-服务 4-娱乐 5-其他
     */
    private Integer projectType;

    /**
     * 项目状态：0-草稿 1-开发中 2-审核中 3-已发布 4-已下架
     */
    private Integer status;

    /**
     * 项目版本号
     */
    private String version;

    /**
     * 页面配置JSON
     */
    private String pageConfig;

    /**
     * 全局样式配置
     */
    private String globalStyle;

    /**
     * 应用配置JSON
     */
    private String appConfig;

    /**
     * 使用的组件列表JSON
     */
    private String usedComponents;

    /**
     * 预览二维码URL
     */
    private String previewQrCode;

    /**
     * 项目缩略图
     */
    private String thumbnail;

    /**
     * 模板ID（基于模板创建）
     */
    private Long templateId;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 访问量
     */
    private Long visitCount;

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
