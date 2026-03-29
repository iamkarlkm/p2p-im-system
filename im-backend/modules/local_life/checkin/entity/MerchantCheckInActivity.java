package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商户签到活动实体
 */
@Data
@TableName("merchant_checkin_activity")
public class MerchantCheckinActivity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * POI ID
     */
    private String poiId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动类型: DOUBLE_POINTS-双倍积分, BONUS-额外奖励, GIFT-到店礼品
     */
    private String activityType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 每日限额
     */
    private Integer dailyLimit;

    /**
     * 奖励积分
     */
    private Integer bonusPoints;

    /**
     * 活动状态: ACTIVE-进行中, PAUSED-暂停, ENDED-已结束
     */
    private String status;

    /**
     * 活动规则描述
     */
    private String rules;

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
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}
