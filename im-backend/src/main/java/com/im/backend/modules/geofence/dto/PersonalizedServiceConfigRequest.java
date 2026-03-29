package com.im.backend.modules.geofence.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 配置个性化服务请求
 */
@Data
public class PersonalizedServiceConfigRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    private Long storeId;

    /** 欢迎消息模板 */
    private String welcomeMessageTemplate;

    /** 新客优惠券ID */
    private Long newCustomerCouponId;

    /** 老客优惠券ID */
    private Long oldCustomerCouponId;

    /** VIP客户优惠券ID */
    private Long vipCustomerCouponId;

    /** 是否启用优先接待提醒 */
    private Boolean enablePriorityAlert;

    /** 优先接待会员等级阈值 */
    private String priorityMemberLevel;
}
