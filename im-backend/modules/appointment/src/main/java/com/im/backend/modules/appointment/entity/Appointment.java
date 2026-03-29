package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 服务预约实体
 * 本地生活服务预约与排班管理系统的核心实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 服务人员ID
     */
    private Long staffId;

    /**
     * 预约单号 (唯一业务单号)
     */
    private String appointmentNo;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 预约开始时间
     */
    private LocalTime startTime;

    /**
     * 预约结束时间
     */
    private LocalTime endTime;

    /**
     * 服务时长(分钟)
     */
    private Integer duration;

    /**
     * 预约状态
     * 0-待确认, 1-已确认, 2-已到店, 3-服务中, 4-已完成, 5-已取消, 6-已爽约
     */
    private Integer status;

    /**
     * 预约来源
     * 1-APP, 2-小程序, 3-H5, 4-电话, 5-到店预约
     */
    private Integer source;

    /**
     * 预约人数
     */
    private Integer peopleCount;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人备注
     */
    private String contactRemark;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 服务价格
     */
    private BigDecimal servicePrice;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    /**
     * 定金支付状态
     * 0-未支付, 1-已支付, 2-已退还, 3-已扣除
     */
    private Integer depositStatus;

    /**
     * 实际支付金额
     */
    private BigDecimal actualAmount;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消人 (0-用户, 1-商户, 2-系统)
     */
    private Integer cancelBy;

    /**
     * 到店时间
     */
    private LocalDateTime arriveTime;

    /**
     * 服务开始时间
     */
    private LocalDateTime serviceStartTime;

    /**
     * 服务完成时间
     */
    private LocalDateTime serviceEndTime;

    /**
     * 提醒状态
     * 0-未提醒, 1-已发送预约提醒, 2-已发送到店提醒
     */
    private Integer remindStatus;

    /**
     * 提醒发送时间
     */
    private LocalDateTime remindTime;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String reviewContent;

    /**
     * 评价时间
     */
    private LocalDateTime reviewTime;

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

    /**
     * 版本号(乐观锁)
     */
    @Version
    private Integer version;

    // ========== 扩展字段(非数据库字段) ==========

    /**
     * 商户名称
     */
    @TableField(exist = false)
    private String merchantName;

    /**
     * 门店名称
     */
    @TableField(exist = false)
    private String storeName;

    /**
     * 服务名称
     */
    @TableField(exist = false)
    private String serviceName;

    /**
     * 服务人员名称
     */
    @TableField(exist = false)
    private String staffName;

    /**
     * 服务人员头像
     */
    @TableField(exist = false)
    private String staffAvatar;

    /**
     * 服务图片
     */
    @TableField(exist = false)
    private String serviceImage;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 预约服务项目列表
     */
    @TableField(exist = false)
    private List<AppointmentItem> items;

    // ========== 业务方法 ==========

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        switch (this.status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已到店";
            case 3: return "服务中";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "已爽约";
            default: return "未知";
        }
    }

    /**
     * 是否可取消
     */
    public boolean canCancel() {
        return this.status != null && (this.status == 0 || this.status == 1);
    }

    /**
     * 是否可改期
     */
    public boolean canReschedule() {
        return this.status != null && (this.status == 0 || this.status == 1);
    }

    /**
     * 是否可评价
     */
    public boolean canReview() {
        return this.status != null && this.status == 4 && this.rating == null;
    }

    /**
     * 检查是否爽约
     * 预约时间已过且状态仍为"已确认"则认为爽约
     */
    public boolean isNoShow() {
        if (this.status == null || this.status != 1) {
            return false;
        }
        LocalDateTime appointmentDateTime = LocalDateTime.of(this.appointmentDate, this.endTime);
        return LocalDateTime.now().isAfter(appointmentDateTime.plusMinutes(30));
    }

    /**
     * 获取预约完整时间字符串
     */
    public String getAppointmentTimeStr() {
        if (this.appointmentDate == null || this.startTime == null || this.endTime == null) {
            return "";
        }
        return String.format("%s %s-%s", 
            this.appointmentDate.toString(),
            this.startTime.toString(),
            this.endTime.toString()
        );
    }

    /**
     * 计算距离预约开始还有多久(分钟)
     */
    public long getMinutesUntilAppointment() {
        if (this.appointmentDate == null || this.startTime == null) {
            return -1;
        }
        LocalDateTime appointmentTime = LocalDateTime.of(this.appointmentDate, this.startTime);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(appointmentTime)) {
            return -1;
        }
        return java.time.Duration.between(now, appointmentTime).toMinutes();
    }

    /**
     * 是否需要发送预约提醒(提前1小时)
     */
    public boolean needSendReminder() {
        long minutes = getMinutesUntilAppointment();
        return minutes >= 50 && minutes <= 70 && this.remindStatus != null && this.remindStatus == 0;
    }

    /**
     * 获取预约状态枚举
     */
    public AppointmentStatus getStatusEnum() {
        return AppointmentStatus.fromCode(this.status);
    }

    /**
     * 预约状态枚举
     */
    public enum AppointmentStatus {
        PENDING(0, "待确认"),
        CONFIRMED(1, "已确认"),
        ARRIVED(2, "已到店"),
        IN_SERVICE(3, "服务中"),
        COMPLETED(4, "已完成"),
        CANCELLED(5, "已取消"),
        NO_SHOW(6, "已爽约");

        private final int code;
        private final String desc;

        AppointmentStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static AppointmentStatus fromCode(Integer code) {
            if (code == null) return null;
            for (AppointmentStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 预约来源枚举
     */
    public enum Source {
        APP(1, "APP"),
        MINI_PROGRAM(2, "小程序"),
        H5(3, "H5"),
        PHONE(4, "电话"),
        OFFLINE(5, "到店预约");

        private final int code;
        private final String desc;

        Source(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static Source fromCode(Integer code) {
            if (code == null) return null;
            for (Source source : values()) {
                if (source.code == code) {
                    return source;
                }
            }
            return null;
        }
    }

    /**
     * 定金支付状态枚举
     */
    public enum DepositStatus {
        UNPAID(0, "未支付"),
        PAID(1, "已支付"),
        REFUNDED(2, "已退还"),
        DEDUCTED(3, "已扣除");

        private final int code;
        private final String desc;

        DepositStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static DepositStatus fromCode(Integer code) {
            if (code == null) return null;
            for (DepositStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }
    }
}
