package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户积分账户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_point_account")
public class UserPointAccount extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前积分余额
     */
    private Integer balance;

    /**
     * 累计获得积分
     */
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    private Integer totalUsed;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 连续签到天数
     */
    private Integer streakDays;

    /**
     * 最长连续签到记录
     */
    private Integer maxStreakDays;

    /**
     * 累计签到天数
     */
    private Integer totalCheckinDays;

    /**
     * 最后签到日期
     */
    private String lastCheckinDate;

    /**
     * 最后签到时间
     */
    private LocalDateTime lastCheckinTime;

    /**
     * 版本号(乐观锁)
     */
    @Version
    private Integer version;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
