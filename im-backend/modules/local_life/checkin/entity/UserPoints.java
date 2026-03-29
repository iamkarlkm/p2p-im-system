package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户积分账户实体
 */
@Data
@TableName("user_points")
public class UserPoints {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前可用积分
     */
    private Integer availablePoints;

    /**
     * 累计获得积分
     */
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    private Integer totalSpent;

    /**
     * 用户等级: BRONZE-青铜, SILVER-白银, GOLD-黄金, PLATINUM-铂金, DIAMOND-钻石
     */
    private String userLevel;

    /**
     * 等级积分(用于计算等级)
     */
    private Integer levelPoints;

    /**
     * 今日已获积分
     */
    private Integer todayEarned;

    /**
     * 今日签到次数
     */
    private Integer todayCheckInCount;

    /**
     * 最后签到日期
     */
    private LocalDateTime lastCheckInDate;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 版本号(乐观锁)
     */
    @Version
    private Integer version;
}
