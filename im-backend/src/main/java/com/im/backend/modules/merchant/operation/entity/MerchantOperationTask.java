package com.im.backend.modules.merchant.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商户运营任务实体
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_operation_task")
public class MerchantOperationTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 任务类型: 1-营业提醒, 2-库存预警, 3-营销活动, 4-客户回访, 5-数据分析
     */
    private Integer taskType;

    /**
     * 任务标题
     */
    private String taskTitle;

    /**
     * 任务描述
     */
    private String taskDescription;

    /**
     * 优先级: 1-低, 2-中, 3-高, 4-紧急
     */
    private Integer priority;

    /**
     * 任务状态: 0-待处理, 1-处理中, 2-已完成, 3-已忽略
     */
    private Integer status;

    /**
     * 触发条件(JSON)
     */
    private String triggerCondition;

    /**
     * 建议操作
     */
    private String suggestedAction;

    /**
     * 操作链接
     */
    private String actionUrl;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime executedTime;

    /**
     * 执行结果
     */
    private String executeResult;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
