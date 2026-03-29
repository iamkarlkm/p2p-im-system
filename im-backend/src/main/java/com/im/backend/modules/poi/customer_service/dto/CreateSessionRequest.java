package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 创建客服会话请求
 */
@Data
public class CreateSessionRequest {

    /**
     * POI商户ID
     */
    @NotNull(message = "POI商户ID不能为空")
    private Long poiId;

    /**
     * 会话来源
     */
    private String source;

    /**
     * 咨询类型
     */
    private String inquiryType;

    /**
     * 关联订单ID
     */
    private Long relatedOrderId;

    /**
     * 会话主题
     */
    private String subject;

    /**
     * 用户首次消息
     */
    private String firstMessage;

    /**
     * 是否优先分配机器人
     */
    private Boolean preferRobot;
}
