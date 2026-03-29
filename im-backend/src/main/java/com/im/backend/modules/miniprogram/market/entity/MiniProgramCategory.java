package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 小程序分类实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_category")
public class MiniProgramCategory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类编码（顶级为0）
     */
    private String parentCode;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类图标
     */
    private String iconUrl;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 场景类型：1-餐饮 2-生活 3-出行 4-购物 5-健康 6-教育
     */
    private Integer sceneType;

    /**
     * 排序权重
     */
    private Integer sortWeight;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 小程序数量
     */
    private Integer appCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
