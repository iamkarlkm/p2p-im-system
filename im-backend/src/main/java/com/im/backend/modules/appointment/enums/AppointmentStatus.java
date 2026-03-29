package com.im.backend.modules.appointment.enums;

import lombok.Getter;

/**
 * 预约状态枚举
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum AppointmentStatus {

    /** 待确认 - 预约提交等待商户确认 */
    PENDING("PENDING", "待确认", "预约已提交，等待商户确认"),
    
    /** 已确认 - 商户已确认预约 */
    CONFIRMED("CONFIRMED", "已确认", "预约已确认，请准时到店"),
    
    /** 服务中 - 客户已到店，正在服务 */
    IN_SERVICE("IN_SERVICE", "服务中", "正在为您提供服务"),
    
    /** 已完成 - 服务已完成 */
    COMPLETED("COMPLETED", "已完成", "服务已完成，感谢您的光临"),
    
    /** 已取消 - 预约已取消 */
    CANCELLED("CANCELLED", "已取消", "预约已取消"),
    
    /** 爽约 - 客户未到店 */
    NO_SHOW("NO_SHOW", "爽约", "您未按时到店，已标记为爽约");

    private final String code;
    private final String label;
    private final String description;

    AppointmentStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static AppointmentStatus fromCode(String code) {
        for (AppointmentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 是否可以取消
     */
    public boolean canCancel() {
        return this == PENDING || this == CONFIRMED;
    }

    /**
     * 是否可以修改
     */
    public boolean canModify() {
        return this == PENDING;
    }

    /**
     * 是否是终态
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    /**
     * 是否需要提醒
     */
    public boolean needRemind() {
        return this == CONFIRMED;
    }
}
