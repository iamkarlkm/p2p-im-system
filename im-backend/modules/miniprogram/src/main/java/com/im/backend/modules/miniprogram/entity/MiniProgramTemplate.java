package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序模板实体
 * 预设场景模板
 */
@Data
@TableName("mini_program_template")
public class MiniProgramTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板唯一标识
     */
    private String templateKey;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板分类：1-餐饮 2-零售 3-服务 4-娱乐 5-教育 6-医疗 7-其他
     */
    private Integer category;

    /**
     * 行业标签
     */
    private String industryTags;

    /**
     * 模板缩略图
     */
    private String thumbnail;

    /**
     * 预览图列表JSON
     */
    private String previewImages;

    /**
     * 页面配置JSON
     */
    private String pageConfig;

    /**
     * 全局样式配置
     */
    private String globalStyle;

    /**
     * 页面列表JSON（包含完整页面配置）
     */
    private String pages;

    /**
     * 使用的组件列表
     */
    private String usedComponents;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 模板状态：0-草稿 1-审核中 2-已发布 3-已下架
     */
    private Integer status;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 是否是官方模板
     */
    private Boolean isOfficial;

    /**
     * 是否是VIP模板
     */
    private Boolean isVip;

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
