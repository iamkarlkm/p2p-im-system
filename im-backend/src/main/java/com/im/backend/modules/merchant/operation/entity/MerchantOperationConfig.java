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
 * 商户运营助手配置实体
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_operation_config")
public class MerchantOperationConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 智能提醒开关
     */
    private Boolean smartReminderEnabled;

    /**
     * 自动营销开关
     */
    private Boolean autoMarketingEnabled;

    /**
     * 数据分析开关
     */
    private Boolean dataAnalysisEnabled;

    /**
     * 智能客服开关
     */
    private Boolean smartServiceEnabled;

    /**
     * 营业提醒时间(小时)
     */
    private Integer businessReminderHour;

    /**
     * 低库存阈值
     */
    private Integer lowStockThreshold;

    /**
     * 预警销售额阈值
     */
    private Double lowSalesThreshold;

    /**
     * 自动营销触发条件(JSON)
     */
    private String autoMarketingRules;

    /**
     * 配置状态: 0-禁用, 1-启用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 删除标记
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
