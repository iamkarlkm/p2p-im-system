package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约服务项目明细实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("appointment_item")
public class AppointmentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 预约ID
     */
    private Long appointmentId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 服务名称(快照)
     */
    private String serviceName;

    /**
     * 服务图片(快照)
     */
    private String serviceImage;

    /**
     * 服务时长(分钟)
     */
    private Integer duration;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    private BigDecimal subtotal;

    /**
     * 服务人员ID
     */
    private Long staffId;

    /**
     * 服务人员名称(快照)
     */
    private String staffName;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

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
     * 服务项目详情
     */
    @TableField(exist = false)
    private MerchantService serviceDetail;

    /**
     * 计算小计金额
     */
    public BigDecimal calculateSubtotal() {
        if (this.unitPrice == null || this.quantity == null) {
            return BigDecimal.ZERO;
        }
        return this.unitPrice.multiply(new BigDecimal(this.quantity));
    }
}
