package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 服务资源实体类
 * 管理商户的服务工位、包间、设备等资源
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_service_resource")
public class ServiceResource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 资源编号 */
    @TableField("resource_no")
    private String resourceNo;

    /** 商户ID */
    @TableField("merchant_id")
    private Long merchantId;

    /** 资源名称 */
    @TableField("name")
    private String name;

    /** 资源类型: ROOM-包间, TABLE-餐桌, SEAT-座位, EQUIPMENT-设备, STATION-工位, WINDOW-窗口 */
    @TableField("type")
    private String type;

    /** 资源子类型 */
    @TableField("sub_type")
    private String subType;

    /** 资源状态: AVAILABLE-可用, IN_USE-使用中, MAINTENANCE-维护中, RESERVED-已预留, DISABLED-已禁用 */
    @TableField("status")
    private String status;

    /** 可容纳人数 */
    @TableField("capacity")
    private Integer capacity;

    /** 最少容纳人数 */
    @TableField("min_capacity")
    private Integer minCapacity;

    /** 位置描述 */
    @TableField("location")
    private String location;

    /** 楼层 */
    @TableField("floor")
    private String floor;

    /** 区域 */
    @TableField("area")
    private String area;

    /** 图片URL */
    @TableField("image_url")
    private String imageUrl;

    /** 资源描述 */
    @TableField("description")
    private String description;

    /** 设施配置（JSON数组） */
    @TableField("facilities")
    private String facilities;

    /** 适用服务类型ID列表（逗号分隔） */
    @TableField("service_types")
    private String serviceTypes;

    /** 最低消费 */
    @TableField("min_consumption")
    private java.math.BigDecimal minConsumption;

    /** 预订押金 */
    @TableField("deposit")
    private java.math.BigDecimal deposit;

    /** 每小时价格 */
    @TableField("hourly_price")
    private java.math.BigDecimal hourlyPrice;

    /** 排序号 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 是否支持预约 */
    @TableField("support_appointment")
    private Boolean supportAppointment;

    /** 是否支持排队 */
    @TableField("support_queue")
    private Boolean supportQueue;

    /** 是否VIP专属 */
    @TableField("vip_only")
    private Boolean vipOnly;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 是否删除 */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    // ==================== 业务方法 ====================

    /**
     * 生成资源编号
     */
    public void generateResourceNo() {
        String prefix = type != null ? type.substring(0, 1).toUpperCase() : "R";
        this.resourceNo = prefix + System.currentTimeMillis();
    }

    /**
     * 检查是否可用
     */
    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    /**
     * 获取设施列表
     */
    public List<String> getFacilityList() {
        if (facilities != null && !facilities.isEmpty()) {
            return java.util.Arrays.asList(facilities.split(","));
        }
        return new java.util.ArrayList<>();
    }

    /**
     * 检查是否支持指定服务类型
     */
    public boolean supportsServiceType(Long serviceTypeId) {
        if (serviceTypes == null || serviceTypes.isEmpty()) {
            return true; // 默认支持所有
        }
        return serviceTypes.contains(serviceTypeId.toString());
    }

    /**
     * 占用资源
     */
    public void occupy() {
        if ("AVAILABLE".equals(status)) {
            this.status = "IN_USE";
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if ("IN_USE".equals(status) || "RESERVED".equals(status)) {
            this.status = "AVAILABLE";
        }
    }

    /**
     * 检查容量是否匹配
     */
    public boolean matchesCapacity(int peopleCount) {
        if (minCapacity != null && peopleCount < minCapacity) {
            return false;
        }
        if (capacity != null && peopleCount > capacity) {
            return false;
        }
        return true;
    }
}
