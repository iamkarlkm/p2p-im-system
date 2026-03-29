package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 小程序分发记录实体（用于统计和推荐优化）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_distribution")
public class MiniProgramDistribution {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小程序ID
     */
    private Long appId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 分发渠道：1-推荐 2-搜索 3-分类 4-分享 5-广告
     */
    private Integer channel;

    /**
     * 场景标签
     */
    private String sceneTag;

    /**
     * 用户当前位置（GeoHash）
     */
    private String locationHash;

    /**
     * 是否点击
     */
    private Boolean isClicked;

    /**
     * 是否使用
     */
    private Boolean isUsed;

    /**
     * 使用时长（秒）
     */
    private Integer useDuration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Boolean deleted;
}
