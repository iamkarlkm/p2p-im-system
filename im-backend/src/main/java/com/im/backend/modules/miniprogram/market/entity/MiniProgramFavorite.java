package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户小程序收藏实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_favorite")
public class MiniProgramFavorite {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 小程序ID
     */
    private Long appId;

    /**
     * 收藏夹分组ID（0为默认分组）
     */
    private Long groupId;

    /**
     * 备注名称
     */
    private String remarkName;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUseTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
