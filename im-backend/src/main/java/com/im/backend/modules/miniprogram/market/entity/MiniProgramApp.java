package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序应用实体
 * 本地生活场景化小程序应用市场核心实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_app")
public class MiniProgramApp {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小程序唯一标识
     */
    private String appKey;

    /**
     * 小程序名称
     */
    private String appName;

    /**
     * 小程序描述
     */
    private String description;

    /**
     * 小程序图标
     */
    private String iconUrl;

    /**
     * 截图列表（JSON数组）
     */
    private String screenshots;

    /**
     * 所属开发者ID
     */
    private Long developerId;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 子分类编码
     */
    private String subCategoryCode;

    /**
     * 场景标签（JSON数组）
     */
    private String sceneTags;

    /**
     * 版本号
     */
    private String version;

    /**
     * 状态：0-待审核 1-已上架 2-已下架 3-审核拒绝
     */
    private Integer status;

    /**
     * 评分（1-5分）
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 下载量/使用量
     */
    private Long downloadCount;

    /**
     * 日活跃用户
     */
    private Long dau;

    /**
     * 关联的POI ID
     */
    private Long poiId;

    /**
     * 服务范围：城市编码列表（JSON）
     */
    private String serviceCities;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 推荐权重
     */
    private Integer recommendWeight;

    /**
     * 排序权重
     */
    private Integer sortWeight;

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
