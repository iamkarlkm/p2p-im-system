package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 签到活动实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_activity")
public class CheckinActivity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动类型: GLOBAL-全局, MERCHANT-商户
     */
    private String activityType;

    /**
     * 商户ID(商户活动必填)
     */
    private Long merchantId;

    /**
     * 适用POI类型
     */
    private String poiTypes;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 积分倍数
     */
    private Double pointMultiplier;

    /**
     * 额外积分奖励
     */
    private Integer bonusPoints;

    /**
     * 每日限额
     */
    private Integer dailyLimit;

    /**
     * 总限额
     */
    private Integer totalLimit;

    /**
     * 已参与人数
     */
    private Integer participantCount;

    /**
     * 活动状态: ACTIVE-进行中, ENDED-已结束
     */
    private String status;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
