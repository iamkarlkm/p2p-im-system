package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 配送骑手实体
 * 骑手信息、状态与绩效
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("delivery_rider")
public class DeliveryRider {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 骑手编号
     */
    private String riderNo;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 工作状态: OFFLINE-离线, IDLE-空闲, BUSY-忙碌, REST-休息
     */
    private String workStatus;

    /**
     * 当前经度
     */
    private Double currentLongitude;

    /**
     * 当前纬度
     */
    private Double currentLatitude;

    /**
     * 位置更新时间
     */
    private LocalDateTime locationUpdateTime;

    /**
     * 今日接单数
     */
    private Integer todayOrderCount;

    /**
     * 今日完成数
     */
    private Integer todayCompletedCount;

    /**
     * 总接单数
     */
    private Integer totalOrderCount;

    /**
     * 总完成数
     */
    private Integer totalCompletedCount;

    /**
     * 准时率(%)
     */
    private BigDecimal onTimeRate;

    /**
     * 评分(0-5)
     */
    private BigDecimal rating;

    /**
     * 配送范围(米)
     */
    private Integer deliveryRange;

    /**
     * 账户状态: ACTIVE-正常, SUSPENDED-暂停, BLOCKED-封禁
     */
    private String accountStatus;

    /**
     * 审核状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝
     */
    private String auditStatus;

    /**
     * 审核时间
     */
    private LocalDateTime auditedTime;

    /**
     * 头像URL
     */
    private String avatarUrl;

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
     * 是否删除
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;

    /**
     * 获取工作状态文本
     */
    public String getWorkStatusText() {
        switch (workStatus) {
            case "OFFLINE": return "离线";
            case "IDLE": return "空闲";
            case "BUSY": return "忙碌";
            case "REST": return "休息";
            default: return "未知";
        }
    }

    /**
     * 是否可以接单
     */
    public boolean canTakeOrder() {
        return "IDLE".equals(workStatus) && "ACTIVE".equals(accountStatus) && "APPROVED".equals(auditStatus);
    }
}
