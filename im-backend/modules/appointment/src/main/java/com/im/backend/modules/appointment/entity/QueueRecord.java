package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 排队取号记录实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("queue_record")
public class QueueRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 队列ID
     */
    private Long queueId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 排队号码
     */
    private Integer queueNumber;

    /**
     * 排队类型
     * 1-现场取号, 2-在线取号, 3-预约取号
     */
    private Integer takeType;

    /**
     * 状态
     * 0-等待中, 1-叫号中, 2-已确认, 3-服务中, 4-已完成, 5-已过号, 6-已取消
     */
    private Integer status;

    /**
     * 前面等待人数
     */
    private Integer waitingCount;

    /**
     * 预估等待时间(分钟)
     */
    private Integer estimatedWaitMinutes;

    /**
     * 取号时间
     */
    private LocalDateTime takeTime;

    /**
     * 叫号时间
     */
    private LocalDateTime callTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 服务开始时间
     */
    private LocalDateTime serviceStartTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 过号时间
     */
    private LocalDateTime passTime;

    /**
     * 过号原因
     */
    private String passReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预约ID(如果是预约取号)
     */
    private Long appointmentId;

    /**
     * 排队日期
     */
    private LocalDate queueDate;

    /**
     * 服务窗口号
     */
    private Integer serviceWindow;

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
     * 队列信息
     */
    @TableField(exist = false)
    private QueueInfo queueInfo;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 取号类型名称
     */
    @TableField(exist = false)
    private String takeTypeName;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (this.status == null) return "未知";
        switch (this.status) {
            case 0: return "等待中";
            case 1: return "叫号中";
            case 2: return "已确认";
            case 3: return "服务中";
            case 4: return "已完成";
            case 5: return "已过号";
            case 6: return "已取消";
            default: return "未知";
        }
    }

    /**
     * 获取取号类型名称
     */
    public String getTakeTypeName() {
        if (this.takeType == null) return "未知";
        switch (this.takeType) {
            case 1: return "现场取号";
            case 2: return "在线取号";
            case 3: return "预约取号";
            default: return "未知";
        }
    }

    /**
     * 是否等待中
     */
    public boolean isWaiting() {
        return this.status != null && (this.status == 0 || this.status == 1);
    }

    /**
     * 是否已过号
     */
    public boolean isPassed() {
        return this.status != null && this.status == 5;
    }

    /**
     * 是否可以重新取号(过号后)
     */
    public boolean canRequeue() {
        return isPassed() && this.passTime != null 
            && this.passTime.plusMinutes(10).isAfter(LocalDateTime.now());
    }

    /**
     * 获取等待时长(分钟)
     */
    public long getWaitingDurationMinutes() {
        if (this.takeTime == null) {
            return 0;
        }
        LocalDateTime end = this.callTime != null ? this.callTime : LocalDateTime.now();
        return java.time.Duration.between(this.takeTime, end).toMinutes();
    }

    /**
     * 取号类型枚举
     */
    public enum TakeType {
        ONSITE(1, "现场取号"),
        ONLINE(2, "在线取号"),
        APPOINTMENT(3, "预约取号");

        private final int code;
        private final String desc;

        TakeType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static TakeType fromCode(Integer code) {
            if (code == null) return null;
            for (TakeType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 状态枚举
     */
    public enum Status {
        WAITING(0, "等待中"),
        CALLING(1, "叫号中"),
        CONFIRMED(2, "已确认"),
        IN_SERVICE(3, "服务中"),
        COMPLETED(4, "已完成"),
        PASSED(5, "已过号"),
        CANCELLED(6, "已取消");

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
