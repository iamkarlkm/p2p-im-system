package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务人员技能关联实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("staff_service_skill")
public class StaffServiceSkill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 员工ID
     */
    private Long staffId;

    /**
     * 服务项目ID
     */
    private Long serviceId;

    /**
     * 技能熟练度(1-5)
     */
    private Integer proficiency;

    /**
     * 服务次数
     */
    private Integer serviceCount;

    /**
     * 平均服务时长(分钟)
     */
    private Integer avgDuration;

    /**
     * 是否启用
     */
    private Boolean enabled;

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
     * 服务名称
     */
    @TableField(exist = false)
    private String serviceName;

    /**
     * 服务图片
     */
    @TableField(exist = false)
    private String serviceImage;

    /**
     * 获取熟练度描述
     */
    public String getProficiencyDesc() {
        if (this.proficiency == null) return "未知";
        switch (this.proficiency) {
            case 1: return "入门";
            case 2: return "初级";
            case 3: return "中级";
            case 4: return "高级";
            case 5: return "专家";
            default: return "未知";
        }
    }
}
