package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户成长记录实体
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_user_growth_record")
public class UserGrowthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 行为类型
     */
    private String actionType;

    /**
     * 获得经验值
     */
    private Integer gainedExp;

    /**
     * 行为前值
     */
    private Long expBefore;

    /**
     * 行为后值
     */
    private Long expAfter;

    /**
     * 是否升级
     */
    private Boolean levelUp;

    /**
     * 旧等级
     */
    private Integer oldLevel;

    /**
     * 新等级
     */
    private Integer newLevel;

    /**
     * 关联POI ID
     */
    private String poiId;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
