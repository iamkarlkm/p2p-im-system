package com.im.backend.enums;

import lombok.Getter;

/**
 * 摘要状态枚举
 */
@Getter
public enum SummaryStatus {
    
    /**
     * 待处理 - 摘要生成任务已创建，等待处理
     */
    PENDING("PENDING", "待处理"),
    
    /**
     * 处理中 - 摘要正在生成中
     */
    PROCESSING("PROCESSING", "处理中"),
    
    /**
     * 已完成 - 摘要生成成功
     */
    COMPLETED("COMPLETED", "已完成"),
    
    /**
     * 失败 - 摘要生成失败
     */
    FAILED("FAILED", "失败"),
    
    /**
     * 已过期 - 摘要已过期（缓存失效）
     */
    EXPIRED("EXPIRED", "已过期"),
    
    /**
     * 已删除 - 摘要已被逻辑删除
     */
    DELETED("DELETED", "已删除"),
    
    /**
     * 已取消 - 摘要生成任务已取消
     */
    CANCELLED("CANCELLED", "已取消"),
    
    /**
     * 需要重新生成 - 摘要质量低，需要重新生成
     */
    NEEDS_REGEN("NEEDS_REGEN", "需要重新生成");

    private final String code;
    private final String description;

    SummaryStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static SummaryStatus fromCode(String code) {
        for (SummaryStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的摘要状态: " + code);
    }

    /**
     * 检查状态是否为活动状态（可操作状态）
     */
    public boolean isActive() {
        return this == PENDING || this == PROCESSING || this == COMPLETED || this == NEEDS_REGEN;
    }

    /**
     * 检查状态是否为最终状态（不可再更改）
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == EXPIRED || this == DELETED || this == CANCELLED;
    }

    /**
     * 检查状态是否可重新生成
     */
    public boolean canRegenerate() {
        return this == COMPLETED || this == FAILED || this == NEEDS_REGEN;
    }

    /**
     * 获取所有活动状态的枚举
     */
    public static SummaryStatus[] getActiveStatuses() {
        return new SummaryStatus[] {PENDING, PROCESSING, COMPLETED, NEEDS_REGEN};
    }

    /**
     * 获取所有最终状态的枚举
     */
    public static SummaryStatus[] getFinalStatuses() {
        return new SummaryStatus[] {COMPLETED, FAILED, EXPIRED, DELETED, CANCELLED};
    }
}