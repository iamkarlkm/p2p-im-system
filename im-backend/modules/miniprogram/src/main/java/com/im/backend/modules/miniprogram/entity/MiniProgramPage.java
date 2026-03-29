package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 小程序页面实体
 * 存储页面配置和组件树
 */
@Data
@TableName("mini_program_page")
public class MiniProgramPage {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 页面唯一标识
     */
    private String pageKey;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面标题
     */
    private String pageTitle;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 页面路径
     */
    private String pagePath;

    /**
     * 页面类型：1-首页 2-列表页 3-详情页 4-表单页 5-自定义
     */
    private Integer pageType;

    /**
     * 页面状态：0-草稿 1-已发布
     */
    private Integer status;

    /**
     * 组件树JSON（低代码配置）
     */
    private String componentTree;

    /**
     * 页面样式配置
     */
    private String pageStyle;

    /**
     * 页面数据绑定配置
     */
    private String dataBinding;

    /**
     * 生命周期配置
     */
    private String lifecycleConfig;

    /**
     * 事件配置
     */
    private String eventConfig;

    /**
     * 页面排序
     */
    private Integer sortOrder;

    /**
     * 是否是首页
     */
    private Boolean isHomePage;

    /**
     * 页面缩略图
     */
    private String thumbnail;

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
