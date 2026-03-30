package com.im.backend.modules.merchant.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 商户运营配置请求DTO
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商户运营配置请求")
public class MerchantOperationConfigRequest {

    @NotNull(message = "商户ID不能为空")
    @Schema(description = "商户ID", required = true)
    private Long merchantId;

    @NotBlank(message = "配置名称不能为空")
    @Schema(description = "配置名称", required = true)
    private String configName;

    @Schema(description = "智能提醒开关", example = "true")
    private Boolean smartReminderEnabled;

    @Schema(description = "自动营销开关", example = "true")
    private Boolean autoMarketingEnabled;

    @Schema(description = "数据分析开关", example = "true")
    private Boolean dataAnalysisEnabled;

    @Schema(description = "智能客服开关", example = "true")
    private Boolean smartServiceEnabled;

    @Min(value = 0, message = "营业时间提醒不能为负数")
    @Schema(description = "营业提醒时间(小时)", example = "9")
    private Integer businessReminderHour;

    @Min(value = 1, message = "库存阈值至少为1")
    @Schema(description = "低库存阈值", example = "10")
    private Integer lowStockThreshold;

    @Schema(description = "预警销售额阈值", example = "500.00")
    private BigDecimal lowSalesThreshold;

    @Schema(description = "自动营销触发条件(JSON格式)")
    private String autoMarketingRules;
}
