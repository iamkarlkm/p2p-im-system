package com.im.backend.modules.poi.customer_service.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 客服转接记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("poi_cs_transfer_record")
public class PoiCustomerServiceTransfer {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原会话ID
     */
    private String fromSessionId;

    /**
     * 目标会话ID
     */
    private String toSessionId;

    /**
     * 转接类型: MANUAL-手动转接, AUTO-自动转接, ROBOT_TO_HUMAN-机器人转人工
     */
    private String transferType;

    /**
     * 原客服ID
     */
    private Long fromAgentId;

    /**
     * 目标客服ID
     */
    private Long toAgentId;

    /**
     * 转接原因: CAPACITY-容量不足, SKILL-技能不匹配, USER_REQUEST-用户要求, 
     * ROBOT_CANNOT_ANSWER-机器人无法回答, SHIFT_END-交接班
     */
    private String reason;

    /**
     * 转接说明
     */
    private String remark;

    /**
     * 操作人ID(系统转接为0)
     */
    private Long operatorId;

    /**
     * 转接时间
     */
    private LocalDateTime transferTime;

    /**
     * 用户是否感知到转接
     */
    private Boolean userNoticed;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
