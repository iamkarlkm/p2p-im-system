package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 发起拼团请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBuyDTO {
    
    /**
     * 活动ID
     */
    @NotBlank(message = "活动ID不能为空")
    private String activityId;
    
    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String productId;
    
    /**
     * 商品规格
     */
    private String productSpec;
    
    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;
    
    /**
     * 团长留言
     */
    private String leaderMessage;
    
    /**
     * 是否匿名开团
     */
    private Boolean anonymous;
    
    /**
     * 收货地址ID
     */
    @NotBlank(message = "收货地址不能为空")
    private String addressId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 使用的优惠券ID
     */
    private String couponId;
    
    /**
     * 应付金额（校验用）
     */
    @NotNull(message = "应付金额不能为空")
    private BigDecimal payAmount;
}
