package com.im.backend.modules.miniprogram.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序组件实体
 * 组件市场的组件定义
 */
@Data
@TableName("mini_program_component")
public class MiniProgramComponent {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 组件唯一标识
     */
    private String componentKey;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 组件描述
     */
    private String description;

    /**
     * 组件分类：1-基础组件 2-布局组件 3-表单组件 4-展示组件 5-业务组件 6-营销组件
     */
    private Integer category;

    /**
     * 组件类型：vue/react/native
     */
    private String componentType;

    /**
     * 组件图标
     */
    private String icon;

    /**
     * 组件预览图
     */
    private String previewImage;

    /**
     * 组件代码（WXML/Vue模板）
     */
    private String templateCode;

    /**
     * 组件样式代码（WXSS/CSS）
     */
    private String styleCode;

    /**
     * 组件脚本代码（JS/TS）
     */
    private String scriptCode;

    /**
     * 组件属性配置Schema
     */
    private String propsSchema;

    /**
     * 组件默认数据
     */
    private String defaultData;

    /**
     * 组件事件定义
     */
    private String eventSchema;

    /**
     * 组件插槽定义
     */
    private String slotSchema;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 版本号
     */
    private String version;

    /**
     * 组件状态：0-草稿 1-审核中 2-已发布 3-已下架
     */
    private Integer status;

    /**
     * 下载次数
     */
    private Long downloadCount;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 价格：0-免费 >0-付费
     */
    private BigDecimal price;

    /**
     * 是否是官方组件
     */
    private Boolean isOfficial;

    /**
     * 标签JSON
     */
    private String tags;

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
