package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 排队叫号队列实体
 * 本地生活服务预约与排班管理系统的核心实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("queue_info")
public class QueueInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 队列ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 队列编号
     */
    private String queueCode;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 队列类型
     * 1-普通队列, 2-VIP队列, 3-预约队列, 4-快速通道
     */
    private Integer queueType;

    /**
     * 服务类型ID
     */
    private Long serviceTypeId;

    /**
     * 服务类型名称
     */
    private String serviceTypeName;

    /**
     * 队列状态
     * 0-暂停, 1-正常, 2-已满
     */
    private Integer status;

    /**
     * 当前排队人数
     */
    private Integer currentQueueCount;

    /**
     * 当前叫号
     */
    private Integer currentNumber;

    /**
     * 队列最大容量
     */
    private Integer maxCapacity;

    /**
     * 预估服务时长(分钟)
     */
    private Integer estimatedServiceTime;

    /**
     * 平均等待时间(分钟)
     */
    private Integer averageWaitTime;

    /**
     * 工作窗口/服务工位数量
     */
    private Integer serviceWindowCount;

    /**
     * 营业开始时间
     */
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    private LocalTime businessEndTime;

    /**
     * 是否启用在线取号
     */
    private Boolean enableOnlineQueue;

    /**
     * 在线取号提前量(分钟)
     */
    private Integer onlineQueueAdvanceMinutes;

    /**
     * 是否启用预约优先
     */
    private Boolean enableAppointmentPriority;

    /**
     * 预约优先阈值(提前多少分钟)
     */
    private Integer appointmentPriorityThreshold;

    /**
     * 今日日期
     */
    private LocalDate queueDate;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ========== 扩展字段 ==========

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 队列类型名称
     */
    @TableField(exist = false)
    private String queueTypeName;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (this.status == null) return "未知";
        switch (this.status) {
            case 0: return "暂停";
            case 1: return "正常";
            case 2: return "已满";
            default: return "未知";
        }
    }

    /**
     * 获取队列类型名称
     */
    public String getQueueTypeName() {
        if (this.queueType == null) return "未知";
        switch (this.queueType) {
            case 1: return "普通队列";
            case 2: return "VIP队列";
            case 3: return "预约队列";
            case 4: return "快速通道";
            default: return "未知";
        }
    }

    /**
     * 是否已满
     */
    public boolean isFull() {
        return this.currentQueueCount != null && this.maxCapacity != null 
            && this.currentQueueCount >= this.maxCapacity;
    }

    /**
     * 是否可以取号
     */
    public boolean canTakeNumber() {
        if (this.status == null || this.status != 1) {
            return false;
        }
        return !isFull() && isInBusinessHours();
    }

    /**
     * 是否在营业时间内
     */
    public boolean isInBusinessHours() {
        if (this.businessStartTime == null || this.businessEndTime == null) {
            return true;
        }
        LocalTime now = LocalTime.now();
        return !now.isBefore(this.businessStartTime) && !now.isAfter(this.businessEndTime);
    }

    /**
     * 队列类型枚举
     */
    public enum QueueType {
        NORMAL(1, "普通队列"),
        VIP(2, "VIP队列"),
        APPOINTMENT(3, "预约队列"),
        EXPRESS(4, "快速通道");

        private final int code;
        private final String desc;

        QueueType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static QueueType fromCode(Integer code) {
            if (code == null) return null;
            for (QueueType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 队列状态枚举
     */
    public enum Status {
        PAUSED(0, "暂停"),
        NORMAL(1, "正常"),
        FULL(2, "已满");

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static Status fromCode(Integer code) {
            if (code == null) return null;
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }
    }
}
