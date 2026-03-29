package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 连续签到记录实体
 */
@Data
@TableName("check_in_streak")
public class CheckInStreak {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前连续签到天数
     */
    private Integer currentStreak;

    /**
     * 历史最高连续天数
     */
    private Integer maxStreak;

    /**
     * 本月签到天数
     */
    private Integer monthCheckInCount;

    /**
     * 本年签到天数
     */
    private Integer yearCheckInCount;

    /**
     * 累计签到天数
     */
    private Integer totalCheckInCount;

    /**
     * 上次签到日期
     */
    private LocalDateTime lastCheckInDate;

    /**
     * 连续签到开始日期
     */
    private LocalDateTime streakStartDate;

    /**
     * 本月首次签到日期
     */
    private LocalDateTime monthFirstCheckIn;

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
}
